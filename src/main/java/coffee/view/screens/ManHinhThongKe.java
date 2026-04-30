package coffee.view.screens;

import coffee.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class ManHinhThongKe extends JPanel {
    public final JLabel doanhThuValueLabel = new JLabel("0 VND");
    public final JLabel soHoaDonValueLabel = new JLabel("0");
    public final JLabel hoaDonDaThanhToanValueLabel = new JLabel("0");
    public final JLabel giaTriTrungBinhValueLabel = new JLabel("0 VND");
    public final JLabel capNhatLanCuoiLabel = new JLabel("Cập nhật: -");

    public final JTextField fromDateField = new ModernTextField(8);
    public final JTextField toDateField = new ModernTextField(8);
    public final JTextField shiftDateField = new ModernTextField(8);
    public final JButton applyFilterButton = new ModernButton("Áp dụng", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public final DefaultTableModel topSanPhamTableModel = new DefaultTableModel(
            new Object[]{"Sản phẩm", "Loại", "Đã bán", "Doanh thu"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable topSanPhamTable = new JTable(topSanPhamTableModel);

    public final DefaultTableModel hoaDonGanDayTableModel = new DefaultTableModel(
            new Object[]{"Mã đơn", "Bàn", "Nhân viên", "Tổng tiền", "Thời gian", "Trạng thái"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable hoaDonGanDayTable = new JTable(hoaDonGanDayTableModel);

    public final DoanhThuBieuDoPanel bieuDoTheoNgayPanel = new DoanhThuBieuDoPanel("Doanh thu theo ngày (7 ngày gần nhất)");
    public final DoanhThuBieuDoPanel bieuDoTheoThangPanel = new DoanhThuBieuDoPanel("Doanh thu theo tháng (6 tháng gần nhất)");
    public final DoanhThuBieuDoPanel bieuDoTheoCaPanel = new DoanhThuBieuDoPanel("Doanh thu theo ca");

    public final JButton refreshButton = new ModernButton("Làm mới thống kê", ModernUITheme.PRIMARY_COLOR, ModernUITheme.TEXT_PRIMARY);

    public ManHinhThongKe() {
        setLayout(new BorderLayout(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD,
                ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ModernUITheme.BG_PRIMARY);

        JPanel titleWrap = new JPanel(new GridLayout(2, 1, 0, 2));
        titleWrap.setOpaque(false);

        JLabel title = new JLabel("BÁO CÁO KINH DOANH");
        title.setFont(ModernUITheme.FONT_HEADING);
        title.setForeground(ModernUITheme.PRIMARY_DARK);
        titleWrap.add(title);

        capNhatLanCuoiLabel.setFont(ModernUITheme.FONT_SMALL);
        capNhatLanCuoiLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        titleWrap.add(capNhatLanCuoiLabel);

        topBar.add(titleWrap, BorderLayout.WEST);

        JPanel actionWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionWrap.setOpaque(false);
        refreshButton.setPreferredSize(new Dimension(160, 34));
        actionWrap.add(refreshButton);
        topBar.add(actionWrap, BorderLayout.EAST);

        JPanel filterPanel = buildFilterPanel();
        topBar.add(filterPanel, BorderLayout.SOUTH);

        return topBar;
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        panel.setOpaque(false);

        JLabel fromLabel = new JLabel("Từ ngày:");
        fromLabel.setFont(ModernUITheme.FONT_SMALL);
        fromLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        panel.add(fromLabel);

        fromDateField.setText(LocalDate.now().minusDays(6).format(dateFormatter));
        fromDateField.setEditable(false);
        fromDateField.setPreferredSize(new Dimension(100, 28));
        panel.add(fromDateField);

        JButton fromDateBtn = new JButton("📅");
        fromDateBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        fromDateBtn.setPreferredSize(new Dimension(32, 28));
        fromDateBtn.addActionListener(e -> showDatePicker(fromDateField));
        panel.add(fromDateBtn);

        JLabel toLabel = new JLabel("Đến ngày:");
        toLabel.setFont(ModernUITheme.FONT_SMALL);
        toLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        panel.add(toLabel);

        toDateField.setText(LocalDate.now().format(dateFormatter));
        toDateField.setEditable(false);
        toDateField.setPreferredSize(new Dimension(100, 28));
        panel.add(toDateField);

        JButton toDateBtn = new JButton("📅");
        toDateBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        toDateBtn.setPreferredSize(new Dimension(32, 28));
        toDateBtn.addActionListener(e -> showDatePicker(toDateField));
        panel.add(toDateBtn);

        JLabel shiftLabel = new JLabel("Ngày ca:");
        shiftLabel.setFont(ModernUITheme.FONT_SMALL);
        shiftLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        panel.add(shiftLabel);

        shiftDateField.setText(LocalDate.now().format(dateFormatter));
        shiftDateField.setEditable(false);
        shiftDateField.setPreferredSize(new Dimension(100, 28));
        panel.add(shiftDateField);

        JButton shiftDateBtn = new JButton("📅");
        shiftDateBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        shiftDateBtn.setPreferredSize(new Dimension(32, 28));
        shiftDateBtn.addActionListener(e -> showDatePicker(shiftDateField));
        panel.add(shiftDateBtn);

        applyFilterButton.setPreferredSize(new Dimension(100, 28));
        panel.add(applyFilterButton);

        return panel;
    }

    private void showDatePicker(JTextField targetField) {
        JPanel datePanel = new JPanel(new BorderLayout(8, 8));
        datePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        datePanel.setBackground(ModernUITheme.BG_PRIMARY);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        Calendar cal = Calendar.getInstance();
        try {
            LocalDate date = LocalDate.parse(targetField.getText(), dateFormatter);
            cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        } catch (Exception ex) {
            // Use current date if parsing fails
        }
        dateModel.setValue(cal.getTime());

        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        datePanel.add(new JLabel("Chọn ngày:"), BorderLayout.NORTH);
        datePanel.add(dateSpinner, BorderLayout.CENTER);

        int option = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                datePanel,
                "Chọn ngày",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime((java.util.Date) dateModel.getValue());
            LocalDate selectedDate = LocalDate.of(
                    selectedCal.get(Calendar.YEAR),
                    selectedCal.get(Calendar.MONTH) + 1,
                    selectedCal.get(Calendar.DAY_OF_MONTH)
            );
            targetField.setText(selectedDate.format(dateFormatter));
        }
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_MD));
        content.setBackground(ModernUITheme.BG_PRIMARY);

        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, ModernUITheme.PADDING_SM, 0));
        kpiPanel.setBackground(ModernUITheme.BG_PRIMARY);
        kpiPanel.add(createKpiCard("Doanh thu", doanhThuValueLabel));
        kpiPanel.add(createKpiCard("Tổng hóa đơn", soHoaDonValueLabel));
        kpiPanel.add(createKpiCard("Đã thanh toán", hoaDonDaThanhToanValueLabel));
        kpiPanel.add(createKpiCard("Trung bình/đơn", giaTriTrungBinhValueLabel));

        JTabbedPane chartTabs = new JTabbedPane();
        chartTabs.setFont(ModernUITheme.FONT_SMALL);
        chartTabs.addTab("Theo ngày", bieuDoTheoNgayPanel);
        chartTabs.addTab("Theo tháng", bieuDoTheoThangPanel);
        chartTabs.addTab("Theo ca", bieuDoTheoCaPanel);

        JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartTabs, buildTopProductPanel());
        topSplit.setBorder(null);
        topSplit.setDividerLocation(260);
        topSplit.setResizeWeight(0.55);
        topSplit.setContinuousLayout(true);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, buildRecentInvoicePanel());
        mainSplit.setBorder(null);
        mainSplit.setDividerLocation(430);
        mainSplit.setResizeWeight(0.60);
        mainSplit.setContinuousLayout(true);

        content.add(kpiPanel, BorderLayout.NORTH);
        content.add(mainSplit, BorderLayout.CENTER);
        return content;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 229, 233), 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ModernUITheme.FONT_SMALL);
        titleLabel.setForeground(ModernUITheme.TEXT_SECONDARY);

        valueLabel.setFont(ModernUITheme.FONT_HEADING);
        valueLabel.setForeground(ModernUITheme.PRIMARY_DARK);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTopProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel title = new JLabel("Top Sản Phẩm Bán Chạy");
        title.setFont(ModernUITheme.FONT_BODY);
        title.setForeground(ModernUITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        topSanPhamTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(topSanPhamTable);
        JScrollPane scrollPane = new JScrollPane(topSanPhamTable);
        ModernStyler.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildRecentInvoicePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        JLabel title = new JLabel("Hóa Đơn Gần Đây");
        title.setFont(ModernUITheme.FONT_BODY);
        title.setForeground(ModernUITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        hoaDonGanDayTable.setFillsViewportHeight(true);
        ModernStyler.styleTable(hoaDonGanDayTable);
        JScrollPane scrollPane = new JScrollPane(hoaDonGanDayTable);
        ModernStyler.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
