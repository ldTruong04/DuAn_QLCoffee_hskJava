package coffee.model;

public class BanCafe {
    private final int ma;
    private String ten;
    private boolean dangSuDung;
    private boolean daDat;
    private boolean khongSuDung;

    public BanCafe(int ma, String ten) {
        this.ma = ma;
        this.ten = ten;
        this.dangSuDung = false;
        this.daDat = false;
        this.khongSuDung = false;
    }

    public int getMa() {
        return ma;
    }

    public String getTen() {
        return ten;
    }

    public boolean isDangSuDung() {
        return dangSuDung;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setDangSuDung(boolean dangSuDung) {
        this.dangSuDung = dangSuDung;
    }

    public boolean isDaDat() {
        return daDat;
    }

    public void setDaDat(boolean daDat) {
        this.daDat = daDat;
    }

    public boolean isKhongSuDung() {
        return khongSuDung;
    }

    public void setKhongSuDung(boolean khongSuDung) {
        this.khongSuDung = khongSuDung;
    }

    @Override
    public String toString() {
        String status = khongSuDung ? "Không sử dụng" : (dangSuDung ? "Đang dùng" : (daDat ? "Đã đặt" : "Trống"));
        return ma + " - " + ten + " [" + status + "]";
    }
}
