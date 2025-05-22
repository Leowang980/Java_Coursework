package shapeville;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The launch screen class for the Shapeville application.
 * This class serves as the entry point for the application and handles
 * the initial setup of the UI look and feel.
 *
 * @author Shapeville Team
 * @version 1.0
 */
public class LaunchScreen {
    /**
     * The main entry point for the Shapeville application.
     * This method:
     * 1. Sets up the system's native look and feel
     * 2. Creates and displays the main application window
     * 3. Ensures proper focus for keyboard event handling
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ShapevilleApp app = new ShapevilleApp();
            app.setVisible(true);
            
            // Request focus for the start screen to capture key events
            app.requestFocusInWindow();
        });
    }
} 