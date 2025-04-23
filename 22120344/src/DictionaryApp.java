
import Controller.DictionaryController;
import Models.DictionaryManager;
import View.DictionaryView;
import javax.swing.*;

public class DictionaryApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DictionaryManager manager = new DictionaryManager();
                DictionaryView view = new DictionaryView();
                new DictionaryController(manager, view);
                view.show();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}