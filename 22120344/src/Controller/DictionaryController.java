package Controller;

import Models.DictionaryManager;
import Models.FileHandler;
import View.DictionaryView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DictionaryController {
    private DictionaryManager dictionaryManager;
    private DictionaryView view;
    private Set<String> favorites;
    private JFrame favoritesFrame;
    private boolean isFavoritesFrameVisible = false;
    private JFrame wordMeaningFrame = null;

    public DictionaryController(DictionaryManager dictionaryManager, DictionaryView view) {
        this.dictionaryManager = dictionaryManager;
        this.view = view;
        this.favorites = FileHandler.loadFavorites();
        initializeListeners();
    }

    private void initializeListeners() {
        view.getSearchButton().addActionListener(e -> handleSearch());
        view.getEnglishToVietnameseButton().addActionListener(e -> switchLanguage(true));
        view.getVietnameseToEnglishButton().addActionListener(e -> switchLanguage(false));
        view.getAddButton().addActionListener(e -> handleAddWord());
        view.getDeleteButton().addActionListener(e -> handleDeleteWord());
        view.getFavoriteButton().addActionListener(e -> handleFavorite());
        view.getShowFavoritesButton().addActionListener(e -> showFavoritesFrame());
        view.getShowStatisticsButton().addActionListener(e -> showStatisticsFrame());
    }

    private void handleSearch() {
        String searchTerm = view.getSearchField().getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            String meaning = dictionaryManager.searchWord(searchTerm);
            if (meaning != null) {
                boolean isFavorite = favorites.contains(searchTerm);
                view.getFavoriteButton().setText(isFavorite ? "Unfavorite" : "Favorite");
                view.getResultArea().setText("Word: " + searchTerm + "\nMeaning: " + meaning + "\n");
                dictionaryManager.logSearch(searchTerm);
            } else {
                view.getResultArea().setText("Word not found.");
                if (favoritesFrame != null && favoritesFrame.isVisible()) {
                    favoritesFrame.dispose();
                }
            }
        }
    }

    private void switchLanguage(boolean isEnglishToVietnamese) {
        dictionaryManager.setEnglishToVietnamese(isEnglishToVietnamese);
        JOptionPane.showMessageDialog(view.getFrame(),
                "Language switched to " + (isEnglishToVietnamese ? "English to Vietnamese" : "Vietnamese to English"));
    }

    private void handleAddWord() {
        String word = JOptionPane.showInputDialog(view.getFrame(), "Enter word:");
        if (word != null) {
            if (dictionaryManager.wordExists(word.trim().toLowerCase())) {
                JOptionPane.showMessageDialog(view.getFrame(), "Word already exists in the dictionary.");
                return;
            }

            String meaning = JOptionPane.showInputDialog(view.getFrame(), "Enter meaning:");
            if (meaning != null) {
                String[] options = {"English to Vietnamese", "Vietnamese to English"};
                int choice = JOptionPane.showOptionDialog(view.getFrame(), "Select language direction:", "Add Word",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 0 || choice == 1) {
                    dictionaryManager.addWord(word.trim().toLowerCase(), meaning.trim(), choice == 0);
                    JOptionPane.showMessageDialog(view.getFrame(), "Word added successfully.");
                }
            }
        }
    }

    private void handleDeleteWord() {
        String wordToDelete = JOptionPane.showInputDialog(view.getFrame(), "Enter word to delete:");
        if (wordToDelete != null) {
            int confirmation = JOptionPane.showConfirmDialog(view.getFrame(),
                    "Are you sure you want to delete the word: " + wordToDelete + "?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                boolean isEnglishToVietnamese = true;
                boolean deletedSuccessfully = false;
                if (dictionaryManager.wordExists(wordToDelete.trim().toLowerCase())) {
                    String[] options = {"English to Vietnamese", "Vietnamese to English"};
                    int choice = JOptionPane.showOptionDialog(view.getFrame(),
                            "Select language direction for the word: " + wordToDelete, "Delete Word",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    isEnglishToVietnamese = (choice == 0);
                    dictionaryManager.deleteWord(wordToDelete.trim().toLowerCase(), isEnglishToVietnamese);
                    deletedSuccessfully = true;
                }
                if (!deletedSuccessfully) {
                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Word does not exist in the dictionary.", "Word Not Found", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleFavorite() {
        String searchTerm = view.getSearchField().getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            boolean isFavorite = favorites.contains(searchTerm);
            if (isFavorite) {
                favorites.remove(searchTerm);
                view.getFavoriteButton().setText("Favorite");
                JOptionPane.showMessageDialog(view.getFrame(), "Word removed from Favorites.");
            } else {
                favorites.add(searchTerm);
                view.getFavoriteButton().setText("Unfavorite");
                JOptionPane.showMessageDialog(view.getFrame(), "Word added to Favorites.");
            }
            FileHandler.saveFavorites(favorites);
        }
    }

    private void showFavoritesFrame() {
        if (!isFavoritesFrameVisible) {
            favoritesFrame = new JFrame("Favorites");
            favoritesFrame.setSize(500, 600);
            favoritesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Word");
            model.addColumn("Meaning");

            JTable favoritesTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(favoritesTable);

            JPanel buttonPanel = new JPanel();
            JButton sortAZButton = new JButton("Sort A-Z");
            sortAZButton.addActionListener(e -> sortFavorites(model, true));
            JButton sortZAButton = new JButton("Sort Z-A");
            sortZAButton.addActionListener(e -> sortFavorites(model, false));
            buttonPanel.add(sortAZButton);
            buttonPanel.add(sortZAButton);

            JPanel favoritesPanel = new JPanel(new BorderLayout());
            favoritesPanel.add(scrollPane, BorderLayout.CENTER);
            favoritesPanel.add(buttonPanel, BorderLayout.SOUTH);

            for (String favorite : favorites) {
                String meaning = dictionaryManager.searchWord(favorite);
                if (meaning != null) {
                    model.addRow(new Object[]{favorite, meaning});
                }
            }

            favoritesTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        int row = favoritesTable.getSelectedRow();
                        String word = (String) favoritesTable.getValueAt(row, 0);
                        String meaning = (String) favoritesTable.getValueAt(row, 1);
                        showWordMeaningDialog(word, meaning);
                    }
                }
            });

            favoritesFrame.add(favoritesPanel);
            favoritesFrame.setVisible(true);
            isFavoritesFrameVisible = true;

            favoritesFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    isFavoritesFrameVisible = false;
                }
            });
        } else {
            favoritesFrame.toFront();
        }
    }

    private void showWordMeaningDialog(String word, String meaning) {
        if (wordMeaningFrame == null) {
            wordMeaningFrame = new JFrame("Word Meaning");
            wordMeaningFrame.setSize(500, 500);
            wordMeaningFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else {
            wordMeaningFrame.getContentPane().removeAll();
        }

        JTextArea wordMeaningArea = new JTextArea();
        wordMeaningArea.setEditable(false);
        wordMeaningArea.append("Word: " + word + "\n");
        wordMeaningArea.append("Meaning: " + meaning + "\n");

        JScrollPane scrollPane = new JScrollPane(wordMeaningArea);
        wordMeaningFrame.add(scrollPane);
        wordMeaningFrame.setVisible(true);
    }

    private void sortFavorites(DefaultTableModel model, boolean ascending) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        JTable favoritesTable = new JTable(model);
        favoritesTable.setRowSorter(sorter);
    }

    private void showStatisticsFrame() {
        JFrame statisticsFrame = new JFrame("Search Statistics");
        statisticsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statisticsFrame.setSize(500, 600);

        JPanel statisticsPanel = new JPanel(new BorderLayout());

        JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
        JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");

        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);

        JButton generateButton = new JButton("Generate Statistics");
        JTextArea statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statisticsArea);

        generateButton.addActionListener(e -> {
            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);
                showStatistics(startDate, endDate, statisticsArea);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(statisticsFrame, "Invalid date format. Please use yyyy-MM-dd format.");
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(startDateLabel);
        inputPanel.add(startDateField);
        inputPanel.add(endDateLabel);
        inputPanel.add(endDateField);
        inputPanel.add(generateButton);

        statisticsPanel.add(inputPanel, BorderLayout.NORTH);
        statisticsPanel.add(scrollPane, BorderLayout.CENTER);

        statisticsFrame.add(statisticsPanel);
        statisticsFrame.setVisible(true);
    }

    private void showStatistics(Date startDate, Date endDate, JTextArea statisticsArea) {
        Map<String, Integer> wordFrequency = dictionaryManager.getWordFrequency(startDate, endDate);

        StringBuilder sb = new StringBuilder();
        sb.append("Search Statistics from ").append(startDate).append(" to ").append(endDate).append("\n");
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" times\n");
        }

        statisticsArea.setText(sb.toString());
    }
}