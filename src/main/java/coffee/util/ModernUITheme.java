package coffee.util;

import java.awt.*;

/**
 * Modern UI Theme - Color palette and styling constants for the application
 */
public class ModernUITheme {
    // Primary Colors
    public static final Color PRIMARY_COLOR = new Color(22, 163, 74);        // Green
    public static final Color PRIMARY_DARK = new Color(21, 128, 61);         // Dark green
    public static final Color PRIMARY_LIGHT = new Color(187, 247, 208);      // Light green

    // Secondary Colors
    public static final Color SUCCESS_COLOR = new Color(22, 163, 74);        // Green
    public static final Color WARNING_COLOR = new Color(234, 179, 8);        // Amber
    public static final Color DANGER_COLOR = new Color(220, 38, 38);         // Red
    public static final Color INFO_COLOR = new Color(34, 197, 94);           // Green accent

    // Neutral Colors
    public static final Color BG_PRIMARY = new Color(255, 255, 255);         // White
    public static final Color BG_SECONDARY = new Color(248, 250, 252);       // Soft gray
    public static final Color BG_TERTIARY = new Color(236, 253, 245);        // Very light green
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);          // Dark slate
    public static final Color TEXT_SECONDARY = new Color(51, 65, 85);        // Slate
    public static final Color TEXT_TERTIARY = new Color(100, 116, 139);      // Muted slate
    public static final Color BORDER_COLOR = new Color(203, 213, 225);       // Light border

    // Accent Colors
    public static final Color ACCENT_PURPLE = new Color(22, 163, 74);
    public static final Color ACCENT_CYAN = new Color(74, 222, 128);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);

    // Spacing
    public static final int PADDING_XS = 4;
    public static final int PADDING_SM = 8;
    public static final int PADDING_MD = 12;
    public static final int PADDING_LG = 16;
    public static final int PADDING_XL = 24;
    public static final int PADDING_XXL = 32;

    // Border Radius (used for custom rendering)
    public static final int BORDER_RADIUS_SM = 4;
    public static final int BORDER_RADIUS_MD = 8;
    public static final int BORDER_RADIUS_LG = 12;
}
