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

public class ManHinhHoaDon extends JPanel {
    public final JRadioButton taiBanRadio = new JRadioButton("Tại bàn", true);
    public final JRadioButton mangDiRadio = new JRadioButton("Bán mang đi");
    public final ButtonGroup typeGroup = new ButtonGroup();
    public final JComboBox<BanCafe> tableBox = new JComboBox<>();
    public final JTextField customerNameField = new ModernTextField(15);
    public final JTextField customerPhoneField = new ModernTextField(15);
    public final JLabel employeeNameLabel = new JLabel("Nhân viên: -");
    public final JLabel invoiceInfoLabel = new JLabel("Hóa đơn hiện tại: Chưa có");
    public final JLabel totalLabel = new JLabel("Tổng cộng: 0 VND");

    public final DefaultTableModel invoiceItemTableModel = new DefaultTableModel(new Object[]{"Tên món", "Số lượng", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable invoiceItemTable = new JTable(invoiceItemTableModel);

    public final DefaultTableModel storeProductTableModel = new DefaultTableModel(new Object[]{"Tên món", "Loại", "Giá"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable storeProductTable = new JTable(storeProductTableModel);
    public final JLabel selectedProductLabel = new JLabel("Món đã chọn: -");
    public final JTextField quantityField = new ModernTextField(8);
    public final JButton addProductButton = new ModernButton("Thêm món", ModernUITheme.SUCCESS_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton increaseItemButton = new ModernButton("+", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton decreaseItemButton = new ModernButton("-", ModernUITheme.DANGER_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton payButton = new ModernButton("Thanh toán", ModernUITheme.PRIMARY_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton printButton = new ModernButton("In chi tiết", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);

    private List<SanPham> storeProducts = List.of();
    private List<ChiTietHoaDon> currentInvoiceItems = List.of();

    public ManHinhHoaDon() {
        setLayout(new BorderLayout(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD,
            ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));

        typeGroup.add(taiBanRadio);
        typeGroup.add(mangDiRadio);
        styleRadio(taiBanRadio);
        styleRadio(mangDiRadio);
        styleCombo(tableBox);

        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.50);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_MD));
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel title = new JLabel("THANH TOÁN");
        title.setFont(ModernUITheme.FONT_HEADING);
        title.setForeground(ModernUITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        content.setBackground(ModernUITheme.BG_PRIMARY);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernUITheme.BG_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_SM, ModernUITheme.PADDING_SM);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        typePanel.setBackground(ModernUITheme.BG_PRIMARY);
        typePanel.add(taiBanRadio);
        typePanel.add(mangDiRadio);
        formPanel.add(typePanel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(createFieldLabel("Chọn bàn"), gbc);
        gbc.gridx = 2;
        formPanel.add(tableBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFieldLabel("Tên khách hàng"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(customerNameField, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFieldLabel("Số điện thoại"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(customerPhoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        employeeNameLabel.setFont(ModernUITheme.FONT_SMALL);
        formPanel.add(employeeNameLabel, gbc);

        gbc.gridx = 1;
        invoiceInfoLabel.setFont(ModernUITheme.FONT_SMALL);
        invoiceInfoLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        formPanel.add(invoiceInfoLabel, gbc);

        tableBox.setPreferredSize(new Dimension(170, 30));
        customerNameField.setPreferredSize(new Dimension(160, 30));
        customerPhoneField.setPreferredSize(new Dimension(160, 30));

        content.add(formPanel, BorderLayout.NORTH);

        JPanel itemPanel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        itemPanel.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel itemTitle = new JLabel("CHI TIẾT THANH TOÁN");
        itemTitle.setFont(ModernUITheme.FONT_HEADING);
        itemTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        itemPanel.add(itemTitle, BorderLayout.NORTH);

        invoiceItemTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(invoiceItemTable);
        JScrollPane itemScroll = new JScrollPane(invoiceItemTable);
        ModernStyler.styleScrollPane(itemScroll);
        itemPanel.add(itemScroll, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, ModernUITheme.PADDING_SM, 0));
        bottomBar.setBackground(ModernUITheme.BG_PRIMARY);
        totalLabel.setFont(ModernUITheme.FONT_SMALL);
        totalLabel.setForeground(ModernUITheme.PRIMARY_DARK);
        payButton.setPreferredSize(new Dimension(100, 30));
        printButton.setPreferredSize(new Dimension(100, 30));
        bottomBar.add(totalLabel);
        JLabel editLabel = new JLabel("Số lượng");
        editLabel.setFont(ModernUITheme.FONT_SMALL);
        bottomBar.add(editLabel);
        increaseItemButton.setPreferredSize(new Dimension(48, 30));
        decreaseItemButton.setPreferredSize(new Dimension(48, 30));
        bottomBar.add(increaseItemButton);
        bottomBar.add(decreaseItemButton);
        bottomBar.add(payButton);
        bottomBar.add(printButton);
        itemPanel.add(bottomBar, BorderLayout.SOUTH);

        content.add(itemPanel, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_MD));
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel title = new JLabel("DANH SÁCH MÓN CỬA HÀNG");
        title.setFont(ModernUITheme.FONT_HEADING);
        title.setForeground(ModernUITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        storeProductTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(storeProductTable);
        JScrollPane productScroll = new JScrollPane(storeProductTable);
        ModernStyler.styleScrollPane(productScroll);
        panel.add(productScroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(ModernUITheme.BG_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_SM, ModernUITheme.PADDING_SM);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        selectedProductLabel.setFont(ModernUITheme.FONT_BODY);
        selectedProductLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        actionPanel.add(selectedProductLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        actionPanel.add(createFieldLabel("Số lượng"), gbc);
        gbc.gridx = 1;
        quantityField.setText("1");
        quantityField.setPreferredSize(new Dimension(72, 28));
        actionPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addProductButton.setPreferredSize(new Dimension(110, 30));
        actionPanel.add(addProductButton, gbc);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernUITheme.FONT_SMALL);
        label.setForeground(ModernUITheme.TEXT_PRIMARY);
        return label;
    }

    private void styleCombo(JComboBox<?> comboBox) {
        comboBox.setBackground(ModernUITheme.BG_PRIMARY);
        comboBox.setForeground(ModernUITheme.TEXT_PRIMARY);
        comboBox.setFont(ModernUITheme.FONT_SMALL);
        comboBox.setPreferredSize(new Dimension(170, 30));
    }

    private void styleRadio(JRadioButton radioButton) {
        radioButton.setOpaque(false);
        radioButton.setFont(ModernUITheme.FONT_SMALL);
        radioButton.setForeground(ModernUITheme.TEXT_PRIMARY);
    }

    public void setTables(List<BanCafe> tables, BanCafe selected) {
        tableBox.removeAllItems();
        for (BanCafe table : tables) {
            tableBox.addItem(table);
        }
        if (selected != null) {
            for (int i = 0; i < tableBox.getItemCount(); i++) {
                BanCafe item = tableBox.getItemAt(i);
                if (item.getMa() == selected.getMa()) {
                    tableBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public BanCafe getSelectedTable() {
        Object selected = tableBox.getSelectedItem();
        if (selected instanceof BanCafe table) {
            return table;
        }
        return null;
    }

    public boolean isTakeawaySelected() {
        return mangDiRadio.isSelected();
    }

    public int getQuantity() {
        return Integer.parseInt(quantityField.getText().trim());
    }

    public ChiTietHoaDon getSelectedInvoiceItem() {
        int row = invoiceItemTable.getSelectedRow();
        if (row < 0 || row >= currentInvoiceItems.size()) {
            return null;
        }
        return currentInvoiceItems.get(row);
    }

    public void setStoreProducts(List<SanPham> products) {
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

    public SanPham getSelectedStoreProduct() {
        int row = storeProductTable.getSelectedRow();
        if (row < 0 || row >= storeProducts.size()) {
            return null;
        }
        return storeProducts.get(row);
    }

    public void setInvoiceItems(List<ChiTietHoaDon> items, Integer invoiceId) {
        currentInvoiceItems = new ArrayList<>(items);
        invoiceItemTableModel.setRowCount(0);
        double total = 0;
        for (ChiTietHoaDon item : currentInvoiceItems) {
            invoiceItemTableModel.addRow(new Object[]{
                    item.getSanPham().getTen(),
                    item.getSoLuong(),
                    String.format("%.0f", item.getThanhTien())
            });
            total += item.getThanhTien();
        }
        totalLabel.setText("Tổng cộng: " + String.format("%.0f", total) + " VND");
        invoiceInfoLabel.setText("Hóa đơn hiện tại: " + (invoiceId == null ? "Chưa có" : "#" + invoiceId));
        boolean hasItems = !currentInvoiceItems.isEmpty();
        increaseItemButton.setEnabled(hasItems);
        decreaseItemButton.setEnabled(hasItems);
    }

    public void setEmployeeName(String name) {
        employeeNameLabel.setText("Nhân viên: " + (name == null || name.isBlank() ? "-" : name));
    }

    public void setTableSelectionEnabled(boolean enabled) {
        tableBox.setEnabled(enabled);
    }

    public void bindStoreProductSelection() {
        storeProductTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            SanPham product = getSelectedStoreProduct();
            if (product == null) {
                selectedProductLabel.setText("Món đã chọn: -");
                return;
            }
            selectedProductLabel.setText("Món đã chọn: " + product.getTen() + " - " + String.format("%.0f", product.getGia()) + " VND");
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

    public void bindInvoiceTypeChange(Runnable action) {
        taiBanRadio.addActionListener(e -> action.run());
        mangDiRadio.addActionListener(e -> action.run());
    }

    public void bindTableChange(Runnable action) {
        tableBox.addActionListener(e -> action.run());
    }

    public void setActionButtonsEnabled(boolean enabled) {
        addProductButton.setEnabled(enabled);
        payButton.setEnabled(enabled);
        printButton.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        if (!enabled) {
            increaseItemButton.setEnabled(false);
            decreaseItemButton.setEnabled(false);
        }
    }
}
