package coffee.util;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.EventObject;

public class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private final JPanel editorPanel = new JPanel(new BorderLayout(4, 0));
    private final JButton minusButton = new JButton("-");
    private final JTextField quantityField = new JTextField();
    private final JButton plusButton = new JButton("+");

    private final JPanel renderPanel = new JPanel(new BorderLayout(4, 0));
    private final JLabel renderMinus = new JLabel("-", SwingConstants.CENTER);
    private final JLabel renderValue = new JLabel("0", SwingConstants.CENTER);
    private final JLabel renderPlus = new JLabel("+", SwingConstants.CENTER);

    public QuantityCellEditor() {
        setupEditorPanel();
        setupRenderPanel();
    }

    public static void installOn(JTable table, int columnIndex) {
        QuantityCellEditor quantityCellEditor = new QuantityCellEditor();
        table.getColumnModel().getColumn(columnIndex).setCellEditor(quantityCellEditor);
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(quantityCellEditor);
    }

    private void setupEditorPanel() {
        minusButton.setMargin(new Insets(0, 0, 0, 0));
        plusButton.setMargin(new Insets(0, 0, 0, 0));
        minusButton.setFocusable(false);
        plusButton.setFocusable(false);
        minusButton.setPreferredSize(new Dimension(24, 22));
        plusButton.setPreferredSize(new Dimension(24, 22));

        quantityField.setHorizontalAlignment(SwingConstants.CENTER);

        minusButton.addActionListener(e -> {
            setQuantity(Math.max(0, getQuantity() - 1));
            fireEditingStopped();
        });

        plusButton.addActionListener(e -> {
            setQuantity(getQuantity() + 1);
            fireEditingStopped();
        });

        editorPanel.setOpaque(true);
        editorPanel.add(minusButton, BorderLayout.WEST);
        editorPanel.add(quantityField, BorderLayout.CENTER);
        editorPanel.add(plusButton, BorderLayout.EAST);
    }

    private void setupRenderPanel() {
        renderPanel.setOpaque(true);
        renderPanel.add(renderMinus, BorderLayout.WEST);
        renderPanel.add(renderValue, BorderLayout.CENTER);
        renderPanel.add(renderPlus, BorderLayout.EAST);

        renderMinus.setForeground(new Color(120, 120, 120));
        renderPlus.setForeground(new Color(120, 120, 120));
        renderValue.setForeground(new Color(50, 50, 50));
        renderMinus.setPreferredSize(new Dimension(16, 22));
        renderPlus.setPreferredSize(new Dimension(16, 22));
    }

    @Override
    public Object getCellEditorValue() {
        return quantityField.getText().trim();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        quantityField.setText(String.valueOf(value == null ? "0" : value));
        editorPanel.setBackground(table.getSelectionBackground());
        quantityField.setBackground(table.getSelectionBackground());
        quantityField.setForeground(table.getSelectionForeground());
        return editorPanel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderValue.setText(String.valueOf(value == null ? "0" : value));

        Color bg = isSelected ? table.getSelectionBackground() : table.getBackground();
        Color fg = isSelected ? table.getSelectionForeground() : table.getForeground();

        renderPanel.setBackground(bg);
        renderValue.setForeground(fg);
        return renderPanel;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    private int getQuantity() {
        String text = quantityField.getText() == null ? "0" : quantityField.getText().trim();
        if (text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            if (text.startsWith("+") || text.startsWith("-")) {
                try {
                    return Integer.parseInt(text.substring(1));
                } catch (NumberFormatException ignored) {
                    return 0;
                }
            }
            return 0;
        }
    }

    private void setQuantity(int value) {
        quantityField.setText(String.valueOf(value));
    }
}
