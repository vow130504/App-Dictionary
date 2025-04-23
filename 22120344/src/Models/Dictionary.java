package Dictionary;

import Models.Word;
import Models.WType;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class Dictionary {
    private Map<String, LocalDate> E_to_V_count;
    private Map<String, LocalDate> V_to_E_count;

    private Map<String, Word> V_to_E;
    private Map<String, Word> E_to_V;
    private Map<String, Word> Fav_E_to_V;
    private Map<String, Word> Fav_V_to_E;

    private Map<String, Integer> E_TO_V_FREQUENCY_MAP;
    private Map<String, Integer> V_TO_E_FREQUENCY_MAP;
    private List<Integer> errorList = new ArrayList<>();

    public Dictionary() {
        this.E_to_V_count = new HashMap<>();
        this.V_to_E_count = new HashMap<>();
        this.E_to_V = new HashMap<>();
        this.V_to_E = new HashMap<>();
        this.Fav_E_to_V = new HashMap<>();
        this.Fav_V_to_E = new HashMap<>();
    }

    public Map<String, Word> getE_to_V() {
        return E_to_V;
    }

    public void setE_to_V(Map<String, Word> E_to_V) {
        this.E_to_V = E_to_V;
    }

    public Map<String, Word> getV_to_E() {
        return V_to_E;
    }

    public void setV_to_E(Map<String, Word> V_to_E) {
        this.V_to_E = V_to_E;
    }

    public Map<String, Word> getFav_E_to_V() {
        return Fav_E_to_V;
    }

    public void setFav_E_to_V(Map<String, Word> Fav_E_to_V) {
        this.Fav_E_to_V = Fav_E_to_V;
    }

    public Map<String, Word> getFav_V_to_E() {
        return Fav_V_to_E;
    }

    public void setFav_V_to_E(Map<String, Word> Fav_V_to_E) {
        this.Fav_V_to_E = Fav_V_to_E;
    }

    public Map<String, LocalDate> getE_to_V_count() {
        return E_to_V_count;
    }

    public void setE_to_V_count(Map<String, LocalDate> E_to_V_count) {
        this.E_to_V_count = E_to_V_count;
    }

    public Map<String, LocalDate> getV_to_E_count() {
        return V_to_E_count;
    }

    public void setV_to_E_count(Map<String, LocalDate> V_to_E_count) {
        this.V_to_E_count = V_to_E_count;
    }

    // --- Methods thêm, xóa, cập nhật từ ---
    public void addWord(String word, String meann, String pronun, List<WType> types,
                        boolean isFav, boolean isEtoV, LocalDate date, boolean isCount) {
        Word newWord = new Word(word, meann, pronun, types);
        if (isFav) {
            Fav_E_to_V.put(word, newWord);
            Fav_V_to_E.put(word, newWord);
        } else {
            if (isEtoV) {
                E_to_V.put(word, newWord);
                if (isCount) E_to_V_count.put(word, date);
            } else {
                V_to_E.put(word, newWord);
                if (isCount) V_to_E_count.put(word, date);
            }
        }
    }

    public void removeWord(String word, boolean isFav, boolean isEtoV, LocalDate date, boolean isCount) {
        if (isFav) {
            Fav_E_to_V.remove(word);
            Fav_V_to_E.remove(word);
        } else {
            if (isEtoV) {
                E_to_V.remove(word);
                if (isCount) E_to_V_count.remove(word);
            } else {
                V_to_E.remove(word);
                if (isCount) V_to_E_count.remove(word);
            }
        }
    }

    public void updateWord(String word, String meann, String pronun, List<WType> types,
                           boolean isFav, boolean isEtoV, LocalDate date, boolean isCount) {
        Word newWord = new Word(word, meann, pronun, types);
        if (isFav) {
            Fav_E_to_V.put(word, newWord);
            Fav_V_to_E.put(word, newWord);
        } else {
            if (isEtoV) {
                E_to_V.put(word, newWord);
                if (isCount) E_to_V_count.put(word, date);
            } else {
                V_to_E.put(word, newWord);
                if (isCount) V_to_E_count.put(word, date);
            }
        }
    }

    // --- Load XML frequency map ---
    public void loadSearchFrequencyFromXML(String V_to_E_Path, String E_to_V_Path) {
        System.out.println("Loading search frequency data");
        E_TO_V_FREQUENCY_MAP = GlobalFunction.loadSearchFrequencyMapFromXML(E_to_V_Path, "EnglishToVietnameseSearchFrequency");
        V_TO_E_FREQUENCY_MAP = GlobalFunction.loadSearchFrequencyMapFromXML(V_to_E_Path, "VietnameseToEnglishSearchFrequency");

        if (V_TO_E_FREQUENCY_MAP == null || V_TO_E_FREQUENCY_MAP.isEmpty()) {
            System.out.println("Cannot load data for V_TO_E_FREQUENCY_MAP");
            errorList.add(5);
        }
        if (E_TO_V_FREQUENCY_MAP == null || E_TO_V_FREQUENCY_MAP.isEmpty()) {
            System.out.println("Cannot load data for E_TO_V_FREQUENCY_MAP");
            errorList.add(6);
        }
    }

    public void clear(boolean isFav, boolean isEtoV) {
        if (isFav) {
            Fav_E_to_V.clear();
            Fav_V_to_E.clear();
        } else {
            if (isEtoV) E_to_V.clear();
            else V_to_E.clear();
        }
    }
    public void loadDictionariesFromXML(String vieToEngPath, String engToViePath) {
        vietnameseToEnglishDictionary = loadDictionaryFromXML(vieToEngPath);
        englishToVietnameseDictionary = loadDictionaryFromXML(engToViePath);
        if (vietnameseToEnglishDictionary == null) errorList.add(1);
        if (englishToVietnameseDictionary == null) errorList.add(2);
    }

    public void loadFavoriteWordsFromXML(String vieToEngFavoritePath, String engToVieFavoritePath) {
        vietnameseToEnglishFavoriteWords = loadDictionaryFromXML(vieToEngFavoritePath);
        englishToVietnameseFavoriteWords = loadDictionaryFromXML(engToVieFavoritePath);
        if (vietnameseToEnglishFavoriteWords == null) errorList.add(3);
        if (englishToVietnameseFavoriteWords == null) errorList.add(4);
    }

    public void saveSearchFrequencyToXML(String vieToEngFreqPath, String engToVieFreqPath) {
        GlobalFunction.saveSearchFrequencyMapToXML(vietnameseToEnglishSearchFrequencyMap, vieToEngFreqPath, "VietnameseToEnglishSearchFrequency");
        GlobalFunction.saveSearchFrequencyMapToXML(englishToVietnameseSearchFrequencyMap, engToVieFreqPath, "EnglishToVietnameseSearchFrequency");
    }

    public String getHtmlMeaning(String word, boolean isVieToEngMode) {
        increaseSearchFrequency(word, isVieToEngMode);
        saveSearchFrequencyToXML(ConstantString.VIETNAMESE_TO_ENGLISH_SEARCH_FREQUENCY_FILE_PATH,
                ConstantString.ENGLISH_TO_VIETNAMESE_SEARCH_FREQUENCY_FILE_PATH);
        return isVieToEngMode ? vietnameseToEnglishDictionary.get(word).toHtmlString()
                : englishToVietnameseDictionary.get(word).toHtmlString();
    }

    public TreeSet<String> search(String keyword, boolean isVieToEngMode, boolean isFavoriteMode) {
        System.out.println("Searching in favorite mode: " + isFavoriteMode);
        TreeSet<String> relatedWords = new TreeSet<>();
        Set<String> keySet = isFavoriteMode
                ? (isVieToEngMode ? vietnameseToEnglishFavoriteWords.keySet() : englishToVietnameseFavoriteWords.keySet())
                : (isVieToEngMode ? vietnameseToEnglishDictionary.keySet() : englishToVietnameseDictionary.keySet());

        for (String word : keySet) {
            if (word.contains(keyword)) {
                relatedWords.add(word);
            }
        }
        return relatedWords;
    }

    public String suggest(String keyword, boolean isVieToEngMode, boolean isFavoriteMode) {
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;
        Set<String> keySet = isFavoriteMode
                ? (isVieToEngMode ? vietnameseToEnglishFavoriteWords.keySet() : englishToVietnameseFavoriteWords.keySet())
                : (isVieToEngMode ? vietnameseToEnglishDictionary.keySet() : englishToVietnameseDictionary.keySet());

        for (String word : keySet) {
            int distance = levenshteinDistance(keyword, word);
            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = word;
            }
        }
        return bestMatch;
    }

    public boolean isFavoriteWord(String word, boolean isVieToEngMode) {
        return isVieToEngMode ? vietnameseToEnglishFavoriteWords.containsKey(word)
                : englishToVietnameseFavoriteWords.containsKey(word);
    }

    public void addFavoriteWord(String word, Word meaning, boolean isVieToEngMode) {
        if (isVieToEngMode) vietnameseToEnglishFavoriteWords.put(word, meaning);
        else englishToVietnameseFavoriteWords.put(word, meaning);
    }

    public void removeFavoriteWord(String word, boolean isVieToEngMode) {
        if (isVieToEngMode) vietnameseToEnglishFavoriteWords.remove(word);
        else englishToVietnameseFavoriteWords.remove(word);
    }

    public Word getWord(String word, boolean isVieToEngMode) {
        return isVieToEngMode ? vietnameseToEnglishDictionary.get(word)
                : englishToVietnameseDictionary.get(word);
    }

    public void saveFavoriteWordsToXML(String vieToEngFavoritePath, String engToVieFavoritePath) {
        GlobalFunction.saveDictionaryToXML(vietnameseToEnglishFavoriteWords, vieToEngFavoritePath);
        GlobalFunction.saveDictionaryToXML(englishToVietnameseFavoriteWords, engToVieFavoritePath);
    }

    public void saveDictionariesToXML(String vieToEngPath, String engToViePath) {
        GlobalFunction.saveDictionaryToXML(vietnameseToEnglishDictionary, vieToEngPath);
        GlobalFunction.saveDictionaryToXML(englishToVietnameseDictionary, engToViePath);
    }

    public void deleteWord(String word, boolean isVieToEngMode) {
        if (isVieToEngMode) {
            vietnameseToEnglishDictionary.remove(word);
            vietnameseToEnglishFavoriteWords.remove(word);
        } else {
            englishToVietnameseDictionary.remove(word);
            englishToVietnameseFavoriteWords.remove(word);
        }
    }

    private void increaseSearchFrequency(String word, boolean isVieToEngMode) {
        LocalDate today = LocalDate.now();
        if (isVieToEngMode) {
            vietnameseToEnglishSearchFrequencyMap.computeIfAbsent(today, k -> new HashMap<>());
            vietnameseToEnglishSearchFrequencyMap.get(today).merge(word, 1, Integer::sum);
        } else {
            englishToVietnameseSearchFrequencyMap.computeIfAbsent(today, k -> new HashMap<>());
            englishToVietnameseSearchFrequencyMap.get(today).merge(word, 1, Integer::sum);
        }
    }

    public void loadSearchFrequencyFromXML(String vieToEngFreqPath, String engToVieFreqPath) {
        System.out.println("Loading search frequency data");
        englishToVietnameseSearchFrequencyMap = GlobalFunction.loadSearchFrequencyMapFromXML(engToVieFreqPath, "EnglishToVietnameseSearchFrequency");
        vietnameseToEnglishSearchFrequencyMap = GlobalFunction.loadSearchFrequencyMapFromXML(vieToEngFreqPath, "VietnameseToEnglishSearchFrequency");

        if (vietnameseToEnglishSearchFrequencyMap.keySet().isEmpty()) {
            System.out.println("Cannot load data for vietnameseToEnglishSearchFrequencyMap");
            errorList.add(5);
        }
        if (englishToVietnameseSearchFrequencyMap.keySet().isEmpty()) {
            System.out.println("Cannot load data for englishToVietnameseSearchFrequencyMap");
            errorList.add(6);
        }
    }

    public Map<String, Word> getFavoriteWords(boolean isVieToEngMode) {
        return isVieToEngMode ? vietnameseToEnglishFavoriteWords : englishToVietnameseFavoriteWords;
    }

}
