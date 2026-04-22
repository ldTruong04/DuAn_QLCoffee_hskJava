package coffee.view.screens;

import coffee.util.ModernStyler;
import coffee.util.ModernUITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManHinhDanhSachHoaDon extends JPanel {
    public final DefaultTableModel invoiceTableModel = new DefaultTableModel(
            new Object[]{"Mã đơn", "Khách hàng", "Nhân viên", "Tổng tiền", "Tổng thanh toán", "Ngày giờ tạo", "Bàn"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public final DefaultTableModel detailTableModel = new DefaultTableModel(
            new Object[]{"Tên món", "Số lượng", "Đơn giá", "Thành tiền"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public final JTable invoiceTable = new JTable(invoiceTableModel);
    public final JTable detailTable = new JTable(detailTableModel);
    public final JLabel summarySubtotalLabel = new JLabel("Tổng tiền: 0 VND");
    public final JLabel summaryDiscountLabel = new JLabel("Giảm giá: 0 VND");
    public final JLabel summaryTotalLabel = new JLabel("Tổng thanh toán: 0 VND");

    public ManHinhDanhSachHoaDon() {
        setLayout(new BorderLayout(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD,
                ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));

        JPanel topPanel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        topPanel.setBackground(ModernUITheme.BG_PRIMARY);
        JLabel topTitle = new JLabel("DANH SÁCH HÓA ĐƠN");
        topTitle.setFont(ModernUITheme.FONT_HEADING);
        topTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        topPanel.add(topTitle, BorderLayout.NORTH);

        ModernStyler.styleTable(invoiceTable);
        JScrollPane invoiceScroll = new JScrollPane(invoiceTable);
        ModernStyler.styleScrollPane(invoiceScroll);
        topPanel.add(invoiceScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        bottomPanel.setBackground(ModernUITheme.BG_PRIMARY);
        JLabel bottomTitle = new JLabel("CHI TIẾT HÓA ĐƠN");
        bottomTitle.setFont(ModernUITheme.FONT_HEADING);
        bottomTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        bottomPanel.add(bottomTitle, BorderLayout.NORTH);

        ModernStyler.styleTable(detailTable);
        JScrollPane detailScroll = new JScrollPane(detailTable);
        ModernStyler.styleScrollPane(detailScroll);

        JPanel detailCenterPanel = new JPanel(new BorderLayout(0, ModernUITheme.PADDING_SM));
        detailCenterPanel.setBackground(ModernUITheme.BG_PRIMARY);
        detailCenterPanel.add(detailScroll, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(ModernUITheme.BG_SECONDARY);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_SM, ModernUITheme.PADDING_MD,
                ModernUITheme.PADDING_SM, ModernUITheme.PADDING_MD));

        summarySubtotalLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summarySubtotalLabel.setForeground(ModernUITheme.TEXT_PRIMARY);
        summaryDiscountLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summaryDiscountLabel.setForeground(ModernUITheme.TEXT_PRIMARY);
        summaryTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        summaryTotalLabel.setForeground(ModernUITheme.PRIMARY_COLOR);

        summaryPanel.add(summarySubtotalLabel);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(summaryDiscountLabel);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(summaryTotalLabel);

        detailCenterPanel.add(summaryPanel, BorderLayout.SOUTH);
        bottomPanel.add(detailCenterPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.68);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);
    }
}
