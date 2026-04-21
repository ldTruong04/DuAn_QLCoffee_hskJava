package coffee.util;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Modern custom password field with rounded corners and focus effects
 */
public class ModernPasswordField extends JPasswordField {
    private boolean isFocused = false;
    private final int borderRadius;
    private final int padding;

    public ModernPasswordField(int columns) {
        this(columns, ModernUITheme.BORDER_RADIUS_MD, ModernUITheme.PADDING_MD);
    }

    public ModernPasswordField(int columns, int borderRadius, int padding) {
        super(columns);
        this.borderRadius = borderRadius;
        this.padding = padding;

        setFont(ModernUITheme.FONT_BODY);
        setBackground(ModernUITheme.BG_TERTIARY);
        setForeground(ModernUITheme.TEXT_PRIMARY);
        setCaretColor(ModernUITheme.PRIMARY_COLOR);
        setBorder(new RoundedBorder(borderRadius, padding));
        setOpaque(false);
        setPreferredSize(new Dimension(250, 40));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(ModernUITheme.BG_TERTIARY);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // Draw border
        if (isFocused) {
            g2d.setColor(ModernUITheme.PRIMARY_COLOR);
            g2d.setStroke(new BasicStroke(2));
        } else {
            g2d.setColor(ModernUITheme.BORDER_COLOR);
            g2d.setStroke(new BasicStroke(1));
        }
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius);

        super.paintComponent(g);
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final int padding;

        RoundedBorder(int radius, int padding) {
            this.radius = radius;
            this.padding = padding;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(padding, padding + 5, padding, padding + 5);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
