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

    public final JTabbedPane tabbedPane = new JTabbedPane();
    
    // Kitchen Order components
    public final DefaultTableModel kitchenOrderTableModel = new DefaultTableModel(new Object[]{"Hóa đơn", "Thời gian", "Bàn", "Tên món", "SL", "Trạng thái"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable kitchenOrderTable = new JTable(kitchenOrderTableModel);
    public final JButton completeOrderButton = new ModernButton("Hoàn thành", ModernUITheme.SUCCESS_COLOR, Color.WHITE);
    public final JButton cancelOrderButton = new ModernButton("Hủy bỏ", ModernUITheme.DANGER_COLOR, Color.WHITE);

    public ManHinhSanPham() {
        setLayout(new BorderLayout());
        setBackground(new Color(242, 245, 248));

        // Hide id field, use it only for internal tracking
        idField.setVisible(false);

        // Setup Product Panel
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBackground(new Color(242, 245, 248));
        productPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JSplitPane productSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildTopCard(), buildTableCard());
        productSplit.setResizeWeight(0.50);
        productSplit.setContinuousLayout(true);
        productSplit.setBorder(null);
        productSplit.setDividerSize(6);
        productSplit.setOneTouchExpandable(true);
        productPanel.add(productSplit, BorderLayout.CENTER);

        // Setup Kitchen Order Panel
        JPanel kitchenPanel = buildKitchenPanel();

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.addTab("Quản lý Sản phẩm", productPanel);
        tabbedPane.addTab("Đơn hàng Bếp", kitchenPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildKitchenPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("DANH SÁCH MÓN ĐANG CHỜ CHẾ BIẾN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(ModernUITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        kitchenOrderTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(kitchenOrderTable);
        JScrollPane scrollPane = new JScrollPane(kitchenOrderTable);
        ModernStyler.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setBackground(Color.WHITE);
        completeOrderButton.setPreferredSize(new Dimension(130, 38));
        cancelOrderButton.setPreferredSize(new Dimension(110, 38));
        footer.add(completeOrderButton);
        footer.add(cancelOrderButton);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
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

        // Horizontal split: Image on left, Details on right
        JPanel imageSection = buildImageSection();
        JPanel detailsSection = buildDetailsSection();
        
        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imageSection, detailsSection);
        horizontalSplit.setBorder(null);
        horizontalSplit.setDividerLocation(200);
        horizontalSplit.setResizeWeight(0.25);
        horizontalSplit.setContinuousLayout(true);
        
        card.add(horizontalSplit, BorderLayout.CENTER);
        card.add(buildActionButtons(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildImageSection() {
        JPanel imageSection = new JPanel(new BorderLayout(0, 12));
        imageSection.setOpaque(false);

        imagePreviewLabel.setPreferredSize(new Dimension(160, 160));
        imagePreviewLabel.setMaximumSize(new Dimension(160, 160));
        imagePreviewLabel.setBorder(new LineBorder(new Color(240, 240, 240), 2));
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setBackground(Color.WHITE);
        imagePreviewLabel.setForeground(new Color(120, 120, 120));
        imagePreviewLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        imagePreviewLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        browseImageButton.setPreferredSize(new Dimension(160, 38));
        
        imageSection.add(imagePreviewLabel, BorderLayout.NORTH);
        imageSection.add(browseImageButton, BorderLayout.CENTER);
        imageSection.add(Box.createVerticalGlue(), BorderLayout.SOUTH);
        
        return imageSection;
    }

    private JPanel buildDetailsSection() {
        JPanel detailsSection = new JPanel(new BorderLayout(0, 0));
        detailsSection.setOpaque(false);
        
        JPanel details = buildDetailsPanel();
        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        detailsSection.add(scroll, BorderLayout.CENTER);
        return detailsSection;
    }

private JPanel buildDetailsPanel() {
    JPanel details = new JPanel(new GridBagLayout());
    details.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các ô

    // Hàng 1: Tên sản phẩm (Chiếm toàn bộ chiều ngang)
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
    details.add(createInputGroup("Tên sản phẩm", nameField), gbc);

    // Hàng 2: Danh mục (Cột 1) và Giá bán (Cột 2)
    gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.5;
    details.add(createInputGroup("Danh mục", categoryField), gbc);
    
    gbc.gridx = 1;
    details.add(createInputGroup("Giá bán", priceField), gbc);

    // Hàng 3: Mô tả (Chiếm toàn bộ chiều ngang)
    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
    
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    descriptionArea.setBorder(new LineBorder(new Color(220, 225, 230), 1, true));
    JScrollPane descScroll = new JScrollPane(descriptionArea);
    descScroll.setPreferredSize(new Dimension(0, 80)); // Giảm chiều cao mô tả
    
    JPanel descPanel = new JPanel(new BorderLayout(0, 5));
    descPanel.setOpaque(false);
    JLabel descLabel = new JLabel("Mô tả");
    descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    descPanel.add(descLabel, BorderLayout.NORTH);
    descPanel.add(descScroll, BorderLayout.CENTER);
    
    details.add(descPanel, gbc);

    return details;
}

// Hàm hỗ trợ tạo label + field nhanh
private JPanel createInputGroup(String labelText, JTextField field) {
    JPanel panel = new JPanel(new BorderLayout(0, 5));
    panel.setOpaque(false);
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
    label.setForeground(new Color(100, 100, 100));
    panel.add(label, BorderLayout.NORTH);
    panel.add(field, BorderLayout.CENTER);
    return panel;
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

        table.setFillsViewportHeight(true);
        ModernStyler.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        ModernStyler.styleScrollPane(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
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
        Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
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
