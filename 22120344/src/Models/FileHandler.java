// FileHandler.java

package Models;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class FileHandler {
    private static final String FAVORITES_FILE = "favorites.txt";

    public static Set<String> loadFavorites() {
        Set<String> favorites = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FAVORITES_FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                favorites.add(line.trim());
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty set
        }
        return favorites;
    }

    public static void saveFavorites(Set<String> favorites) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FAVORITES_FILE), StandardCharsets.UTF_8))) {
            for (String word : favorites) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}