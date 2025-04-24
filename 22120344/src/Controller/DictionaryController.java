package Controller;

import Models.DictionaryManager;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DictionaryController {
    private static final String FAVORITES_FILE = "./22120344/Data/Favorites.txt";
    private DictionaryManager dictionaryManager;
    private Set<String> favorites;

    public DictionaryController(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
        this.favorites = loadFavorites();
    }

    public String searchWord(String searchTerm) {
        if (!searchTerm.isEmpty()) {
            String meaning = dictionaryManager.searchWord(searchTerm);
            if (meaning != null) {
                dictionaryManager.logSearch(searchTerm);
                return meaning;
            }
        }
        return null;
    }

    public void switchToEnglishToVietnamese() {
        dictionaryManager.setEnglishToVietnamese();
    }

    public void switchToVietnameseToEnglish() {
        dictionaryManager.setVietnameseToEnglish();
    }

    public boolean isEnglishToVietnamese() {
        return dictionaryManager.isEnglishToVietnamese();
    }

    public boolean addWord(String word, String meaning, boolean isEnglishToVietnamese) {
        if (word == null || word.isEmpty() || meaning == null || meaning.isEmpty()) {
            return false;
        }

        if (dictionaryManager.wordExistsInSpecificDictionary(word.trim().toLowerCase(), isEnglishToVietnamese)) {
            return false;
        }

        dictionaryManager.addWord(word.trim().toLowerCase(), meaning.trim(), isEnglishToVietnamese);
        return true;
    }

    public boolean deleteWord(String word, boolean isEnglishToVietnamese) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        if (!dictionaryManager.wordExistsInSpecificDictionary(word.trim().toLowerCase(), isEnglishToVietnamese)) {
            return false;
        }

        dictionaryManager.deleteWord(word.trim().toLowerCase(), isEnglishToVietnamese);
        return true;
    }

    public boolean wordExists(String word) {
        return dictionaryManager.wordExists(word.trim().toLowerCase());
    }

    public Set<String> getFavorites() {
        return favorites;
    }

    public boolean toggleFavorite(String word) {
        if (favorites.contains(word)) {
            favorites.remove(word);
            saveFavorites();
            return false; // Word is removed from favorites
        } else {
            favorites.add(word);
            saveFavorites();
            return true; // Word added to favorites
        }
    }

    public boolean isFavorite(String word) {
        return favorites.contains(word);
    }

    public boolean addToFavorites(String word) {
        if (favorites.contains(word)) {
            return false; // Word is already in favorites
        } else {
            favorites.add(word);
            saveFavorites();
            return true; // Word added to favorites
        }
    }

    public boolean removeFromFavorites(String word) {
        if (favorites.contains(word)) {
            favorites.remove(word);
            saveFavorites();
            return true; // Word removed from favorites
        }
        return false; // Word was not in favorites
    }

    private Set<String> loadFavorites() {
        Set<String> favorites = new HashSet<>();
        File favoritesFile = new File(FAVORITES_FILE);

        // Create directory if it doesn't exist
        if (!favoritesFile.getParentFile().exists()) {
            favoritesFile.getParentFile().mkdirs();
        }

        // Create file if it doesn't exist
        if (!favoritesFile.exists()) {
            try {
                favoritesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    private void saveFavorites() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FAVORITES_FILE), StandardCharsets.UTF_8))) {
            for (String word : favorites) {
                bw.write(word);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getWordFrequency(Date startDate, Date endDate) {
        return dictionaryManager.getWordFrequency(startDate, endDate);
    }
}