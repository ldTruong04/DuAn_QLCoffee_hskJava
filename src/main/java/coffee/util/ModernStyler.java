package coffee.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Utility class to apply modern styling to Swing components
 */
public class ModernStyler {
    
    /**
     * Apply modern styling to a JTable
     */
    public static void styleTable(JTable table) {
        // Removed all styling to leave table in default format
    }

    /**
     * Apply modern styling to a JPanel
     */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(ModernUITheme.BG_PRIMARY);
        panel.setForeground(ModernUITheme.TEXT_PRIMARY);
    }

    /**
     * Apply modern styling to a JScrollPane
     */
    public static void styleScrollPane(JScrollPane scrollPane) {
        // Removed all styling to leave scrollPane in default format
    }

    /**
     * Apply modern styling to a JLabel
     */
    public static void styleLabel(JLabel label) {
        label.setFont(ModernUITheme.FONT_BODY);
        label.setForeground(ModernUITheme.TEXT_PRIMARY);
    }

    /**
     * Create a modern form panel with GridBagLayout
     */
    public static JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ModernUITheme.BG_SECONDARY);
        panel.setBorder(BorderFactory.createEmptyBorder(
                ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG
        ));
        return panel;
    }

    /**
     * Create a modern section panel
     */
    public static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUITheme.BG_SECONDARY);
        panel.setBorder(BorderFactory.createEmptyBorder(
                ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG
        ));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(ModernUITheme.FONT_HEADING);
            titleLabel.setForeground(ModernUITheme.PRIMARY_COLOR);
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        return panel;
    }
}
