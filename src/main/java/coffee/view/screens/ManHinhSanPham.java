package coffee.view.screens;

import coffee.model.SanPham;
import coffee.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ManHinhSanPham extends JPanel {
    public final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Mã", "Tên", "Loại", "Giá", "Mô tả", "Hình ảnh"}, 0);
    public final JTable table = new JTable(tableModel);
    public final JTextField idField = new ModernTextField(15);
    public final JTextField nameField = new ModernTextField(15);
    public final JTextField categoryField = new ModernTextField(15);
    public final JTextField priceField = new ModernTextField(15);
    public final JTextArea descriptionArea = new JTextArea(4, 20);
    public final JTextField imagePathField = new ModernTextField(15);
    public final JButton browseImageButton = new ModernButton("Chọn ảnh", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JLabel imagePreviewLabel = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
    public final JButton addButton = new ModernButton("Thêm", ModernUITheme.SUCCESS_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton updateButton = new ModernButton("Sửa", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton deleteButton = new ModernButton("Xóa", ModernUITheme.DANGER_COLOR, ModernUITheme.TEXT_PRIMARY);

    public ManHinhSanPham() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(242, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildTopCard(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
    }

    private JPanel buildTopCard() {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 233, 237), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("THÔNG TIN CHI TIẾT SẢN PHẨM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(52, 73, 94));
        card.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel imageBox = buildImageBox();
        gbc.gridx = 0;
        gbc.weightx = 0;
        content.add(imageBox, gbc);

        JPanel details = buildDetailsPanel();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        content.add(details, gbc);

        card.add(content, BorderLayout.CENTER);
        card.add(buildActionButtons(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildImageBox() {
        JPanel imageBox = new JPanel(new BorderLayout(8, 8));
        imageBox.setOpaque(false);

        imagePreviewLabel.setPreferredSize(new Dimension(160, 160));
        imagePreviewLabel.setBorder(new LineBorder(new Color(240, 240, 240), 2));
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setBackground(Color.WHITE);
        imagePreviewLabel.setForeground(new Color(120, 120, 120));
        imagePreviewLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        imagePreviewLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        JPanel pathPanel = new JPanel(new BorderLayout(8, 0));
        pathPanel.setOpaque(false);
        
        pathPanel.add(browseImageButton, BorderLayout.EAST);

        imageBox.add(imagePreviewLabel, BorderLayout.CENTER);
        imageBox.add(pathPanel, BorderLayout.SOUTH);
        return imageBox;
    }

    private JPanel buildDetailsPanel() {
        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        addInput(details, "Mã sản phẩm", idField, gbc);

        gbc.gridx = 1;
        addInput(details, "Tên sản phẩm", nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addInput(details, "Danh mục", categoryField, gbc);

        gbc.gridx = 1;
        addInput(details, "Giá bán", priceField, gbc);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setForeground(new Color(55, 55, 55));
        descriptionArea.setBorder(new LineBorder(new Color(220, 225, 230), 1, true));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(200, 80));
        descriptionScroll.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        addInput(details, "Mô tả", descriptionScroll, gbc);

        return details;
    }

    private JPanel buildActionButtons() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setOpaque(false);
        addButton.setPreferredSize(new Dimension(110, 38));
        updateButton.setPreferredSize(new Dimension(110, 38));
        deleteButton.setPreferredSize(new Dimension(110, 38));
        footer.add(addButton);
        footer.add(updateButton);
        footer.add(deleteButton);
        return footer;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 233, 237), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("DANH SÁCH SẢN PHẨM");
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

    public void capNhatXemTruocAnh(String duongDan) {
        imagePathField.setText(duongDan == null ? "" : duongDan);
        if (duongDan == null || duongDan.isBlank()) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("Chưa có ảnh");
            return;
        }

        File file = new File(duongDan);
        if (!file.exists()) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("Không tìm thấy ảnh");
            return;
        }

        ImageIcon icon = new ImageIcon(duongDan);
        Image scaled = icon.getImage().getScaledInstance(220, 120, Image.SCALE_SMOOTH);
        imagePreviewLabel.setIcon(new ImageIcon(scaled));
        imagePreviewLabel.setText(file.getName());
    }

    public void capNhatTuSanPham(SanPham sanPham) {
        if (sanPham == null) {
            return;
        }
        idField.setText(String.valueOf(sanPham.getMa()));
        nameField.setText(sanPham.getTen());
        categoryField.setText(sanPham.getDanhMuc());
        priceField.setText(String.valueOf((long) sanPham.getGia()));
        descriptionArea.setText(sanPham.getMoTa() == null ? "" : sanPham.getMoTa());
        capNhatXemTruocAnh(sanPham.getDuongDanHinhAnh());
    }
}
