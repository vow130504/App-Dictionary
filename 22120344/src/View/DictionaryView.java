package View;

import Controller.DictionaryController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import Support.RoundedButton;
import Support.RoundedTextField;
import Support.RoundedPanel;
import Support.RoundedBorder;

public class DictionaryView {

    private JFrame mainFrame;
    private RoundedTextField searchField;
    private JTextArea resultArea;
    private RoundedButton searchButton;
    private RoundedButton favoriteButton;
    private RoundedButton englishToVietnameseButton;
    private RoundedButton vietnameseToEnglishButton;
    private RoundedButton addButton;
    private RoundedButton deleteButton;
    private RoundedButton showFavoritesButton;
    private RoundedButton showStatisticsButton;

    private DictionaryController controller;

    private JFrame favoritesFrame;
    private JTable favoritesTable;
    private boolean isFavoritesFrameVisible = false;
    private JFrame wordMeaningFrame = null;

    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Color BACKGROUND_COLOR = new Color(200, 255, 200); // Xanh nhạt
    private static final Color BUTTON_COLOR = new Color(0, 100, 0); // Xanh đậm
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;
    private static final int CORNER_RADIUS = 15;

    public DictionaryView(DictionaryController controller) {
        this.controller = controller;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        mainFrame = new JFrame("Từ điển Anh - Việt");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Mở toàn màn hình

        // Main panel with BorderLayout
        RoundedPanel mainPanel = new RoundedPanel(CORNER_RADIUS);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title panel
        RoundedPanel titlePanel = new RoundedPanel(CORNER_RADIUS);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Ứng dụng Từ điển Anh - Việt");
        titleLabel.setFont(TITLE_FONT);
        titlePanel.add(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        titleLabel.setForeground(new Color(0, 128, 0));

        // Content panel (center) with button panel on left and content on right
        RoundedPanel contentPanel = new RoundedPanel(CORNER_RADIUS);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel (vertical, on the left)
        RoundedPanel buttonPanel = new RoundedPanel(CORNER_RADIUS);
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and configure buttons
        englishToVietnameseButton = createStyledButton("Anh - Việt");
        vietnameseToEnglishButton = createStyledButton("Việt - Anh");
        addButton = createStyledButton("Thêm từ");
        deleteButton = createStyledButton("Xóa từ");
        showFavoritesButton = createStyledButton("Danh sách thích");
        showStatisticsButton = createStyledButton("Thống kê");
        favoriteButton = createStyledButton("Từ yêu thích");

        // Add buttons to panel with spacing
        addButtonWithSpacing(buttonPanel, englishToVietnameseButton);
        addButtonWithSpacing(buttonPanel, vietnameseToEnglishButton);
        addButtonWithSpacing(buttonPanel, addButton);
        addButtonWithSpacing(buttonPanel, deleteButton);
        addButtonWithSpacing(buttonPanel, showFavoritesButton);
        addButtonWithSpacing(buttonPanel, showStatisticsButton);
        addButtonWithSpacing(buttonPanel, favoriteButton);

        contentPanel.add(buttonPanel, BorderLayout.WEST);

        // Right panel for search and results
        RoundedPanel rightPanel = new RoundedPanel(CORNER_RADIUS);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        RoundedPanel searchPanel = new RoundedPanel(CORNER_RADIUS);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        searchField = new RoundedTextField(CORNER_RADIUS);
        searchField.setFont(DEFAULT_FONT);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchButton = createStyledButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(DEFAULT_FONT);
        resultArea.setBackground(Color.WHITE);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new RoundedBorder(CORNER_RADIUS, Color.GRAY));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add event listeners
        setupEventListeners();

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    private RoundedButton createStyledButton(String text) {
        RoundedButton button = new RoundedButton(text, CORNER_RADIUS);
        button.setFont(DEFAULT_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void addButtonWithSpacing(JPanel panel, JButton button) {
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    ///
    ///
    private void setupEventListeners() {
        searchButton.addActionListener(e -> searchWord());

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchWord();
                }
            }
        });

        favoriteButton.addActionListener(e -> toggleFavorite());

        englishToVietnameseButton.addActionListener(e -> {
            controller.switchToEnglishToVietnamese();
            JOptionPane.showMessageDialog(mainFrame, "Language switched to English to Vietnamese.");
        });

        vietnameseToEnglishButton.addActionListener(e -> {
            controller.switchToVietnameseToEnglish();
            JOptionPane.showMessageDialog(mainFrame, "Language switched to Vietnamese to English.");
        });

        addButton.addActionListener(e -> showAddWordDialog());
        deleteButton.addActionListener(e -> showDeleteWordDialog());
        showFavoritesButton.addActionListener(e -> showFavoritesFrame());
        showStatisticsButton.addActionListener(e -> showStatisticsFrame());
    }

