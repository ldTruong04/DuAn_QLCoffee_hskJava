package coffee.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private final int ma;
    private final BanCafe ban;
    private final NhanVien nhanVien;
    private final LocalDateTime thoiGianTao;
    private final List<ChiTietHoaDon> danhSachMon;
    private boolean daThanhToan;

    public HoaDon(int ma, BanCafe ban, NhanVien nhanVien) {
        this.ma = ma;
        this.ban = ban;
        this.nhanVien = nhanVien;
        this.thoiGianTao = LocalDateTime.now();
        this.danhSachMon = new ArrayList<>();
        this.daThanhToan = false;
    }

    public HoaDon(int ma, BanCafe ban, NhanVien nhanVien, LocalDateTime thoiGianTao, boolean daThanhToan) {
        this.ma = ma;
        this.ban = ban;
        this.nhanVien = nhanVien;
        this.thoiGianTao = thoiGianTao;
        this.danhSachMon = new ArrayList<>();
        this.daThanhToan = daThanhToan;
    }

    public int getMa() {
        return ma;
    }

    public BanCafe getBan() {
        return ban;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public List<ChiTietHoaDon> getDanhSachMon() {
        return danhSachMon;
    }

    public boolean isDaThanhToan() {
        return daThanhToan;
    }

    public void setDaThanhToan(boolean daThanhToan) {
        this.daThanhToan = daThanhToan;
    }

    public void themMon(SanPham sanPham, int soLuong) {
        danhSachMon.add(new ChiTietHoaDon(sanPham, soLuong));
    }

    public double getTongTien() {
        return danhSachMon.stream().mapToDouble(ChiTietHoaDon::getThanhTien).sum();
    }

    @Override
    public String toString() {
        return "HD#" + ma + " - Bàn " + ban.getTen() + " - " + (daThanhToan ? "Đã thanh toán" : "Chưa thanh toán");
    }
}
