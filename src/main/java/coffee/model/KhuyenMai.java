package coffee.model;

public class KhuyenMai {
    private final int ma;
    private String maCode;
    private boolean theoPhanTram;
    private double giaTriGiam;
    private boolean kichHoat;

    public KhuyenMai(int ma, String maCode, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        this.ma = ma;
        this.maCode = maCode;
        this.theoPhanTram = theoPhanTram;
        this.giaTriGiam = giaTriGiam;
        this.kichHoat = kichHoat;
    }

    public int getMa() {
        return ma;
    }

    public String getMaCode() {
        return maCode;
    }

    public boolean isTheoPhanTram() {
        return theoPhanTram;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public boolean isKichHoat() {
        return kichHoat;
    }

    public void setMaCode(String maCode) {
        this.maCode = maCode;
    }

    public void setTheoPhanTram(boolean theoPhanTram) {
        this.theoPhanTram = theoPhanTram;
    }

    public void setGiaTriGiam(double giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public void setKichHoat(boolean kichHoat) {
        this.kichHoat = kichHoat;
    }

    public double tinhSoTienGiam(double tongTien) {
        if (tongTien <= 0) {
            return 0;
        }
        double discount = theoPhanTram ? (tongTien * giaTriGiam / 100.0) : giaTriGiam;
        return Math.max(0, Math.min(discount, tongTien));
    }

    public String getLoaiHienThi() {
        return theoPhanTram ? "Phần trăm" : "Giảm trực tiếp";
    }

    public String getGiaTriHienThi() {
        if (theoPhanTram) {
            return String.format("%.0f%%", giaTriGiam);
        }
        return String.format("%.0f VND", giaTriGiam);
    }
}
