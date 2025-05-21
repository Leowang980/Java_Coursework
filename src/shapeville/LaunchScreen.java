package shapeville;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class LaunchScreen {
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