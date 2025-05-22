package shapeville.utils;

import java.awt.Color;

/**
 * A utility class that centralizes all color constants used throughout the application.
 * This class provides a consistent color scheme with a wooden theme for the UI elements.
 */
public class ColorConstants {
    /**
     * Wooden theme colors (from WoodenButton)
     */
    public static final Color WOOD_BG_COLOR = new Color(232, 194, 145);      // Light wood background
    public static final Color WOOD_BORDER_COLOR = new Color(160, 82, 45);    // Dark brown border
    public static final Color WOOD_TEXT_COLOR = new Color(101, 67, 33);      // Dark brown text
    
    /**
     * Main background color - light wooden tone
     */
    public static final Color MAIN_BG_COLOR = new Color(255, 248, 225);      // Beige, matching wooden theme
    
    /**
     * Title bar background color
     */
    public static final Color TITLE_BG_COLOR = new Color(176, 115, 66);      // Medium brown, lighter than border
    
    /**
     * Navigation bar background color
     */
    public static final Color NAV_BG_COLOR = new Color(245, 230, 200);       // Light yellow, coordinating with wooden theme
    
    /**
     * Success/completion page background color
     */
    public static final Color SUCCESS_BG_COLOR = new Color(235, 245, 215);   // Light green-yellow, showing completion
    
    /**
     * Bonus task panel background color
     */
    public static final Color BONUS_BG_COLOR = new Color(245, 235, 225);     // Light pink-wood
    
    /**
     * Special panel highlight colors
     */
    public static final Color PANEL_HIGHLIGHT_1 = new Color(240, 230, 205);  // Light brown-yellow
    public static final Color PANEL_HIGHLIGHT_2 = new Color(230, 220, 195);  // Slightly darker brown-yellow
    
    /**
     * Button colors (for submit and other function buttons)
     */
    public static final Color FUNC_BUTTON_BG = new Color(180, 155, 115);     // Medium wood, coordinating with border
    
    /**
     * Task completion indicator color
     */
    public static final Color TASK_COMPLETED_COLOR = new Color(144, 238, 144); // Light green, indicating task completion
} 