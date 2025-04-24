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

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import Models.DictionaryManager;
import org.w3c.dom.*;
import java.util.*;
import java.util.List;
import java.text.*;


public class DictionaryApp {
    private static final String ANH_VIET_XML = "./22120344/Data/Anh_Viet.xml";
    private static final String VIET_ANH_XML = "./22120344/Data/Viet_Anh.xml";
    private static final String FAVORITES_FILE = "./22120344/Favorites.txt";
    private static final String SEARCH_LOG_FILE = "./22120344/SearchLog.txt";

    private static DictionaryManager dictionaryManager;
    private static Set<String> favorites;
    private static JTable favoritesTable;

    public static void main(String[] args) {
        dictionaryManager = new DictionaryManager();
        favorites = loadFavorites();

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Dictionary App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 400);

        Font defaultFont = new Font("Arial", Font.PLAIN, 14);
        Color backgroundColor = new Color(240, 240, 240);


        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);


        JTextField searchField = new JTextField();
        searchField.setFont(defaultFont);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(defaultFont);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(backgroundColor);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.NORTH);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(defaultFont);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton englishToVietnameseButton = new JButton("English to Vietnamese");
        JButton vietnameseToEnglishButton = new JButton("Vietnamese to English");
        JButton addButton = new JButton("Add Word");
        JButton deleteButton = new JButton("Delete Word");
        JButton showFavoritesButton = new JButton("Show Favorites");
        JButton showStatisticsButton = new JButton("Show Statistics");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);


        resultArea.setBackground(backgroundColor);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);



        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(englishToVietnameseButton);
        buttonPanel.add(vietnameseToEnglishButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showFavoritesButton);
        buttonPanel.add(showStatisticsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);




        englishToVietnameseButton.addActionListener(e -> {
            dictionaryManager.setEnglishToVietnamese();
            JOptionPane.showMessageDialog(frame, "Language switched to English to Vietnamese.");
        });

        vietnameseToEnglishButton.addActionListener(e -> {
            dictionaryManager.setVietnameseToEnglish();
            JOptionPane.showMessageDialog(frame, "Language switched to Vietnamese to English.");
        });

        addButton.addActionListener(e -> {
            String word = JOptionPane.showInputDialog(frame, "Enter word:");
            if (word != null) {
                if (dictionaryManager.wordExists(word.trim().toLowerCase())) {
                    JOptionPane.showMessageDialog(frame, "Word already exists in the dictionary.");
                    return;
                }

                String meaning = JOptionPane.showInputDialog(frame, "Enter meaning:");
                if (meaning != null) {
                    String[] options = {"English to Vietnamese", "Vietnamese to English"};
                    int choice = JOptionPane.showOptionDialog(frame, "Select language direction:", "Add Word", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        dictionaryManager.addWord(word.trim().toLowerCase(), meaning.trim(), true);
                    } else if (choice == 1) {
                        dictionaryManager.addWord(word.trim().toLowerCase(), meaning.trim(), false);
                    }
                    JOptionPane.showMessageDialog(frame, "Word added successfully.");
                }
            }
        });

        deleteButton.addActionListener(e -> {
            String wordToDelete = JOptionPane.showInputDialog(frame, "Enter word to delete:");
            if (wordToDelete != null) {
                int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete the word: " + wordToDelete + "?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    boolean isEnglishToVietnamese = true; // Mặc định xóa từ Anh-Việt
                    boolean deletedSuccessfully = false; // Biến để kiểm tra xem từ có được xóa thành công không
                    if (dictionaryManager.wordExists(wordToDelete.trim().toLowerCase())) {
                        // Kiểm tra xem từ đó là từ tiếng Anh sang tiếng Việt hay từ tiếng Việt sang tiếng Anh
                        String meaning = dictionaryManager.searchWord(wordToDelete.trim().toLowerCase());
                        String[] options = {"English to Vietnamese", "Vietnamese to English"};
                        int choice = JOptionPane.showOptionDialog(frame, "Select language direction for the word: " + wordToDelete, "Delete Word", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                        if (choice == 0) {
                            isEnglishToVietnamese = true;
                        } else if (choice == 1) {
                            isEnglishToVietnamese = false;
                        }
                        dictionaryManager.deleteWord(wordToDelete.trim().toLowerCase(), isEnglishToVietnamese);
                        deletedSuccessfully = true; // Đánh dấu từ đã được xóa thành công
                    }
                    // Nếu từ không tồn tại hoặc không được xóa thành công, không hiển thị thông báo xóa thành công
                    if (!deletedSuccessfully) {
                        JOptionPane.showMessageDialog(frame, "Word does not exist in the dictionary.", "Word Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });



        JButton favoriteButton = new JButton("Favorite");
        buttonPanel.add(favoriteButton);

        favoriteButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (!searchTerm.isEmpty()) {
                boolean isFavorite = favorites.contains(searchTerm);
                if (isFavorite) {
                    favorites.remove(searchTerm);
                    saveFavorites(favorites);
                    favoriteButton.setText("Favorite");
                    JOptionPane.showMessageDialog(frame, "Word removed from Favorites.");
                } else {
                    favorites.add(searchTerm);
                    saveFavorites(favorites);
                    favoriteButton.setText("Unfavorite");
                    JOptionPane.showMessageDialog(frame, "Word added to Favorites.");
                }
            }
        });

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (!searchTerm.isEmpty()) {
                String meaning = dictionaryManager.searchWord(searchTerm);
                if (meaning != null) {
                    boolean isFavorite = favorites.contains(searchTerm);
                    if (isFavorite) {
                        favoriteButton.setText("Unfavorite");
                    } else {
                        favoriteButton.setText("Favorite");
                    }
                    resultArea.setText("Word: " + searchTerm + "\nMeaning: " + meaning + "\n");
                    dictionaryManager.logSearch(searchTerm); // Log the search
                } else {
                    resultArea.setText("Word not found.");
                }
            }
        });

        showFavoritesButton.addActionListener(e -> showFavoritesFrame());
        showStatisticsButton.addActionListener(e -> showStatisticsFrame());

        frame.add(panel);
        frame.setVisible(true);
    }

    private static boolean toggleFavorite(String word) {
        if (favorites.contains(word)) {
            favorites.remove(word);
            saveFavorites(favorites);
            return false; // Word is removed from favorites
        } else {
            favorites.add(word);
            saveFavorites(favorites);
            return true; // Word added to favorites
        }
    }

    private static Set<String> loadFavorites() {
        Set<String> favorites = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FAVORITES_FILE), StandardCharsets.UTF_8))) {
            String word;
            while ((word = br.readLine()) != null) {
                favorites.add(word.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return favorites;
    }

    private static boolean addToFavorites(String word) {
        if (favorites.contains(word)) {
            return false; // Word is already in favorites
        } else {
            favorites.add(word);
            saveFavorites(favorites);
            return true; // Word added to favorites
        }
    }

    private static void saveFavorites(Set<String> favorites) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FAVORITES_FILE), StandardCharsets.UTF_8))) {
            for (String word : favorites) {
                bw.write(word);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static boolean isFavoritesFrameVisible = false;
    private static JFrame favoritesFrame;



    private static void showFavoritesFrame() {
        if (!isFavoritesFrameVisible) {
            favoritesFrame = new JFrame("Favorites");
            favoritesFrame.setSize(500, 600);
            favoritesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel favoritesPanel = new JPanel(new BorderLayout());

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Word");
            model.addColumn("Meaning");

            favoritesTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(favoritesTable);

            JPanel buttonPanel = new JPanel();
            JButton sortAZButton = new JButton("Sort A-Z");
            sortAZButton.addActionListener(e -> {
                sortFavorites(model, true);
            });
            JButton sortZAButton = new JButton("Sort Z-A");
            sortZAButton.addActionListener(e -> {
                sortFavorites(model, false);
            });
            buttonPanel.add(sortAZButton);
            buttonPanel.add(sortZAButton);

            favoritesPanel.add(scrollPane, BorderLayout.CENTER);
            favoritesPanel.add(buttonPanel, BorderLayout.SOUTH);

            for (String favorite : favorites) {
                String meaning = dictionaryManager.searchWord(favorite);
                if (meaning != null) {
                    model.addRow(new Object[]{favorite, meaning});
                }
            }

            favoritesFrame.add(favoritesPanel);
            favoritesFrame.setVisible(true);
            isFavoritesFrameVisible = true;
        } else {
            favoritesFrame.toFront(); // Nếu đã mở, đưa cửa sổ favorits ra phía trước
        }

        favoritesFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isFavoritesFrameVisible = false;
            }
        });
        favoritesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Xác định khi nào chuột được nhấp đúp
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    String word = (String) target.getValueAt(row, 0); // Lấy từ từ cột đầu tiên (cột chứa từ)
                    String meaning = (String) target.getValueAt(row, 1); // Lấy nghĩa từ cột thứ hai (cột chứa nghĩa)
                    showWordMeaningDialog(word, meaning);
                }
            }
        });
    }

    private static JFrame wordMeaningFrame = null;

    private static void showWordMeaningDialog(String word, String meaning) {
        if (wordMeaningFrame == null) {
            wordMeaningFrame = new JFrame("Word Meaning");
            wordMeaningFrame.setSize(500, 500);
            wordMeaningFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else {
            wordMeaningFrame.getContentPane().removeAll(); // Xóa bất kỳ thành phần nào đã được thêm vào trước đó
        }

        JPanel wordMeaningPanel = new JPanel(new BorderLayout());

        JTextArea wordMeaningArea = new JTextArea();
        wordMeaningArea.setEditable(false);
        wordMeaningArea.append("Word: " + word + "\n");
        wordMeaningArea.append("Meaning: " + meaning + "\n");

        JScrollPane scrollPane = new JScrollPane(wordMeaningArea);

        wordMeaningPanel.add(scrollPane, BorderLayout.CENTER);

        wordMeaningFrame.add(wordMeaningPanel);
        wordMeaningFrame.setVisible(true);
    }


    private static void sortFavorites(DefaultTableModel model, boolean ascending) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        favoritesTable.setRowSorter(sorter);
    }
    private static void showStatisticsFrame() {
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






    private static void showStatistics(Date startDate, Date endDate, JTextArea statisticsArea) {
        Map<String, Integer> wordFrequency = dictionaryManager.getWordFrequency(startDate, endDate);

        StringBuilder sb = new StringBuilder();
        sb.append("Search Statistics from ").append(startDate).append(" to ").append(endDate).append("\n");
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" times\n");
        }

        statisticsArea.setText(sb.toString());
    }
}
