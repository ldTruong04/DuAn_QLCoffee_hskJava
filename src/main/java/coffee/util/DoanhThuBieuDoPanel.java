package coffee.util;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DoanhThuBieuDoPanel extends JPanel {
    private String title;
    private LinkedHashMap<String, Double> data = new LinkedHashMap<>();
    private LinkedHashMap<String, Double> lowerData = new LinkedHashMap<>();
    private LinkedHashMap<String, Double> upperData = new LinkedHashMap<>();
    private boolean stacked;
    private String lowerLegend = "Tiền mặt";
    private String upperLegend = "Chuyển khoản";
    private final Color singleColor = new Color(46, 170, 103);
    private final Color lowerColor = new Color(255,0,255);
    private final Color upperColor = new Color(231, 76, 60);

    public DoanhThuBieuDoPanel(String title) {
        this.title = title;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 229, 233), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    public void setChartData(LinkedHashMap<String, Double> data) {
        stacked = false;
        this.data = data == null ? new LinkedHashMap<>() : new LinkedHashMap<>(data);
        repaint();
    }

    public void setStackedChartData(LinkedHashMap<String, Double> lowerData,
                                    LinkedHashMap<String, Double> upperData,
                                    String lowerLegend,
                                    String upperLegend) {
        stacked = true;
        this.lowerData = lowerData == null ? new LinkedHashMap<>() : new LinkedHashMap<>(lowerData);
        this.upperData = upperData == null ? new LinkedHashMap<>() : new LinkedHashMap<>(upperData);
        this.lowerLegend = (lowerLegend == null || lowerLegend.isBlank()) ? "Tiền mặt" : lowerLegend;
        this.upperLegend = (upperLegend == null || upperLegend.isBlank()) ? "Chuyển khoản" : upperLegend;
        repaint();
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int left = 50;
        int right = 20;
        int top = 36;
        int bottom = 55;

        g2.setColor(new Color(52, 73, 94));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString(title, 8, 18);

        LinkedHashMap<String, Double> renderData = stacked ? mergeStackedTotal() : data;
        if (renderData.isEmpty()) {
            g2.setColor(new Color(130, 130, 130));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("Chưa có dữ liệu", 12, 40);
            g2.dispose();
            return;
        }

        double max = 0;
        for (double value : renderData.values()) {
            if (value > max) {
                max = value;
            }
        }
        if (max <= 0) {
            max = 1;
        }

        int chartW = Math.max(100, w - left - right);
        int chartH = Math.max(60, h - top - bottom);

        g2.setColor(new Color(232, 236, 240));
        for (int i = 0; i <= 4; i++) {
            int y = top + (chartH * i / 4);
            g2.drawLine(left, y, left + chartW, y);
        }

        int n = Math.max(1, renderData.size());
        int slot = chartW / n;
        int barW = Math.max(16, Math.min(40, slot - 12));

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int idx = 0;
        for (Map.Entry<String, Double> e : renderData.entrySet()) {
            int xCenter = left + (slot * idx) + (slot / 2);
            int barX = xCenter - (barW / 2);
            double totalValue = e.getValue();
            int barH = (int) Math.round((totalValue / max) * chartH);
            int barY = top + chartH - barH;

            if (stacked) {
                double lower = lowerData.getOrDefault(e.getKey(), 0.0);
                double upper = upperData.getOrDefault(e.getKey(), 0.0);
                int lowerH = (int) Math.round((lower / max) * chartH);
                int upperH = (int) Math.round((upper / max) * chartH);

                int lowerY = top + chartH - lowerH;
                int upperY = lowerY - upperH;

                g2.setColor(lowerColor);
                g2.fillRoundRect(barX, lowerY, barW, lowerH, 8, 8);

                g2.setColor(upperColor);
                g2.fillRoundRect(barX, upperY, barW, upperH, 8, 8);
            } else {
                g2.setColor(singleColor);
                g2.fillRoundRect(barX, barY, barW, barH, 8, 8);
            }

            g2.setColor(new Color(80, 80, 80));
            String valueLabel = String.format("%.0f", totalValue);
            int valueW = g2.getFontMetrics().stringWidth(valueLabel);
            g2.drawString(valueLabel, xCenter - valueW / 2, Math.max(top + 12, barY - 4));

            String key = e.getKey();
            int keyW = g2.getFontMetrics().stringWidth(key);
            g2.drawString(key, xCenter - keyW / 2, top + chartH + 16);
            idx++;
        }

        g2.setColor(new Color(150, 150, 150));
        g2.drawLine(left, top + chartH, left + chartW, top + chartH);
        g2.drawLine(left, top, left, top + chartH);

        if (stacked) {
            drawLegend(g2, w);
        }

        g2.dispose();
    }

    private LinkedHashMap<String, Double> mergeStackedTotal() {
        LinkedHashMap<String, Double> merged = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : lowerData.entrySet()) {
            merged.put(e.getKey(), e.getValue() + upperData.getOrDefault(e.getKey(), 0.0));
        }
        for (Map.Entry<String, Double> e : upperData.entrySet()) {
            merged.putIfAbsent(e.getKey(), lowerData.getOrDefault(e.getKey(), 0.0) + e.getValue());
        }
        return merged;
    }

    private void drawLegend(Graphics2D g2, int width) {
        int legendY = 22;
        int marker = 10;

        String upperText = upperLegend;
        String lowerText = lowerLegend;
        int upperW = g2.getFontMetrics().stringWidth(upperText);
        int lowerW = g2.getFontMetrics().stringWidth(lowerText);
        int gap = 18;
        int totalW = marker + 6 + lowerW + gap + marker + 6 + upperW;
        int startX = Math.max(120, width - totalW - 12);

        g2.setColor(lowerColor);
        g2.fillRect(startX, legendY - marker + 1, marker, marker);
        g2.setColor(new Color(80, 80, 80));
        g2.drawString(lowerText, startX + marker + 6, legendY);

        int secondX = startX + marker + 6 + lowerW + gap;
        g2.setColor(upperColor);
        g2.fillRect(secondX, legendY - marker + 1, marker, marker);
        g2.setColor(new Color(80, 80, 80));
        g2.drawString(upperText, secondX + marker + 6, legendY);
    }
}
