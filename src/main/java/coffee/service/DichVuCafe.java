package coffee.service;

import coffee.dao.NhanVienRepository;
import coffee.dao.HoaDonRepository;
import coffee.dao.SanPhamRepository;
import coffee.dao.BanRepository;
import coffee.dao.KhuyenMaiRepository;
import coffee.model.BanCafe;
import coffee.model.GioiTinh;
import coffee.model.NhanVien;
import coffee.model.HoaDon;
import coffee.model.ChiTietHoaDon;
import coffee.model.KhuyenMai;
import coffee.model.SanPham;
import coffee.model.VaiTro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import coffee.service.EmailService;
import coffee.util.OTPUtil;

public class DichVuCafe {
    private final SanPhamRepository productRepository;
    private final BanRepository tableRepository;
    private final NhanVienRepository employeeRepository;
    private final HoaDonRepository invoiceRepository;
    private final KhuyenMaiRepository promotionRepository;
    
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();

    public DichVuCafe(SanPhamRepository productRepository,
                       BanRepository tableRepository,
                       NhanVienRepository employeeRepository,
                       HoaDonRepository invoiceRepository,
                       KhuyenMaiRepository promotionRepository) {
        this.productRepository = productRepository;
        this.tableRepository = tableRepository;
        this.employeeRepository = employeeRepository;
        this.invoiceRepository = invoiceRepository;
        this.promotionRepository = promotionRepository;
    }

    public Optional<NhanVien> login(String username, String password) {
        return employeeRepository.findByCredentials(username, password);
    }

    public SanPham createProduct(String name, String category, double price, String moTa, String duongDanHinhAnh) {
        requireText(name, "Tên sản phẩm");
        requireText(category, "Loại sản phẩm");
        if (price < 0) {
            throw new IllegalArgumentException("Giá phải >= 0");
        }
        return productRepository.insert(name, category, price, moTa, duongDanHinhAnh);
    }

    public void updateProduct(int id, String name, String category, double price, String moTa, String duongDanHinhAnh) {
        requireText(name, "Tên sản phẩm");
        requireText(category, "Loại sản phẩm");
        if (price < 0) {
            throw new IllegalArgumentException("Giá phải >= 0");
        }
        productRepository.update(id, name, category, price, moTa, duongDanHinhAnh);
    }

    public void deleteProduct(int id) {
        productRepository.delete(id);
    }

    public BanCafe createTable(String name) {
        requireText(name, "Tên bàn");
        return tableRepository.insert(name);
    }

    public void updateTable(int id, String name, boolean occupied) {
        requireText(name, "Tên bàn");
        tableRepository.update(id, name, occupied);
    }

    public void setDaDat(int tableId, boolean reserved) {
        tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        tableRepository.setDaDat(tableId, reserved);
    }

    public void setTableDisabled(int tableId, boolean disabled) {
        tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        tableRepository.setKhongSuDung(tableId, disabled);
    }

    public void deleteTable(int id) {
        tableRepository.delete(id);
    }

