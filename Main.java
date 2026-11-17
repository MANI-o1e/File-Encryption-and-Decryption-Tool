import filecrypt.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application launcher for the File Encryption & Decryption Tool.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native platform aesthetics
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default Swing look and feel
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
