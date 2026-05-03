package coffee.view.screens;

import coffee.util.ModernButton;
import coffee.util.ModernStyler;
import coffee.util.ModernTextField;
import coffee.util.ModernUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManHinhKhuyenMai extends JPanel {
    public final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã", "Code", "Loại", "Giá trị", "Trạng thái"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable table = new JTable(tableModel);

    public final JTextField idField = new ModernTextField(8);
    public final JTextField codeField = new ModernTextField(14);
    public final JComboBox<String> loaiBox = new JComboBox<>(new String[]{"Phần trăm (%)", "Giảm trực tiếp (VND)"});
    public final JTextField giaTriField = new ModernTextField(14);
    public final JCheckBox activeBox = new JCheckBox("Đang áp dụng", true);

    public final JButton addButton = new ModernButton("Tạo mã", ModernUITheme.SUCCESS_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton updateButton = new ModernButton("Cập nhật", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton deleteButton = new ModernButton("Xóa mã", ModernUITheme.DANGER_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton clearButton = new ModernButton("Làm mới", ModernUITheme.BG_TERTIARY, ModernUITheme.TEXT_PRIMARY);

    public ManHinhKhuyenMai() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(242, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildTopCard(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        idField.setEditable(false);
        idField.setVisible(false);
        loaiBox.setFont(ModernUITheme.FONT_SMALL);
        activeBox.setOpaque(false);
        activeBox.setFont(ModernUITheme.FONT_SMALL);
    }

    private JPanel buildTopCard() {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 233, 237), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("THIẾT LẬP KHUYẾN MÃI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(52, 73, 94));
        card.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addInput(form, "Code khuyến mãi", codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        addInput(form, "Loại giảm", loaiBox, gbc);

        gbc.gridx = 1;
        addInput(form, "Giá trị", giaTriField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel checkWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkWrap.setOpaque(false);
        checkWrap.add(activeBox);
        form.add(checkWrap, gbc);

        card.add(form, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setOpaque(false);
        addButton.setPreferredSize(new Dimension(110, 36));
        updateButton.setPreferredSize(new Dimension(110, 36));
        deleteButton.setPreferredSize(new Dimension(110, 36));
        clearButton.setPreferredSize(new Dimension(110, 36));
        actionPanel.add(addButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(clearButton);

        card.add(actionPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 233, 237), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("DANH SÁCH MÃ KHUYẾN MÃI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(127, 140, 141));
        card.add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        ModernStyler.styleScrollPane(scrollPane);
        ModernStyler.styleTable(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void addInput(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(100, 100, 100));
        group.add(label, BorderLayout.NORTH);
        if (field instanceof JTextField) {
            field.setPreferredSize(new Dimension(220, 34));
        }
        group.add(field, BorderLayout.CENTER);
        panel.add(group, gbc);
    }

    public void clearForm() {
        idField.setText("");
        codeField.setText("");
        loaiBox.setSelectedIndex(0);
        giaTriField.setText("");
        activeBox.setSelected(true);
    }
}