    private void searchWord() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            String meaning = controller.searchWord(searchTerm);
            if (meaning != null) {
                boolean isFavorite = controller.isFavorite(searchTerm);
                favoriteButton.setText(isFavorite ? "Unfavorite" : "Favorite");
                resultArea.setText("Word: " + searchTerm + "\nMeaning: " + meaning + "\n");
            } else {
                resultArea.setText("Word not found.");
            }
        }
    }

    private void toggleFavorite() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            boolean isNowFavorite = controller.toggleFavorite(searchTerm);
            favoriteButton.setText(isNowFavorite ? "Unfavorite" : "Favorite");
            JOptionPane.showMessageDialog(mainFrame,
                    isNowFavorite ? "Word added to Favorites." : "Word removed from Favorites.");
        }
    }

    private void showAddWordDialog() {
        String word = JOptionPane.showInputDialog(mainFrame, "Enter word:");
        if (word != null && !word.trim().isEmpty()) {
            word = word.trim().toLowerCase();

            String[] options = {"English to Vietnamese", "Vietnamese to English"};
            int choice = JOptionPane.showOptionDialog(mainFrame,
                    "Select language direction:", "Add Word",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            boolean isEnglishToVietnamese = (choice == 0);

            if (controller.wordExists(word)) {
                JOptionPane.showMessageDialog(mainFrame, "Word already exists in the dictionary.");
                return;
            }

            String meaning = JOptionPane.showInputDialog(mainFrame, "Enter meaning:");
            if (meaning != null && !meaning.trim().isEmpty()) {
                boolean success = controller.addWord(word, meaning, isEnglishToVietnamese);
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Word added successfully.");
                }
            }
        }
    }

    private void showDeleteWordDialog() {
        String wordToDelete = JOptionPane.showInputDialog(mainFrame, "Enter word to delete:");
        if (wordToDelete != null && !wordToDelete.trim().isEmpty()) {
            wordToDelete = wordToDelete.trim().toLowerCase();

            int confirmation = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to delete the word: " + wordToDelete + "?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                String[] options = {"English to Vietnamese", "Vietnamese to English"};
                int choice = JOptionPane.showOptionDialog(mainFrame,
                        "Select language direction for the word: " + wordToDelete,
                        "Delete Word", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);

                boolean isEnglishToVietnamese = (choice == 0);
                boolean deleted = controller.deleteWord(wordToDelete, isEnglishToVietnamese);

                if (!deleted) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Word does not exist in the selected dictionary.",
                            "Word Not Found", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showFavoritesFrame() {
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
            JButton sortZAButton = new JButton("Sort Z-A");

            sortAZButton.addActionListener(e -> sortFavorites(model, true));
            sortZAButton.addActionListener(e -> sortFavorites(model, false));

            buttonPanel.add(sortAZButton);
            buttonPanel.add(sortZAButton);

            favoritesPanel.add(scrollPane, BorderLayout.CENTER);
            favoritesPanel.add(buttonPanel, BorderLayout.SOUTH);

            for (String favorite : controller.getFavorites()) {
                String meaning = controller.searchWord(favorite);
                if (meaning != null) {
                    model.addRow(new Object[]{favorite, meaning});
                }
            }

            favoritesTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();
                        String word = (String) target.getValueAt(row, 0);
                        String meaning = (String) target.getValueAt(row, 1);
                        showWordMeaningDialog(word, meaning);
                    }
                }
            });

            favoritesFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    isFavoritesFrameVisible = false;
                }
            });

            favoritesFrame.add(favoritesPanel);
            favoritesFrame.setVisible(true);
            isFavoritesFrameVisible = true;
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

    private void sortFavorites(DefaultTableModel model, boolean ascending) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
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
                JOptionPane.showMessageDialog(statisticsFrame,
                        "Invalid date format. Please use yyyy-MM-dd format.");
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
        Map<String, Integer> wordFrequency = controller.getWordFrequency(startDate, endDate);

        StringBuilder sb = new StringBuilder();
        sb.append("Search Statistics from ").append(startDate).append(" to ").append(endDate).append("\n");
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" times\n");
        }

        statisticsArea.setText(sb.toString());
    }
}
