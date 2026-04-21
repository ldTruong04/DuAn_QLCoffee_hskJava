package coffee.model;

public class SanPham {
    private final int ma;
    private String ten;
    private String danhMuc;
    private double gia;
    private String moTa;
    private String duongDanHinhAnh;
    private int soLuongDaBan;

    public SanPham(int ma, String ten, String danhMuc, double gia, String moTa, String duongDanHinhAnh) {
        this.ma = ma;
        this.ten = ten;
        this.danhMuc = danhMuc;
        this.gia = gia;
        this.moTa = moTa;
        this.duongDanHinhAnh = duongDanHinhAnh;
        this.soLuongDaBan = 0;
    }

    public SanPham(int ma, String ten, String danhMuc, double gia) {
        this(ma, ten, danhMuc, gia, null, null);
    }

    public int getMa() {
        return ma;
    }

    public String getTen() {
        return ten;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public double getGia() {
        return gia;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getDuongDanHinhAnh() {
        return duongDanHinhAnh;
    }

    public int getSoLuongDaBan() {
        return soLuongDaBan;
    }

    public void setSoLuongDaBan(int soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setDuongDanHinhAnh(String duongDanHinhAnh) {
        this.duongDanHinhAnh = duongDanHinhAnh;
    }

    public void tangSoLuongDaBan(int soLuong) {
        this.soLuongDaBan += soLuong;
    }

    @Override
    public String toString() {
        return ma + " - " + ten + " (" + danhMuc + ") - " + String.format("%.0f", gia) + " VND";
    }
}
