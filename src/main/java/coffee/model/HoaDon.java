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
    private String phuongThucThanhToan;
    private String maKhuyenMai;
    private long giamGia;

    public HoaDon(int ma, BanCafe ban, NhanVien nhanVien) {
        this.ma = ma;
        this.ban = ban;
        this.nhanVien = nhanVien;
        this.thoiGianTao = LocalDateTime.now();
        this.danhSachMon = new ArrayList<>();
        this.daThanhToan = false;
        this.phuongThucThanhToan = null;
        this.maKhuyenMai = null;
        this.giamGia = 0;
    }

    public HoaDon(int ma, BanCafe ban, NhanVien nhanVien, LocalDateTime thoiGianTao, boolean daThanhToan) {
        this(ma, ban, nhanVien, thoiGianTao, daThanhToan, null);
    }

    public HoaDon(int ma, BanCafe ban, NhanVien nhanVien, LocalDateTime thoiGianTao, boolean daThanhToan, String phuongThucThanhToan) {
        this.ma = ma;
        this.ban = ban;
        this.nhanVien = nhanVien;
        this.thoiGianTao = thoiGianTao;
        this.danhSachMon = new ArrayList<>();
        this.daThanhToan = daThanhToan;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.maKhuyenMai = null;
        this.giamGia = 0;
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

    public String getPhuongThucThanhToan() {
        if (phuongThucThanhToan == null || phuongThucThanhToan.isBlank()) {
            return "TIEN_MAT";
        }
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public void themMon(SanPham sanPham, int soLuong) {
        danhSachMon.add(new ChiTietHoaDon(sanPham, soLuong));
    }

    public double getTongTien() {
        return danhSachMon.stream().mapToDouble(ChiTietHoaDon::getThanhTien).sum();
    }

    public long getTongThanhToan() {
        return Math.max(0, Math.round(getTongTien()) - giamGia);
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public long getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(long giamGia) {
        this.giamGia = Math.max(0, giamGia);
    }

    @Override
    public String toString() {
        return "HD#" + ma + " - Bàn " + ban.getTen() + " - " + (daThanhToan ? "Đã thanh toán" : "Chưa thanh toán");
    }
}
