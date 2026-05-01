package coffee.view.screens;

import coffee.model.BanCafe;
import coffee.model.ChiTietHoaDon;
import coffee.model.HoaDon;
import coffee.model.KhuyenMai;
import coffee.model.SanPham;
import coffee.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ManHinhHoaDon extends JPanel {
    public final JRadioButton taiBanRadio = new JRadioButton("Tại bàn", true);
    public final JRadioButton mangDiRadio = new JRadioButton("Bán mang đi");
    public final ButtonGroup typeGroup = new ButtonGroup();
    public final JComboBox<BanCafe> tableBox = new JComboBox<>();
    public final JLabel tableStatusValueLabel = new JLabel("Trạng thái: -");
    public final JButton refreshTableStatusButton = new ModernButton("Làm mới", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JTextField customerNameField = new ModernTextField(15);
    public final JTextField customerPhoneField = new ModernTextField(15);
    public final JLabel employeeNameLabel = new JLabel("Nhân viên: -");
    public final JLabel invoiceInfoLabel = new JLabel("Hóa đơn hiện tại: Chưa có");
    public final JLabel subtotalValueLabel = new JLabel("0 VND");
    public final JLabel discountValueLabel = new JLabel("0 VND");
    public final JLabel totalLabel = new JLabel("0 VND");

    public final DefaultTableModel invoiceItemTableModel = new DefaultTableModel(new Object[]{"Tên món", "Số lượng", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1;
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
    public final JButton payCashButton = new ModernButton("Thanh toán", ModernUITheme.PRIMARY_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton payTransferButton = new ModernButton("Chuyển khoản", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton printButton = new ModernButton("In chi tiết", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JTextField cashReceivedField = new ModernTextField(12);
    public final JLabel changeLabel = new JLabel("0 VND");
    public final JButton quickExactButton = new ModernButton("Đủ tiền", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton refreshButton = new ModernButton("", ModernUITheme.WARNING_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JCheckBox exportInvoiceCheckBox = new JCheckBox("Xuất hóa đơn (.fdf)");
    public final JTextField promotionCodeField = new ModernTextField(12);
    public final JComboBox<KhuyenMai> promotionComboBox = new JComboBox<>();
    public final JButton applyPromotionButton = new ModernButton("Áp dụng", ModernUITheme.SUCCESS_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton clearPromotionButton = new ModernButton("", ModernUITheme.BG_TERTIARY, ModernUITheme.TEXT_PRIMARY);
    public final JLabel promotionInfoLabel = new JLabel("Chưa áp dụng khuyến mãi");
    public final JLabel cashWordsLabel = new JLabel("Khách đưa (bằng chữ): Không đồng");
    public final JLabel changeWordsLabel = new JLabel("Tiền thối (bằng chữ): Không đồng");

    private List<SanPham> storeProducts = List.of();
    private List<ChiTietHoaDon> currentInvoiceItems = List.of();
    private List<KhuyenMai> activePromotions = List.of();
    private long tongTienHienTai;
    private long giamGiaHienTai;
    private KhuyenMai khuyenMaiDangApDung;
    private final DecimalFormat moneyFormat;
    private boolean cashFieldInternalUpdate;

    public ManHinhHoaDon() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        moneyFormat = new DecimalFormat("#,###", symbols);

        setLayout(new BorderLayout(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD,
            ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));

        typeGroup.add(taiBanRadio);
        typeGroup.add(mangDiRadio);
        styleRadio(taiBanRadio);
        styleRadio(mangDiRadio);
        styleCombo(tableBox);
        styleCombo(promotionComboBox);
        applyRefreshButtonIcon();
        applyClearPromotionButtonIcon();
        exportInvoiceCheckBox.setOpaque(false);
        exportInvoiceCheckBox.setSelected(false);
        exportInvoiceCheckBox.setFont(ModernUITheme.FONT_SMALL);
        exportInvoiceCheckBox.setForeground(ModernUITheme.TEXT_PRIMARY);

        promotionComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KhuyenMai km) {
                    label.setText(km.getMaCode() + " - " + km.getGiaTriHienThi());
                } else {
                    label.setText("Chọn mã khuyến mãi");
                }
                return label;
            }
        });

        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.33);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        cashReceivedField.setText("0");
        capNhatTongTienHienThi();
        updateCashWords();
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
        JPanel tableSelectionWrap = new JPanel(new BorderLayout(6, 0));
        tableSelectionWrap.setOpaque(false);
        tableBox.setPreferredSize(new Dimension(170, 30));
        refreshTableStatusButton.setPreferredSize(new Dimension(90, 30));
        tableSelectionWrap.add(tableBox, BorderLayout.CENTER);
        tableSelectionWrap.add(refreshTableStatusButton, BorderLayout.EAST);
        formPanel.add(tableSelectionWrap, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(createFieldLabel("Trạng thái bàn"), gbc);
        gbc.gridx = 2;
        tableStatusValueLabel.setFont(ModernUITheme.FONT_SMALL);
        tableStatusValueLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        formPanel.add(tableStatusValueLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
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
        itemScroll.setPreferredSize(new Dimension(100, 180));
        itemScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel tableWrap = new JPanel();
        tableWrap.setLayout(new BoxLayout(tableWrap, BoxLayout.Y_AXIS));
        tableWrap.setOpaque(false);
        tableWrap.add(itemScroll);

        itemPanel.add(tableWrap, BorderLayout.CENTER);
        QuantityCellEditor.installOn(invoiceItemTable, 1);

        // === PAYMENT DASHBOARD - Compact vertical layout ===
        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new BoxLayout(bottomBar, BoxLayout.Y_AXIS));
        bottomBar.setBackground(ModernUITheme.BG_PRIMARY);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 0, 0, 0)
        ));

        // --- Row 1: Summary (Tạm tính / Giảm giá / Thành tiền) + Khuyến mãi ---
        JPanel row1 = new JPanel(new BorderLayout(8, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        // Summary (left)
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setOpaque(false);
        GridBagConstraints sumGbc = new GridBagConstraints();
        sumGbc.insets = new Insets(1, 0, 1, 8);
        sumGbc.anchor = GridBagConstraints.WEST;

        sumGbc.gridx = 0; sumGbc.gridy = 0;
        summaryPanel.add(createSummaryLabel("Tổng tiền:"), sumGbc);
        sumGbc.gridx = 1;
        subtotalValueLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        summaryPanel.add(subtotalValueLabel, sumGbc);

        sumGbc.gridx = 0; sumGbc.gridy = 1;
        summaryPanel.add(createSummaryLabel("Giảm giá:"), sumGbc);
        sumGbc.gridx = 1;
        discountValueLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        discountValueLabel.setForeground(new Color(40, 167, 69));
        summaryPanel.add(discountValueLabel, sumGbc);

        sumGbc.gridx = 0; sumGbc.gridy = 2;
        JLabel tongThuLabel = createSummaryLabel("Tổng thanh toán:");
        tongThuLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        summaryPanel.add(tongThuLabel, sumGbc);
        sumGbc.gridx = 1;
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(new Color(220, 53, 69));
        summaryPanel.add(totalLabel, sumGbc);

        row1.add(summaryPanel, BorderLayout.WEST);

        // Promotion (right)
        JPanel promotionPanel = new JPanel(new GridBagLayout());
        promotionPanel.setOpaque(false);
        promotionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUITheme.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        GridBagConstraints proGbc = new GridBagConstraints();
        proGbc.insets = new Insets(1, 0, 1, 4);
        proGbc.fill = GridBagConstraints.HORIZONTAL;

        proGbc.gridx = 0; proGbc.gridy = 0; proGbc.weightx = 0;
        promotionPanel.add(createFieldLabel("Mã KM"), proGbc);
        proGbc.gridx = 1; proGbc.weightx = 0.4;
        promotionCodeField.setPreferredSize(new Dimension(90, 26));
        promotionPanel.add(promotionCodeField, proGbc);
        proGbc.gridx = 2; proGbc.weightx = 0.6;
        promotionComboBox.setPreferredSize(new Dimension(140, 26));
        promotionPanel.add(promotionComboBox, proGbc);
        proGbc.gridx = 3; proGbc.weightx = 0;
        applyPromotionButton.setPreferredSize(new Dimension(70, 26));
        promotionPanel.add(applyPromotionButton, proGbc);
        proGbc.gridx = 4;
        clearPromotionButton.setPreferredSize(new Dimension(30, 26));
        promotionPanel.add(clearPromotionButton, proGbc);

        proGbc.gridx = 0; proGbc.gridy = 1; proGbc.gridwidth = 5; proGbc.weightx = 1.0;
        promotionInfoLabel.setFont(ModernUITheme.FONT_SMALL);
        promotionInfoLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        promotionPanel.add(promotionInfoLabel, proGbc);

        row1.add(promotionPanel, BorderLayout.CENTER);
        bottomBar.add(row1);
        bottomBar.add(Box.createVerticalStrut(4));

        // --- Row 2: Khách đưa + Tiền thối (single compact row) ---
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel cashLabel = new JLabel("Khách đưa:");
        cashLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        row2.add(cashLabel);

        cashReceivedField.setPreferredSize(new Dimension(130, 30));
        cashReceivedField.setFont(new Font("SansSerif", Font.BOLD, 14));
        row2.add(cashReceivedField);

        quickExactButton.setPreferredSize(new Dimension(70, 28));
        refreshButton.setPreferredSize(new Dimension(70, 28));
        row2.add(quickExactButton);
        row2.add(refreshButton);

        row2.add(Box.createHorizontalStrut(12));

        JLabel changeTitleLabel = new JLabel("Tiền thối:");
        changeTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        row2.add(changeTitleLabel);

        changeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        changeLabel.setForeground(ModernUITheme.PRIMARY_COLOR);
        row2.add(changeLabel);

        bottomBar.add(row2);

        // --- Row 3: Bằng chữ ---
        JPanel row3 = new JPanel(new GridLayout(1, 2, 10, 0));
        row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        cashWordsLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        cashWordsLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        changeWordsLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        changeWordsLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        row3.add(cashWordsLabel);
        row3.add(changeWordsLabel);
        bottomBar.add(row3);
        bottomBar.add(Box.createVerticalStrut(4));

        // --- Row 4: Export option + Action buttons ---
        JPanel row4 = new JPanel(new BorderLayout(8, 0));
        row4.setOpaque(false);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel exportWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        exportWrap.setOpaque(false);
        exportWrap.add(exportInvoiceCheckBox);
        row4.add(exportWrap, BorderLayout.WEST);

        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        actionButtonsPanel.setOpaque(false);

        payCashButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        payCashButton.setPreferredSize(new Dimension(0, 36));
        payTransferButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        payTransferButton.setPreferredSize(new Dimension(0, 36));
        printButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        printButton.setPreferredSize(new Dimension(0, 36));

        actionButtonsPanel.add(payCashButton);
        actionButtonsPanel.add(payTransferButton);
        actionButtonsPanel.add(printButton);
        row4.add(actionButtonsPanel, BorderLayout.CENTER);
        bottomBar.add(row4);

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

    public ChiTietHoaDon getInvoiceItemAtRow(int row) {
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
        tongTienHienTai = Math.round(total);
        giamGiaHienTai = 0;
        khuyenMaiDangApDung = null;
        capNhatTongTienHienThi();
        promotionInfoLabel.setText("Chưa áp dụng khuyến mãi");
        promotionInfoLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        invoiceInfoLabel.setText("Hóa đơn hiện tại: " + (invoiceId == null ? "Chưa có" : "#" + invoiceId));
        boolean hasItems = !currentInvoiceItems.isEmpty();
        invoiceItemTable.setEnabled(hasItems);
        if (!hasItems) {
            cashReceivedField.setText("0");
            updateCashWords();
            capNhatTienThoi(0, true);
        } else {
            updateCashWords();
            capNhatTienThoi(0, false);
        }
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

    public void bindTableStatusRefresh(Runnable action) {
        refreshTableStatusButton.addActionListener(e -> action.run());
    }

    public void setTableStatusText(String status) {
        tableStatusValueLabel.setText(status == null || status.isBlank() ? "Trạng thái: -" : "Trạng thái: " + status);
    }

    public void setActionButtonsEnabled(boolean enabled) {
        addProductButton.setEnabled(enabled);
        payCashButton.setEnabled(enabled);
        payTransferButton.setEnabled(enabled);
        printButton.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        invoiceItemTable.setEnabled(enabled && !currentInvoiceItems.isEmpty());
        cashReceivedField.setEnabled(enabled);
        quickExactButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        promotionCodeField.setEnabled(enabled);
        promotionComboBox.setEnabled(enabled);
        applyPromotionButton.setEnabled(enabled);
        clearPromotionButton.setEnabled(enabled);
        exportInvoiceCheckBox.setEnabled(enabled);
    }

    public boolean isExportInvoiceSelected() {
        return exportInvoiceCheckBox.isSelected();
    }

    public long getTongTienHienTai() {
        return tongTienHienTai;
    }

    public long getTongTienCanThu() {
        return Math.max(0, tongTienHienTai - giamGiaHienTai);
    }

    public KhuyenMai getKhuyenMaiDangApDung() {
        return khuyenMaiDangApDung;
    }

    public long getGiamGiaHienTai() {
        return giamGiaHienTai;
    }

    public long getTienKhachDua() {
        String text = cashReceivedField.getText();
        if (text == null || text.isBlank()) {
            return 0;
        }
        String normalized = text.replaceAll("[^0-9]", "");
        if (normalized.isEmpty()) {
            return 0;
        }
        return Long.parseLong(normalized);
    }

    public void setTienKhachDua(long amount) {
        cashFieldInternalUpdate = true;
        if (amount <= 0) {
            cashReceivedField.setText("0");
            cashFieldInternalUpdate = false;
            updateCashWords();
            return;
        }
        cashReceivedField.setText(formatMoney(amount));
        cashFieldInternalUpdate = false;
        updateCashWords();
    }

    public void capNhatTienThoi(long change, boolean duTien) {
        if (!duTien) {
            changeLabel.setText("Chưa đủ tiền");
            changeLabel.setForeground(new Color(220, 53, 69));
            changeWordsLabel.setText("Tiền thối (bằng chữ): Chưa đủ tiền");
            return;
        }
        long displayChange = Math.max(0, change);
        changeLabel.setText(formatMoney(displayChange) + " VND");
        changeLabel.setForeground(ModernUITheme.PRIMARY_COLOR);
        changeWordsLabel.setText("Tiền thối (bằng chữ): " + toVietnameseMoneyWords(displayChange));
    }

    public void bindCashInputChange(Runnable action) {
        cashReceivedField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleUpdate();
            }
            
            private void handleUpdate() {
                if (cashFieldInternalUpdate) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    normalizeCashInput();
                    action.run();
                });
            }
        });
    }

    public void bindPromotionSelection(Runnable action) {
        promotionComboBox.addActionListener(e -> action.run());
    }

    public void bindApplyPromotion(Runnable action) {
        applyPromotionButton.addActionListener(e -> action.run());
    }

    public void bindClearPromotion(Runnable action) {
        clearPromotionButton.addActionListener(e -> action.run());
    }

    public void setAvailablePromotions(List<KhuyenMai> promotions) {
        activePromotions = promotions == null ? List.of() : promotions;
        promotionComboBox.removeAllItems();
        promotionComboBox.addItem(null);
        for (KhuyenMai km : activePromotions) {
            if (km.isKichHoat()) {
                promotionComboBox.addItem(km);
            }
        }
        promotionComboBox.setSelectedIndex(0);
    }

    public KhuyenMai getSelectedPromotion() {
        Object selected = promotionComboBox.getSelectedItem();
        if (selected instanceof KhuyenMai km) {
            return km;
        }
        return null;
    }

    public String getPromotionCodeInput() {
        return promotionCodeField.getText() == null ? "" : promotionCodeField.getText().trim();
    }

    public void setPromotionCodeInput(String code) {
        promotionCodeField.setText(code == null ? "" : code);
    }

    public void apDungKhuyenMai(KhuyenMai km, long discountValue) {
        khuyenMaiDangApDung = km;
        giamGiaHienTai = Math.max(0, discountValue);
        if (km == null) {
            promotionInfoLabel.setText("Chưa áp dụng khuyến mãi");
            promotionInfoLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        } else {
            promotionInfoLabel.setText("Đã áp dụng: " + km.getMaCode() + " - " + km.getGiaTriHienThi());
            promotionInfoLabel.setForeground(ModernUITheme.SUCCESS_COLOR);
            setPromotionCodeInput(km.getMaCode());
        }
        capNhatTongTienHienThi();
    }

    public void boKhuyenMai() {
        khuyenMaiDangApDung = null;
        giamGiaHienTai = 0;
        promotionInfoLabel.setText("Chưa áp dụng khuyến mãi");
        promotionInfoLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        setPromotionCodeInput("");
        if (promotionComboBox.getItemCount() > 0) {
            promotionComboBox.setSelectedIndex(0);
        }
        capNhatTongTienHienThi();
    }

    public KhuyenMai findPromotionByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String normalized = code.trim().toUpperCase();
        for (KhuyenMai km : activePromotions) {
            if (km.isKichHoat() && km.getMaCode() != null && km.getMaCode().trim().equalsIgnoreCase(normalized)) {
                return km;
            }
        }
        return null;
    }

    private void normalizeCashInput() {
        if (cashFieldInternalUpdate) {
            return;
        }

        String text = cashReceivedField.getText();
        if (text == null || text.isBlank()) {
            updateCashWords();
            return;
        }
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            updateCashWords();
            return;
        }

        long amount = Long.parseLong(digits);
        String formatted = formatMoney(amount);
        if (!formatted.equals(text)) {
            cashFieldInternalUpdate = true;
            cashReceivedField.setText(formatted);
            cashReceivedField.setCaretPosition(formatted.length());
            cashFieldInternalUpdate = false;
        }
        updateCashWords();
    }

    private void updateCashWords() {
        long amount = getTienKhachDua();
        cashWordsLabel.setText("Khách đưa (bằng chữ): " + toVietnameseMoneyWords(amount));
    }

    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setForeground(ModernUITheme.TEXT_PRIMARY);
        return label;
    }

    private void capNhatTongTienHienThi() {
        subtotalValueLabel.setText(formatMoney(tongTienHienTai) + " VND");
        discountValueLabel.setText(formatMoney(giamGiaHienTai) + " VND");
        totalLabel.setText(formatMoney(getTongTienCanThu()) + " VND");
    }

    private String toVietnameseMoneyWords(long amount) {
        if (amount <= 0) {
            return "Không đồng";
        }
        if (amount >= 1_000_000_000_000L) {
            return "Số tiền quá lớn";
        }

        String[] donVi = {"", "nghìn", "triệu", "tỷ"};
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        long value = amount;

        while (value > 0) {
            int block = (int) (value % 1000);
            if (block > 0) {
                String blockText = readThreeDigits(block, value < 1000);
                if (!blockText.isBlank()) {
                    if (sb.length() > 0) {
                        sb.insert(0, " ");
                    }
                    String suffix = donVi[idx];
                    if (!suffix.isBlank()) {
                        sb.insert(0, suffix);
                        sb.insert(0, " ");
                    }
                    sb.insert(0, blockText);
                }
            }
            value /= 1000;
            idx++;
        }

        String result = sb.toString().trim() + " đồng";
        result = result.replaceAll("\\s+", " ");
        if (result.length() > 0) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }
        return result;
    }

    private String readThreeDigits(int n, boolean isLeading) {
        String[] numberWords = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        int tram = n / 100;
        int chuc = (n % 100) / 10;
        int donVi = n % 10;

        StringBuilder sb = new StringBuilder();
        if (tram > 0 || !isLeading) {
            sb.append(numberWords[tram]).append(" trăm");
        }
        if (chuc > 1) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(numberWords[chuc]).append(" mươi");
            if (donVi == 1) {
                sb.append(" mốt");
            } else if (donVi == 5) {
                sb.append(" lăm");
            } else if (donVi == 4) {
                sb.append(" tư");
            } else if (donVi > 0) {
                sb.append(" ").append(numberWords[donVi]);
            }
        } else if (chuc == 1) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("mười");
            if (donVi == 5) {
                sb.append(" lăm");
            } else if (donVi > 0) {
                sb.append(" ").append(numberWords[donVi]);
            }
        } else if (donVi > 0) {
            if (sb.length() > 0) {
                sb.append(" lẻ ");
            }
            sb.append(numberWords[donVi]);
        }
        return sb.toString().trim();
    }

    private String formatMoney(long amount) {
        return moneyFormat.format(amount);
    }

    private void applyRefreshButtonIcon() {
        java.net.URL iconUrl = getClass().getResource("/icon/refresh-icon-png-2.png");
        if (iconUrl == null) {
            return;
        }
        ImageIcon icon = new ImageIcon(iconUrl);
        Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        refreshButton.setIcon(new ImageIcon(scaled));
        refreshButton.setIconTextGap(6);
        refreshButton.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void applyClearPromotionButtonIcon() {
        Icon clearIcon = UIManager.getIcon("InternalFrame.closeIcon");
        if (clearIcon != null) {
            clearPromotionButton.setIcon(clearIcon);
        }
        clearPromotionButton.setToolTipText("Bỏ mã khuyến mãi");
        clearPromotionButton.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
