package shapeville.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom wooden-styled button component for the Shapeville application.
 * This button features a wooden appearance with hover effects and optional description text.
 * It extends JButton to provide a consistent wooden theme across the application.
 *
 * @author Shapeville Team
 * @version 1.0
 */
public class WoodenButton extends JButton {
    /** Background color for the wooden button */
    private static final Color BG_COLOR = new Color(232, 194, 145);
    
    /** Border color for the wooden button */
    private static final Color BORDER_COLOR = new Color(160, 82, 45);
    
    /** Text color for the button */
    private static final Color FONT_COLOR = new Color(101, 67, 33);
    
    /** Font used for the button text */
    private static final Font BUTTON_FONT = new Font("Serif", Font.BOLD, 22);
    
    /** Corner radius for the rounded rectangle */
    private static final int ARC = 16;
    
    /** Flag indicating if the button is currently being hovered over */
    private boolean hovered = false;
    
    /** Optional description text displayed below the main button text */
    private String description = null;

    /**
     * Constructs a new WoodenButton with the specified text.
     *
     * @param text The text to display on the button
     */
    public WoodenButton(String text) {
        super(text);
        setupButton();
    }
    
    /**
     * Constructs a new WoodenButton with the specified text and description.
     *
     * @param text The main text to display on the button
     * @param description The description text to display below the main text
     */
    public WoodenButton(String text, String description) {
        super(text);
        this.description = description;
        setupButton();
    }
    
    /**
     * Sets up the basic properties and appearance of the button.
     * This includes removing default button styling, setting fonts,
     * and adding hover effect listeners.
     */
    private void setupButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(BUTTON_FONT);
        setForeground(FONT_COLOR);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setMargin(new Insets(12, 24, 12, 24));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    /**
     * Custom painting method to create the wooden appearance.
     * This method handles:
     * - Drawing the wooden background
     * - Drawing the border
     * - Adding hover effects
     * - Rendering the text and optional description
     *
     * @param g The Graphics context to paint with
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        g2.setColor(BG_COLOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        
        // Draw border
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        
        // Draw hover highlight
        if (hovered) {
            g2.setColor(new Color(255, 255, 150, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        }
        
        // Draw text
        String text = getText();
        g2.setColor(FONT_COLOR);
        g2.setFont(BUTTON_FONT);
        
        if (description != null) {
            // Draw two lines of text if description exists
            FontMetrics fm = g2.getFontMetrics();
            int titleWidth = fm.stringWidth(text);
            int titleHeight = fm.getAscent();
            int x = (getWidth() - titleWidth) / 2;
            int y = getHeight() / 2 - 10;  // Title in upper half with spacing
            
            // Draw title
            g2.drawString(text, x, y);
            
            // Draw description with smaller font
            Font descFont = new Font(BUTTON_FONT.getName(), Font.PLAIN, 18);
            g2.setFont(descFont);
            fm = g2.getFontMetrics();
            int descWidth = fm.stringWidth(description);
            x = (getWidth() - descWidth) / 2;
            y = getHeight() / 2 + fm.getAscent() + 10; // Description in lower half with spacing
            g2.drawString(description, x, y);
        } else {
            // Draw single line of text centered
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + textHeight) / 2 - 3;
            g2.drawString(text, x, y);
        }
        
        g2.dispose();
    }
    
    /**
     * Sets the description text for the button.
     * The description will be displayed below the main button text.
     *
     * @param description The description text to display
     */
    public void setDescription(String description) {
        this.description = description;
        repaint();
    }
} 