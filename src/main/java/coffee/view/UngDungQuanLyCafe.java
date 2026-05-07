package coffee.view;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.controller.screens.DieuKhienNhanVien;
import coffee.controller.screens.DieuKhienHoaDon;
import coffee.controller.screens.DieuKhienDanhSachHoaDon;
import coffee.controller.screens.DieuKhienDangNhap;
import coffee.controller.screens.DieuKhienKhuyenMai;
import coffee.controller.screens.DieuKhienSanPham;
import coffee.controller.screens.DieuKhienThongKe;
import coffee.controller.screens.DieuKhienBan;
import coffee.dao.KhoiTaoCoSoDuLieu;
import coffee.dao.KhuyenMaiRepository;
import coffee.dao.NhanVienRepository;
import coffee.dao.HoaDonRepository;
import coffee.dao.SanPhamRepository;
import coffee.dao.BanRepository;
import coffee.model.NhanVien;
import coffee.service.DichVuCafe;
import coffee.util.ModernUITheme;
import coffee.view.screens.ManHinhNhanVien;
import coffee.view.screens.ManHinhHoaDon;
import coffee.view.screens.ManHinhDanhSachHoaDon;
import coffee.view.screens.ManHinhDangNhap;
import coffee.view.screens.ManHinhSanPham;
import coffee.view.screens.ManHinhThongKe;
import coffee.view.screens.ManHinhBan;
import coffee.view.screens.ManHinhKhuyenMai;
import coffee.view.screens.ManHinhTroLyAI;
import coffee.controller.screens.DieuKhienTroLyAI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UngDungQuanLyCafe extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    private final JLabel currentScreenLabel = new JLabel("Tổng quan");
    private final JLabel currentUserLabel = new JLabel("Chưa đăng nhập");
    private final JLabel appTitleLabel = new JLabel("QL Coffee");
    private final JLabel dateTimeLabel = new JLabel();
    private final JButton sanPhamButton = new JButton("Sản phẩm");
    private final JButton banButton = new JButton("Bàn");
    private final JButton thanhToanButton = new JButton("Thanh toán");
    private final JButton hoaDonButton = new JButton("Hóa đơn");
    private final JButton khuyenMaiButton = new JButton("Khuyến mãi");
    private final JButton nhanVienButton = new JButton("Nhân viên");
    private final JButton thongKeButton = new JButton("Thống kê");
    private final JButton aiButton = new JButton("Trợ lý AI");
    private final JButton dangXuatButton = new JButton("Đăng xuất");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final Timer clockTimer = new Timer(1000, e -> updateClock());

    private final DieuKhienUngDung controller;
    private final PhienUngDung session = new PhienUngDung();

    private final ManHinhDangNhap loginView = new ManHinhDangNhap();
    private final ManHinhSanPham productView = new ManHinhSanPham();
    private final ManHinhBan tableView = new ManHinhBan();
    private final ManHinhNhanVien employeeView = new ManHinhNhanVien();
    private final ManHinhHoaDon paymentView = new ManHinhHoaDon();
    private final ManHinhDanhSachHoaDon invoiceView = new ManHinhDanhSachHoaDon();
    private final ManHinhKhuyenMai promotionView = new ManHinhKhuyenMai();
    private final ManHinhThongKe reportView = new ManHinhThongKe();
    private final ManHinhTroLyAI aiView = new ManHinhTroLyAI();

    private final DieuKhienSanPham productController;
    private final DieuKhienBan tableController;
    private final DieuKhienNhanVien employeeController;
    private final DieuKhienHoaDon paymentController;
    private final DieuKhienDanhSachHoaDon invoiceController;
    private final DieuKhienKhuyenMai promotionController;
    private final DieuKhienThongKe reportController;
    private final DieuKhienTroLyAI aiController;

    public UngDungQuanLyCafe() {
        applyModernTheme();
        
        KhoiTaoCoSoDuLieu.initialize();
        this.controller = new DieuKhienUngDung(new DichVuCafe(
                new SanPhamRepository(),
                new BanRepository(),
                new NhanVienRepository(),
                new HoaDonRepository(),
                new KhuyenMaiRepository()
        ));
        this.productController = new DieuKhienSanPham(controller, session, productView);
        this.tableController = new DieuKhienBan(controller, session, tableView);
        this.employeeController = new DieuKhienNhanVien(controller, session, employeeView);
        this.reportController = new DieuKhienThongKe(controller, reportView);
        this.paymentController = new DieuKhienHoaDon(controller, session, paymentView, this::refreshAll);
        this.invoiceController = new DieuKhienDanhSachHoaDon(controller, invoiceView);
        this.promotionController = new DieuKhienKhuyenMai(controller, session, promotionView);
        this.aiController = new DieuKhienTroLyAI(controller, aiView);
        new DieuKhienDangNhap(controller, session, loginView, this::onLoginSuccess);
        initUI();
        refreshAll();
    }

    private void applyModernTheme() {
        UIManager.put("Panel.background", ModernUITheme.BG_PRIMARY);
        UIManager.put("Panel.foreground", ModernUITheme.TEXT_PRIMARY);
        UIManager.put("Label.foreground", ModernUITheme.TEXT_PRIMARY);
        UIManager.put("TabbedPane.foreground", ModernUITheme.TEXT_PRIMARY);
        UIManager.put("TabbedPane.background", ModernUITheme.BG_PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", ModernUITheme.BG_PRIMARY);
        UIManager.put("OptionPane.background", ModernUITheme.BG_PRIMARY);
        UIManager.put("OptionPane.messageForeground", ModernUITheme.TEXT_PRIMARY);
        UIManager.put("Table.background", ModernUITheme.BG_PRIMARY);
        UIManager.put("Table.foreground", ModernUITheme.TEXT_PRIMARY);
        UIManager.put("Button.foreground", ModernUITheme.TEXT_PRIMARY);
    }

    private void initUI() {
        setTitle("☕ Coffee Management - MVC Swing");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Set frame colors
        setBackground(ModernUITheme.BG_PRIMARY);
        rootPanel.setBackground(ModernUITheme.BG_PRIMARY);

        rootPanel.add(loginView, "LOGIN");
        rootPanel.add(createDashboardPanel(), "DASHBOARD");
        add(rootPanel);
        cardLayout.show(rootPanel, "LOGIN");
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUITheme.BG_PRIMARY);

        panel.add(createHeaderPanel(), BorderLayout.NORTH);
        panel.add(createMainWorkspace(), BorderLayout.CENTER);
        panel.add(createStatusBar(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernUITheme.BG_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUITheme.BORDER_COLOR));

        appTitleLabel.setFont(ModernUITheme.FONT_HEADING);
        appTitleLabel.setForeground(ModernUITheme.PRIMARY_DARK);

        dateTimeLabel.setFont(ModernUITheme.FONT_BODY);
        dateTimeLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(appTitleLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(dateTimeLabel);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        updateClock();
        clockTimer.start();
        return header;
    }

    private JPanel createMainWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(ModernUITheme.PADDING_LG, 0));
        workspace.setBackground(ModernUITheme.BG_PRIMARY);
        workspace.setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG,
                ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG));

        JPanel sidebar = createSidebar();
        workspace.add(sidebar, BorderLayout.WEST);

        contentPanel.setBackground(ModernUITheme.BG_PRIMARY);
        contentPanel.add(productView, "SAN_PHAM");
        contentPanel.add(tableView, "BAN");
        contentPanel.add(paymentView, "THANH_TOAN");
        contentPanel.add(invoiceView, "HOA_DON");
        contentPanel.add(promotionView, "KHUYEN_MAI");
        contentPanel.add(employeeView, "NHAN_VIEN");
        contentPanel.add(reportView, "THONG_KE");
        contentPanel.add(aiView, "AI_ASSISTANT");

        workspace.add(contentPanel, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 0, 0));
        sidebar.setPreferredSize(new Dimension(210, 0));

        JButton[] navButtons = {sanPhamButton, banButton, thanhToanButton, hoaDonButton, khuyenMaiButton, nhanVienButton, thongKeButton, aiButton, dangXuatButton};
        for (JButton btn : navButtons) {
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        sanPhamButton.addActionListener(e -> showScreen("Sản phẩm", "SAN_PHAM", sanPhamButton));
        banButton.addActionListener(e -> showScreen("Bàn", "BAN", banButton));
        thanhToanButton.addActionListener(e -> showScreen("Thanh toán", "THANH_TOAN", thanhToanButton));
        hoaDonButton.addActionListener(e -> showScreen("Hóa đơn", "HOA_DON", hoaDonButton));
        khuyenMaiButton.addActionListener(e -> showScreen("Khuyến mãi", "KHUYEN_MAI", khuyenMaiButton));
        nhanVienButton.addActionListener(e -> showScreen("Nhân viên", "NHAN_VIEN", nhanVienButton));
        thongKeButton.addActionListener(e -> showScreen("Thống kê", "THONG_KE", thongKeButton));
        aiButton.addActionListener(e -> showScreen("Trợ lý AI", "AI_ASSISTANT", aiButton));
        dangXuatButton.addActionListener(e -> logout());

        sidebar.add(sanPhamButton);
        sidebar.add(banButton);
        sidebar.add(thanhToanButton);
        sidebar.add(hoaDonButton);
        sidebar.add(khuyenMaiButton);
        sidebar.add(nhanVienButton);
        sidebar.add(thongKeButton);
        sidebar.add(aiButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(dangXuatButton);
        return sidebar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(ModernUITheme.BG_PRIMARY);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUITheme.BORDER_COLOR));

        JLabel statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setForeground(ModernUITheme.TEXT_TERTIARY);
        statusLabel.setFont(ModernUITheme.FONT_SMALL);

        currentUserLabel.setForeground(ModernUITheme.TEXT_TERTIARY);
        currentUserLabel.setFont(ModernUITheme.FONT_SMALL);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(currentUserLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void showScreen(String title, String cardKey, JButton activeButton) {
        currentScreenLabel.setText(title);
        contentLayout.show(contentPanel, cardKey);
        setActiveNav(activeButton);
    }

    private void setActiveNav(JButton activeButton) {
        JButton[] buttons = {sanPhamButton, banButton, thanhToanButton, hoaDonButton, khuyenMaiButton, nhanVienButton, thongKeButton, aiButton};
        for (JButton button : buttons) {
            if (button == activeButton) {
                button.setFont(button.getFont().deriveFont(Font.BOLD));
            } else {
                button.setFont(button.getFont().deriveFont(Font.PLAIN));
            }
        }
    }

    private void logout() {
        session.setCurrentUser(null);
        currentUserLabel.setText("Chưa đăng nhập");
        cardLayout.show(rootPanel, "LOGIN");
    }

    private void showDefaultScreenForUser() {
        if (session.getCurrentUser() != null && session.getCurrentUser().getVaiTro() == coffee.model.VaiTro.ADMIN) {
            nhanVienButton.setEnabled(true);
            khuyenMaiButton.setEnabled(true);
        } else {
            nhanVienButton.setEnabled(false);
            khuyenMaiButton.setEnabled(false);
        }
        showScreen("Thống kê", "THONG_KE", thongKeButton);
    }

    private void refreshAll() {
        productController.refresh();
        tableController.refresh();
        employeeController.refresh();
        paymentController.refresh();
        invoiceController.refresh();
        promotionController.refresh();
        reportController.refresh();
        
        int pendingCount = controller.getPendingOrderItems().size();
        if (pendingCount > 0) {
            sanPhamButton.setText("Sản phẩm (" + pendingCount + ")");
        } else {
            sanPhamButton.setText("Sản phẩm");
        }
    }

    private void onLoginSuccess() {
        NhanVien user = session.getCurrentUser();
        setTitle("Coffee Management - " + user.getHoTen() + " (" + user.getVaiTro() + ")");
        currentUserLabel.setText("Xin chào, " + user.getHoTen() + " • " + user.getVaiTro());
        showDefaultScreenForUser();
        cardLayout.show(rootPanel, "DASHBOARD");
        refreshAll();
    }

    private void updateClock() {
        dateTimeLabel.setText(java.time.LocalDateTime.now().format(dateTimeFormatter));
        
        // Cập nhật số lượng đơn bếp theo thời gian thực
        if (session.getCurrentUser() != null) {
            int pendingCount = controller.getPendingOrderItems().size();
            if (pendingCount > 0) {
                sanPhamButton.setText("Sản phẩm (" + pendingCount + ")");
            } else {
                sanPhamButton.setText("Sản phẩm");
            }
        }
    }
}
