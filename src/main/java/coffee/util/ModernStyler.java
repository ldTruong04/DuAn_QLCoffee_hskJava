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
        // Table styling
        table.setBackground(ModernUITheme.BG_TERTIARY);
        table.setForeground(ModernUITheme.TEXT_PRIMARY);
        table.setGridColor(ModernUITheme.BORDER_COLOR);
        table.setRowHeight(32);
        table.setFont(ModernUITheme.FONT_BODY);
        table.setSelectionBackground(ModernUITheme.PRIMARY_COLOR);
        table.setSelectionForeground(ModernUITheme.TEXT_PRIMARY);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(ModernUITheme.BG_SECONDARY);
        header.setForeground(ModernUITheme.TEXT_PRIMARY);
        header.setFont(ModernUITheme.FONT_SUBHEADING);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ModernUITheme.PRIMARY_COLOR));
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
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        
        verticalScrollBar.setBackground(ModernUITheme.BG_SECONDARY);
        horizontalScrollBar.setBackground(ModernUITheme.BG_SECONDARY);
        
        // Style scroll bar UI
        scrollPane.getViewport().setBackground(ModernUITheme.BG_TERTIARY);
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
