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
        favoriteButton = createStyledButton("Yêu thích");

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
            JOptionPane.showMessageDialog(mainFrame, "Ngôn ngữ chuyển từ tiếng Anh sang tiếng Việt.");
        });

        vietnameseToEnglishButton.addActionListener(e -> {
            controller.switchToVietnameseToEnglish();
            JOptionPane.showMessageDialog(mainFrame, "Ngôn ngữ chuyển từ tiếng Việt sang tiếng Anh.");
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
                favoriteButton.setText(isFavorite ? "Bỏ yêu thích" : "Yêu thích");
                resultArea.setText("Word: " + searchTerm + "\nMeaning: " + meaning + "\n");
            } else {
                resultArea.setText("Không tìm thấy từ vựng.");
            }
        }
    }

    private void toggleFavorite() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            boolean isNowFavorite = controller.toggleFavorite(searchTerm);
            favoriteButton.setText(isNowFavorite ? "Bỏ yêu thích" : "Yêu thích");
            JOptionPane.showMessageDialog(mainFrame,
                    isNowFavorite ? "Đã thêm từ vào mục Yêu thích." : "Đã xóa từ khỏi mục Yêu thích.");
        }
    }

    private void showAddWordDialog() {
        String word = JOptionPane.showInputDialog(mainFrame, "Nhập từ:");
        if (word != null && !word.trim().isEmpty()) {
            word = word.trim().toLowerCase();

            String[] options = {"Tiếng Anh sang tiếng Việt", "Tiếng Việt sang tiếng Anh"};
            int choice = JOptionPane.showOptionDialog(mainFrame,
                    "Chọn hướng ngôn ngữ:", "Thêm từ",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            boolean isEnglishToVietnamese = (choice == 0);

            if (controller.wordExists(word)) {
                JOptionPane.showMessageDialog(mainFrame, "Từ này đã tồn tại trong từ điển.");
                return;
            }

            String meaning = JOptionPane.showInputDialog(mainFrame, "Nhập ý nghĩa:");
            if (meaning != null && !meaning.trim().isEmpty()) {
                boolean success = controller.addWord(word, meaning, isEnglishToVietnamese);
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Đã thêm từ thành công.");
                }
            }
        }
    }

    private void showDeleteWordDialog() {
        String wordToDelete = JOptionPane.showInputDialog(mainFrame, "Nhập từ cần xóa:");
        if (wordToDelete != null && !wordToDelete.trim().isEmpty()) {
            wordToDelete = wordToDelete.trim().toLowerCase();

            int confirmation = JOptionPane.showConfirmDialog(mainFrame,
                    "Bạn có chắc chắn muốn xóa từ: " + wordToDelete + " không ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                String[] options = {"Tiếng Anh sang tiếng Việt", "Tiếng Việt sang tiếng Anh"};
                int choice = JOptionPane.showOptionDialog(mainFrame,
                        "Chọn hướng ngôn ngữ cho từ: " + wordToDelete,
                        "Xóa từ", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);

                boolean isEnglishToVietnamese = (choice == 0);
                boolean deleted = controller.deleteWord(wordToDelete, isEnglishToVietnamese);

                if (!deleted) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Từ không tồn tại trong từ điển đã chọn.",
                            "Không tìm thấy từ", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showFavoritesFrame() {
        if (!isFavoritesFrameVisible) {
            favoritesFrame = new JFrame("Yêu thích");
            favoritesFrame.setSize(500, 600);
            favoritesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel favoritesPanel = new JPanel(new BorderLayout());

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Word");
            model.addColumn("Meaning");

            favoritesTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(favoritesTable);

            JPanel buttonPanel = new JPanel();
            JButton sortAZButton = new JButton("Sắp xếp A-Z");
            JButton sortZAButton = new JButton("Sắp xếp Z-A");

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
        JFrame statisticsFrame = new JFrame("Tìm kiếm Thống kê");
        statisticsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statisticsFrame.setSize(500, 600);
        statisticsFrame.setLocationRelativeTo(null); // Căn giữa khung thống kê

        JPanel statisticsPanel = new JPanel(new BorderLayout());

        JLabel startDateLabel = new JLabel("Ngày bắt đầu (yyyy-MM-dd):");
        JLabel endDateLabel = new JLabel("Ngày kết thúc (yyyy-MM-dd):");

        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);

        JButton generateButton = new JButton("Tạo số liệu thống kê");
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
                        "Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng yyyy-MM-dd.");
            }
        });

        // Tạo panel cho nút để căn giữa
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateButton);

        // Sử dụng GridBagLayout thay vì GridLayout để căn chỉnh tốt hơn
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thêm startDateLabel và startDateField
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(startDateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(startDateField, gbc);

        // Thêm endDateLabel và endDateField
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(endDateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(endDateField, gbc);

        // Thêm buttonPanel (chứa nút căn giữa)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Chiếm cả 2 cột
        gbc.fill = GridBagConstraints.NONE; // Không giãn nút
        inputPanel.add(buttonPanel, gbc);

        statisticsPanel.add(inputPanel, BorderLayout.NORTH);
        statisticsPanel.add(scrollPane, BorderLayout.CENTER);

        statisticsFrame.add(statisticsPanel);
        statisticsFrame.setVisible(true);
    }

    private void showStatistics(Date startDate, Date endDate, JTextArea statisticsArea) {
        Map<String, Integer> wordFrequency = controller.getWordFrequency(startDate, endDate);

        StringBuilder sb = new StringBuilder();
        sb.append("Tìm kiếm Thống kê từ ").append(startDate).append(" đến ").append(endDate).append("\n");
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" lần\n");
        }

        statisticsArea.setText(sb.toString());
    }
}
