package filecrypt;

import filecrypt.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Packaged launcher for File Encryption & Decryption Tool.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
