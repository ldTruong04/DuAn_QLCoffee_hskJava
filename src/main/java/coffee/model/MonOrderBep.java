package coffee.model;

import java.time.LocalDateTime;

public class MonOrderBep {
    private int invoiceId;
    private int productId;
    private String tenMon;
    private int soLuong;
    private double gia;
    private String tenBan;
    private LocalDateTime thoiGian;
    private String trangThai;

    public MonOrderBep(int invoiceId, int productId, String tenMon, int soLuong, double gia, String tenBan, LocalDateTime thoiGian, String trangThai) {
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.gia = gia;
        this.tenBan = tenBan;
        this.thoiGian = thoiGian;
        this.trangThai = trangThai;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public String getTenMon() {
        return tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getGia() {
        return gia;
    }

    public String getTenBan() {
        return tenBan;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
