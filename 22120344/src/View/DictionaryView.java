// DictionaryView.java
package View;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DictionaryView {
    private JFrame frame;
    private JTextField searchField;
    private JButton searchButton;
    private JTextArea resultArea;
    private JButton englishToVietnameseButton;
    private JButton vietnameseToEnglishButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton favoriteButton;
    private JButton showFavoritesButton;
    private JButton showStatisticsButton;

    public DictionaryView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Dictionary App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 400);

        Font defaultFont = new Font("Arial", Font.PLAIN, 14);
        Color backgroundColor = new Color(240, 240, 240);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        // Search components
        searchField = new JTextField();
        searchField.setFont(defaultFont);
        searchButton = new JButton("Search");
        searchButton.setFont(defaultFont);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(backgroundColor);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(defaultFont);
        resultArea.setBackground(backgroundColor);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Buttons
        englishToVietnameseButton = new JButton("English to Vietnamese");
        vietnameseToEnglishButton = new JButton("Vietnamese to English");
        addButton = new JButton("Add Word");
        deleteButton = new JButton("Delete Word");
        favoriteButton = new JButton("Favorite");
        showFavoritesButton = new JButton("Show Favorites");
        showStatisticsButton = new JButton("Show Statistics");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(englishToVietnameseButton);
        buttonPanel.add(vietnameseToEnglishButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(favoriteButton);
        buttonPanel.add(showFavoritesButton);
        buttonPanel.add(showStatisticsButton);

        // Layout
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
    }

    // Getters for all components
    public JFrame getFrame() { return frame; }
    public JTextField getSearchField() { return searchField; }
    public JButton getSearchButton() { return searchButton; }
    public JTextArea getResultArea() { return resultArea; }
    public JButton getEnglishToVietnameseButton() { return englishToVietnameseButton; }
    public JButton getVietnameseToEnglishButton() { return vietnameseToEnglishButton; }
    public JButton getAddButton() { return addButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getFavoriteButton() { return favoriteButton; }
    public JButton getShowFavoritesButton() { return showFavoritesButton; }
    public JButton getShowStatisticsButton() { return showStatisticsButton; }

    public void show() {
        frame.setVisible(true);
    }
}