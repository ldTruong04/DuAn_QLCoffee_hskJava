package coffee.controller;

import coffee.model.NhanVien;
import coffee.model.HoaDon;
import coffee.model.KhuyenMai;
import coffee.model.SanPham;
import coffee.model.GioiTinh;
import coffee.model.VaiTro;
import coffee.service.DichVuCafe;

import java.util.List;
import java.util.Optional;

public class DieuKhienUngDung {
    private final DichVuCafe service;

    public DieuKhienUngDung(DichVuCafe service) {
        this.service = service;
    }

    public Optional<NhanVien> login(String username, String password) {
        return service.login(username, password);
    }

    public void addProduct(String name, String category, double price, String moTa, String duongDanHinhAnh) {
        service.createProduct(name, category, price, moTa, duongDanHinhAnh);
    }

    public void updateProduct(int id, String name, String category, double price, String moTa, String duongDanHinhAnh) {
        service.updateProduct(id, name, category, price, moTa, duongDanHinhAnh);
    }

    public void deleteProduct(int id) {
        service.deleteProduct(id);
    }

    public void addTable(String name) {
        service.createTable(name);
    }

    public void updateTable(int id, String name, boolean occupied) {
        service.updateTable(id, name, occupied);
    }

    public void setTableReserved(int id, boolean reserved) {
        service.setDaDat(id, reserved);
    }

    public void setTableDisabled(int id, boolean disabled) {
        service.setTableDisabled(id, disabled);
    }

    public void deleteTable(int id) {
        service.deleteTable(id);
    }

    public void addEmployee(String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password, String email) {
        service.createEmployee(hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password, email);
    }

    public void updateEmployee(int id, String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password, String email) {
        service.updateEmployee(id, hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password, email);
    }

    public void deleteEmployee(int id) {
        service.deleteEmployee(id);
    }

    public int createInvoice(int tableId, NhanVien staff) {
        return service.createInvoice(tableId, staff).getMa();
    }

    public void addItemToInvoice(int invoiceId, int productId, int quantity) {
        service.addItemToInvoice(invoiceId, productId, quantity);
    }

    public java.util.List<coffee.model.MonOrderBep> getPendingOrderItems() {
        return service.getPendingOrderItems();
    }

    public void updateKitchenOrderItemStatus(int invoiceId, int productId, String status) {
        service.updateKitchenOrderItemStatus(invoiceId, productId, status);
    }

    public int placeOrder(int tableId, NhanVien staff, int productId, int quantity) {
        return service.placeOrder(tableId, staff, productId, quantity);
    }

    public void updateInvoiceItemQuantity(int invoiceId, int productId, int quantity) {
        service.updateInvoiceItemQuantity(invoiceId, productId, quantity);
    }

    public void removeInvoiceItem(int invoiceId, int productId) {
        service.removeInvoiceItem(invoiceId, productId);
    }

    public double payInvoice(int invoiceId) {
        return service.payInvoice(invoiceId);
    }

    public double payInvoice(int invoiceId, String paymentMethod) {
        return service.payInvoice(invoiceId, paymentMethod);
    }

    public String getInvoiceDetailText(int invoiceId) {
        return service.buildInvoiceDetailText(invoiceId);
    }

    public List<SanPham> getProducts() {
        return service.getProducts();
    }

    public List<coffee.model.BanCafe> getTables() {
        return service.getTables();
    }

    public List<NhanVien> getEmployees() {
        return service.getEmployees();
    }

    public List<HoaDon> getInvoices() {
        return service.getInvoices();
    }

    public double getRevenue() {
        return service.getRevenue();
    }

    public List<SanPham> getTopProducts(int limit) {
        return service.getTopProducts(limit);
    }

    public void addPromotion(String code, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        service.createPromotion(code, theoPhanTram, giaTriGiam, kichHoat);
    }

    public void updatePromotion(int id, String code, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        service.updatePromotion(id, code, theoPhanTram, giaTriGiam, kichHoat);
    }

    public void deletePromotion(int id) {
        service.deletePromotion(id);
    }

    public List<KhuyenMai> getPromotions() {
        return service.getPromotions();
    }

    public void updateInvoicePromotionAndDiscount(int invoiceId, String promotionCode, long discountAmount) {
        service.updateInvoicePromotionAndDiscount(invoiceId, promotionCode, discountAmount);
    }

    public void generateAndSendOTP(String email) throws Exception {
        service.generateAndSendOTP(email);
    }

    public void verifyOTPAndResetPassword(String email, String otp, String newPassword) {
        service.verifyOTPAndResetPassword(email, otp, newPassword);
    }
}
