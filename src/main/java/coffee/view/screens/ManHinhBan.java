package coffee.view.screens;

import coffee.model.BanCafe;
import coffee.model.ChiTietHoaDon;
import coffee.model.HoaDon;
import coffee.model.SanPham;
import coffee.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class ManHinhBan extends JPanel {
    public final JPanel tableGridPanel = new JPanel(new GridLayout(0, 2, ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));
    public final DefaultTableModel itemTableModel = new DefaultTableModel(new Object[]{"Tên món", "Số lượng", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable itemTable = new JTable(itemTableModel);
    public final DefaultTableModel storeProductTableModel = new DefaultTableModel(new Object[]{"Tên món", "Loại", "Giá"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable storeProductTable = new JTable(storeProductTableModel);
    public final JLabel selectedTableLabel = new JLabel("Bàn: Chưa chọn");
    public final JLabel selectedStatusLabel = new JLabel("Trạng thái: -");
    public final JLabel selectedInvoiceLabel = new JLabel("Hóa đơn: -");
    public final JLabel totalLabel = new JLabel("Tổng cộng: 0 VND");
    public final JLabel selectedStoreProductLabel = new JLabel("Món đã chọn: -");
    public final JTextField quantityField = new ModernTextField(8);
    public final JButton orderButton = new ModernButton("Thêm vào bàn", ModernUITheme.SUCCESS_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton increaseItemButton = new ModernButton("+", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton decreaseItemButton = new ModernButton("-", ModernUITheme.DANGER_COLOR, ModernUITheme.TEXT_PRIMARY);
    private List<SanPham> storeProducts = List.of();
    private List<ChiTietHoaDon> currentOrderItems = List.of();

    public ManHinhBan() {
        setLayout(new BorderLayout(ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG));

        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(330);
        splitPane.setResizeWeight(0.42);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_MD));
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        JPanel tableSection = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        tableSection.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel tableTitle = new JLabel("DANH SÁCH BÀN");
        tableTitle.setFont(ModernUITheme.FONT_HEADING);
        tableTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        tableSection.add(tableTitle, BorderLayout.NORTH);

        tableGridPanel.setBackground(ModernUITheme.BG_PRIMARY);
        tableGridPanel.setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_SM, 0, ModernUITheme.PADDING_SM, 0));

        JScrollPane tableScroll = new JScrollPane(tableGridPanel);
        ModernStyler.styleScrollPane(tableScroll);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableSection.add(tableScroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("Nhấn vào bàn để xem/chọn gọi món");
        hint.setForeground(ModernUITheme.TEXT_SECONDARY);
        hint.setFont(ModernUITheme.FONT_SMALL);
        tableSection.add(hint, BorderLayout.SOUTH);

        JPanel orderDetailSection = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        orderDetailSection.setBackground(ModernUITheme.BG_PRIMARY);
        orderDetailSection.setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_MD, 0, 0, 0));

        JLabel detailTitle = new JLabel("CHI TIẾT MÓN CỦA BÀN");
        detailTitle.setFont(ModernUITheme.FONT_HEADING);
        detailTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        orderDetailSection.add(detailTitle, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        infoPanel.setBackground(ModernUITheme.BG_PRIMARY);
        selectedTableLabel.setFont(ModernUITheme.FONT_BODY);
        selectedStatusLabel.setFont(ModernUITheme.FONT_BODY);
        selectedInvoiceLabel.setFont(ModernUITheme.FONT_BODY);
        totalLabel.setFont(ModernUITheme.FONT_BODY);
        totalLabel.setForeground(ModernUITheme.PRIMARY_DARK);
        infoPanel.add(selectedTableLabel);
        infoPanel.add(selectedStatusLabel);
        infoPanel.add(selectedInvoiceLabel);
        infoPanel.add(totalLabel);

        JPanel detailContent = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        detailContent.setBackground(ModernUITheme.BG_PRIMARY);
        detailContent.add(infoPanel, BorderLayout.NORTH);

        itemTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(itemTable);
        JScrollPane itemScroll = new JScrollPane(itemTable);
        ModernStyler.styleScrollPane(itemScroll);
        detailContent.add(itemScroll, BorderLayout.CENTER);

        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, ModernUITheme.PADDING_SM, 0));
        editPanel.setBackground(ModernUITheme.BG_PRIMARY);
        JLabel editLabel = new JLabel("Số lượng");
        editLabel.setFont(ModernUITheme.FONT_SMALL);
        editPanel.add(editLabel);
        increaseItemButton.setPreferredSize(new Dimension(48, 28));
        decreaseItemButton.setPreferredSize(new Dimension(48, 28));
        editPanel.add(increaseItemButton);
        editPanel.add(decreaseItemButton);
        detailContent.add(editPanel, BorderLayout.SOUTH);

        orderDetailSection.add(detailContent, BorderLayout.CENTER);

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableSection, orderDetailSection);
        verticalSplit.setBorder(null);
        verticalSplit.setDividerLocation(250);
        verticalSplit.setResizeWeight(0.5);
        verticalSplit.setContinuousLayout(true);
        panel.add(verticalSplit, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_MD));
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel title = new JLabel("DANH SÁCH MÓN CỦA CỬA HÀNG");
        title.setFont(ModernUITheme.FONT_HEADING);
        title.setForeground(ModernUITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        storeProductTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(storeProductTable);
        JScrollPane productScroll = new JScrollPane(storeProductTable);
        ModernStyler.styleScrollPane(productScroll);
        panel.add(productScroll, BorderLayout.CENTER);

        JPanel orderPanel = new JPanel(new GridBagLayout());
        orderPanel.setBackground(ModernUITheme.BG_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_SM, ModernUITheme.PADDING_SM);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        selectedStoreProductLabel.setFont(ModernUITheme.FONT_BODY);
        selectedStoreProductLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        orderPanel.add(selectedStoreProductLabel, gbc);

        JLabel qtyLabel = new JLabel("Số lượng");
        qtyLabel.setFont(ModernUITheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        orderPanel.add(qtyLabel, gbc);
        gbc.gridx = 1;
        quantityField.setText("1");
        quantityField.setPreferredSize(new Dimension(72, 28));
        orderPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        orderButton.setPreferredSize(new Dimension(108, 28));
        orderPanel.add(orderButton, gbc);

        panel.add(orderPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void renderTableCards(List<BanCafe> tables, IntConsumer onSelect, Integer selectedTableId) {
        tableGridPanel.removeAll();
        for (BanCafe table : tables) {
            boolean selected = selectedTableId != null && selectedTableId == table.getMa();
            ModernButton button = new ModernButton(
                    table.getTen() + " - " + (table.isDangSuDung() ? "Đang dùng" : "Trống"),
                    colorForTable(table, selected),
                    Color.WHITE
            );
                button.setPreferredSize(new Dimension(100, 50));
            button.setBaseColor(colorForTable(table, selected));
            button.setToolTipText("Nhấn để xem chi tiết bàn " + table.getTen());
            button.addActionListener(e -> onSelect.accept(table.getMa()));
            tableGridPanel.add(button);
        }
        tableGridPanel.revalidate();
        tableGridPanel.repaint();
    }

    public void showSelectedTable(BanCafe table, HoaDon HoaDonHienTai) {
        if (table == null) {
            selectedTableLabel.setText("Bàn: Chưa chọn");
            selectedStatusLabel.setText("Trạng thái: -");
            selectedInvoiceLabel.setText("Hóa đơn: -");
            totalLabel.setText("Tổng cộng: 0 VND");
            setOrderControlsEnabled(false);
            setOrderItems(List.of());
            return;
        }

        selectedTableLabel.setText("Bàn: " + table.getTen());
        selectedStatusLabel.setText("Trạng thái: " + (table.isDangSuDung() ? "Đang dùng" : "Trống"));
        selectedInvoiceLabel.setText("Hóa đơn: " + (HoaDonHienTai == null ? "Chưa có" : "#" + HoaDonHienTai.getMa()));
        setOrderControlsEnabled(true);
        setOrderItems(HoaDonHienTai == null ? List.of() : HoaDonHienTai.getDanhSachMon());
    }

    public void setProducts(List<SanPham> products) {
        storeProducts = products == null ? List.of() : products;
        storeProductTableModel.setRowCount(0);
        for (SanPham product : storeProducts) {
            storeProductTableModel.addRow(new Object[]{
                    product.getTen(),
                    product.getDanhMuc(),
                    String.format("%.0f", product.getGia())
            });
        }
    }

    public void setOrderItems(List<ChiTietHoaDon> items) {
        currentOrderItems = new ArrayList<>(items);
        itemTableModel.setRowCount(0);
        double total = 0;
        for (ChiTietHoaDon item : currentOrderItems) {
            itemTableModel.addRow(new Object[]{
                    item.getSanPham().getTen(),
                    item.getSoLuong(),
                    String.format("%.0f", item.getThanhTien())
            });
            total += item.getThanhTien();
        }
        totalLabel.setText("Tổng cộng: " + String.format("%.0f", total) + " VND");
        boolean hasItems = !currentOrderItems.isEmpty();
        increaseItemButton.setEnabled(hasItems);
        decreaseItemButton.setEnabled(hasItems);
    }

    public void setOrderControlsEnabled(boolean enabled) {
        storeProductTable.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        orderButton.setEnabled(enabled);
        if (!enabled) {
            increaseItemButton.setEnabled(false);
            decreaseItemButton.setEnabled(false);
        }
    }

    public SanPham getSelectedProduct() {
        int row = storeProductTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        if (row >= storeProducts.size()) {
            return null;
        }
        return storeProducts.get(row);
    }

    public int getQuantity() {
        return Integer.parseInt(quantityField.getText().trim());
    }

    public ChiTietHoaDon getSelectedOrderItem() {
        int row = itemTable.getSelectedRow();
        if (row < 0 || row >= currentOrderItems.size()) {
            return null;
        }
        return currentOrderItems.get(row);
    }

    private Color colorForTable(BanCafe table, boolean selected) {
        if (selected) {
            return ModernUITheme.PRIMARY_COLOR;
        }
        if (table.isDangSuDung()) {
            return new Color(230, 126, 34);
        }
        return ModernUITheme.SUCCESS_COLOR;
    }

    public void bindStoreProductSelection() {
        storeProductTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = storeProductTable.getSelectedRow();
            if (row < 0) {
                selectedStoreProductLabel.setText("Món đã chọn: -");
                return;
            }
            Object name = storeProductTableModel.getValueAt(row, 0);
            Object price = storeProductTableModel.getValueAt(row, 2);
            selectedStoreProductLabel.setText("Món đã chọn: " + name + " - " + price + " VND");
        });
    }

    public void selectFirstProductIfNeeded() {
        if (storeProductTable.getRowCount() == 0) {
            return;
        }
        if (storeProductTable.getSelectedRow() < 0) {
            storeProductTable.setRowSelectionInterval(0, 0);
        }
    }
}