    public NhanVien createEmployee(String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password, String email) {
        requireText(hoTen, "Họ tên");
        requireText(username, "Tên đăng nhập");
        requireText(password, "Mật khẩu");
        if (role == null) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò");
        }
        if (gioiTinh == null) {
            throw new IllegalArgumentException("Vui lòng chọn giới tính");
        }
        if (namSinh < 1900 || namSinh > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Năm sinh không hợp lệ");
        }
        if (luong < 0) {
            throw new IllegalArgumentException("Lương phải >= 0");
        }
        return employeeRepository.insert(hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password, email);
    }

    public void updateEmployee(int id, String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password) {
        requireText(hoTen, "Họ tên");
        requireText(username, "Tên đăng nhập");
        requireText(password, "Mật khẩu");
        if (role == null) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò");
        }
        if (gioiTinh == null) {
            throw new IllegalArgumentException("Vui lòng chọn giới tính");
        }
        if (namSinh < 1900 || namSinh > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Năm sinh không hợp lệ");
        }
        if (luong < 0) {
            throw new IllegalArgumentException("Lương phải >= 0");
        }
        // Giữ nguyên email cũ khi cập nhật qua hàm này
        NhanVien old = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
        employeeRepository.update(id, hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password, old.getEmail());
    }

    public void updateEmployee(int id, String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password, String email) {
        requireText(hoTen, "Họ tên");
        requireText(username, "Tên đăng nhập");
        requireText(password, "Mật khẩu");
        if (role == null) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò");
        }
        if (gioiTinh == null) {
            throw new IllegalArgumentException("Vui lòng chọn giới tính");
        }
        if (namSinh < 1900 || namSinh > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Năm sinh không hợp lệ");
        }
        if (luong < 0) {
            throw new IllegalArgumentException("Lương phải >= 0");
        }
        employeeRepository.update(id, hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password, email);
    }

    public void deleteEmployee(int id) {
        employeeRepository.delete(id);
    }

    public HoaDon createInvoice(int tableId, NhanVien staff) {
        if (staff == null) {
            throw new IllegalArgumentException("Phiên đăng nhập không hợp lệ");
        }
        BanCafe table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        Optional<Integer> openInvoiceId = invoiceRepository.findUnpaidInvoiceIdByTableId(tableId);
        if (openInvoiceId.isPresent()) {
            return invoiceRepository.findDetailedById(openInvoiceId.get());
        }
        if (table.isDangSuDung()) {
            throw new IllegalStateException("Bàn đang có khách nhưng chưa có hóa đơn mở");
        }

        tableRepository.setDangSuDung(tableId, true);
        return invoiceRepository.createInvoice(tableId, staff.getMa());
    }

    public void addItemToInvoice(int invoiceId, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải > 0");
        }
        HoaDon invoice = invoiceRepository.findDetailedById(invoiceId);
        if (invoice.isDaThanhToan()) {
            throw new IllegalStateException("Không thể thêm món vào hóa đơn đã thanh toán");
        }
        SanPham product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        invoiceRepository.upsertInvoiceItem(invoiceId, productId, quantity, product.getGia());
    }

    public int placeOrder(int tableId, NhanVien staff, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải > 0");
        }

        tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        int invoiceId = invoiceRepository.findUnpaidInvoiceIdByTableId(tableId)
                .orElseGet(() -> createInvoice(tableId, staff).getMa());

        addItemToInvoice(invoiceId, productId, quantity);
        return invoiceId;
    }

    public void updateInvoiceItemQuantity(int invoiceId, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải > 0");
        }
        HoaDon invoice = invoiceRepository.findDetailedById(invoiceId);
        if (invoice.isDaThanhToan()) {
            throw new IllegalStateException("Không thể sửa món của hóa đơn đã thanh toán");
        }
        invoiceRepository.updateInvoiceItemQuantity(invoiceId, productId, quantity);
    }

    public void removeInvoiceItem(int invoiceId, int productId) {
        HoaDon invoice = invoiceRepository.findDetailedById(invoiceId);
        if (invoice.isDaThanhToan()) {
            throw new IllegalStateException("Không thể xóa món của hóa đơn đã thanh toán");
        }
        invoiceRepository.deleteInvoiceItem(invoiceId, productId);
    }

    public double payInvoice(int invoiceId) {
        return payInvoice(invoiceId, "TIEN_MAT");
    }

    public double payInvoice(int invoiceId, String paymentMethod) {
        HoaDon invoice = invoiceRepository.findDetailedById(invoiceId);
        if (invoice.getDanhSachMon().isEmpty()) {
            throw new IllegalStateException("Hóa đơn chưa có món");
        }
        HoaDon existing = invoiceRepository.findDetailedById(invoiceId);
        if (existing.isDaThanhToan()) {
            throw new IllegalStateException("Hóa đơn đã thanh toán");
        }

        invoiceRepository.markPaid(invoiceId, paymentMethod);
        tableRepository.setDangSuDung(invoice.getBan().getMa(), false);
        return invoice.getTongTien();
    }

    public String buildInvoiceDetailText(int invoiceId) {
        HoaDon invoice = invoiceRepository.findDetailedById(invoiceId);
        StringBuilder sb = new StringBuilder();
        sb.append("CHI TIET HOA DON #").append(invoice.getMa()).append("\n");
        sb.append("Ban: ").append(invoice.getBan().getTen()).append("\n");
        sb.append("Nhan vien: ").append(invoice.getNhanVien().getTen()).append("\n");
        sb.append("Thoi gian: ").append(invoice.getThoiGianTao()).append("\n");
        sb.append("Trang thai: ").append(invoice.isDaThanhToan() ? "Da thanh toan" : "Chua thanh toan").append("\n\n");
        sb.append(String.format("%-4s %-22s %-10s %-10s%n", "SL", "Mon", "Don gia", "Thanh tien"));
        for (ChiTietHoaDon item : invoice.getDanhSachMon()) {
            sb.append(String.format("%-4d %-22s %-10.0f %-10.0f%n",
                    item.getSoLuong(),
                    item.getSanPham().getTen(),
                    item.getSanPham().getGia(),
                    item.getThanhTien()));
        }
        sb.append("\nTong cong: ").append(String.format("%.0f", invoice.getTongTien())).append(" VND");
        return sb.toString();
    }

    public List<SanPham> getProducts() {
        return productRepository.findAll();
    }

    public List<BanCafe> getTables() {
        return tableRepository.findAll();
    }

    public List<NhanVien> getEmployees() {
        return employeeRepository.findAll();
    }

    public List<HoaDon> getInvoices() {
        return invoiceRepository.findAllDetailed();
    }

    public List<SanPham> getTopProducts(int limit) {
        return productRepository.findTopSelling(limit);
    }

    public double getRevenue() {
        return invoiceRepository.sumRevenue();
    }

    public KhuyenMai createPromotion(String code, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        String normalizedCode = normalizePromoCode(code);
        validatePromotionValue(theoPhanTram, giaTriGiam);
        return promotionRepository.insert(normalizedCode, theoPhanTram, giaTriGiam, kichHoat);
    }

    public void updatePromotion(int id, String code, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        String normalizedCode = normalizePromoCode(code);
        validatePromotionValue(theoPhanTram, giaTriGiam);
        promotionRepository.update(id, normalizedCode, theoPhanTram, giaTriGiam, kichHoat);
    }

    public void deletePromotion(int id) {
        promotionRepository.delete(id);
    }

    public List<KhuyenMai> getPromotions() {
        return promotionRepository.findAll();
    }

    public NhanVien getEmployeeById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));
    }

    public SanPham getProductById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
    }

    public HoaDon getInvoiceById(int id) {
        return invoiceRepository.findAllDetailed().stream()
                .filter(i -> i.getMa() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại"));
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống");
        }
    }

    private String normalizePromoCode(String code) {
        requireText(code, "Mã khuyến mãi");
        return code.trim().toUpperCase();
    }

    private void validatePromotionValue(boolean theoPhanTram, double giaTriGiam) {
        if (giaTriGiam <= 0) {
            throw new IllegalArgumentException("Giá trị giảm phải > 0");
        }
        if (theoPhanTram && giaTriGiam > 100) {
            throw new IllegalArgumentException("Giảm theo phần trăm phải từ 0 đến 100");
        }
    }

    public void updateInvoicePromotionAndDiscount(int invoiceId, String promotionCode, long discountAmount) {
        invoiceRepository.updatePromotionAndDiscount(invoiceId, promotionCode, discountAmount);
    }

    public void generateAndSendOTP(String email) throws Exception {
        NhanVien nv = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống"));
        
        String otp = OTPUtil.generateOTP();
        otpStorage.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
        
        EmailService.sendOTP(email, otp);
    }

    public void verifyOTPAndResetPassword(String email, String otp, String newPassword) {
        if (!otpStorage.containsKey(email)) {
            throw new IllegalArgumentException("OTP chưa được tạo hoặc đã hết hạn");
        }
        if (System.currentTimeMillis() > otpExpiry.get(email)) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            throw new IllegalArgumentException("Mã OTP đã hết hạn");
        }
        if (!otpStorage.get(email).equals(otp)) {
            throw new IllegalArgumentException("Mã OTP không chính xác");
        }
        
        NhanVien nv = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống"));
        
        // Update password
        employeeRepository.update(nv.getMa(), nv.getHoTen(), nv.getNamSinh(), nv.getLuong(), nv.getGioiTinh(), 
                nv.getAnhDaiDien(), nv.getVaiTro(), nv.getTenDangNhap(), newPassword, nv.getEmail());
                
        // Clear OTP
        otpStorage.remove(email);
        otpExpiry.remove(email);
    }
}
