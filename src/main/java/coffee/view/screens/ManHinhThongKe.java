package coffee.view.screens;

import coffee.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManHinhThongKe extends JPanel {
    public final JLabel doanhThuValueLabel = new JLabel("0 VND");
    public final JLabel soHoaDonValueLabel = new JLabel("0");
    public final JLabel hoaDonDaThanhToanValueLabel = new JLabel("0");
    public final JLabel giaTriTrungBinhValueLabel = new JLabel("0 VND");
    public final JLabel capNhatLanCuoiLabel = new JLabel("Cập nhật: -");

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

        return topBar;
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildTopProductPanel(), buildRecentInvoicePanel());
        splitPane.setBorder(null);
        splitPane.setDividerLocation(220);
        splitPane.setResizeWeight(0.48);
        splitPane.setContinuousLayout(true);

        JTabbedPane chartTabs = new JTabbedPane();
        chartTabs.setFont(ModernUITheme.FONT_SMALL);
        chartTabs.addTab("Theo ngày", bieuDoTheoNgayPanel);
        chartTabs.addTab("Theo tháng", bieuDoTheoThangPanel);
        chartTabs.addTab("Theo ca", bieuDoTheoCaPanel);

        JSplitPane lowerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, chartTabs);
        lowerSplit.setBorder(null);
        lowerSplit.setDividerLocation(430);
        lowerSplit.setResizeWeight(0.60);
        lowerSplit.setContinuousLayout(true);

        content.add(kpiPanel, BorderLayout.NORTH);
        content.add(lowerSplit, BorderLayout.CENTER);
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
