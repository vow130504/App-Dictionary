import javax.swing.*;
import Models.DictionaryManager;
import Controller.DictionaryController;
import View.DictionaryView;

public class DictionaryApp {
    public static void main(String[] args) {
        // Create necessary directories
        createDirectories();

        // Initialize application using MVC pattern
        SwingUtilities.invokeLater(() -> {
            DictionaryManager dictionaryManager = new DictionaryManager();
            DictionaryController controller = new DictionaryController(dictionaryManager);
            new DictionaryView(controller);
        });
    }

    private static void createDirectories() {
        // Ensure data directories exist
        new java.io.File("./22120344/Data").mkdirs();
    }
}