package coffee.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern custom button with rounded corners, hover effects, and modern styling
 */
public class ModernButton extends JButton {
    private boolean isHovered = false;
    private Color backgroundColor;
    private final Color textColor;
    private final int borderRadius;

    public ModernButton(String text, Color backgroundColor, Color textColor) {
        this(text, backgroundColor, textColor, ModernUITheme.BORDER_RADIUS_MD);
    }

    public ModernButton(String text, Color backgroundColor, Color textColor, int borderRadius) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.borderRadius = borderRadius;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(ModernUITheme.FONT_BUTTON);
        setForeground(textColor);
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(120, 40));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        Color currentColor = isHovered ? brightenColor(backgroundColor, 1.1f) : backgroundColor;
        g2d.setColor(currentColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // Draw text and icon
        g2d.setColor(textColor);
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        Icon icon = getIcon();
        int iconW = icon == null ? 0 : icon.getIconWidth();
        int iconH = icon == null ? 0 : icon.getIconHeight();
        int gap = icon == null ? 0 : Math.max(0, getIconTextGap());
        int textW = fm.stringWidth(getText());
        int totalW = iconW + gap + textW;

        int x = (getWidth() - totalW) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        if (icon != null) {
            int iconY = (getHeight() - iconH) / 2;
            icon.paintIcon(this, g2d, x, iconY);
            x += iconW + gap;
        }
        g2d.drawString(getText(), x, y);
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);
        return shape.contains(x, y);
    }

    public void setBaseColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
        repaint();
    }

    private Color brightenColor(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() * factor));
        int g = Math.min(255, (int) (color.getGreen() * factor));
        int b = Math.min(255, (int) (color.getBlue() * factor));
        return new Color(r, g, b);
    }
}
