package coffee.model;

public class ChiTietHoaDon {
    private final SanPham sanPham;
    private int soLuong;
    private String trangThai = "PENDING";

    public ChiTietHoaDon(SanPham sanPham, int soLuong) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
    }

    public ChiTietHoaDon(SanPham sanPham, int soLuong, String trangThai) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return sanPham.getGia() * soLuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
