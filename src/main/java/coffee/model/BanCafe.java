package coffee.model;

public class BanCafe {
    private final int ma;
    private String ten;
    private boolean dangSuDung;

    public BanCafe(int ma, String ten) {
        this.ma = ma;
        this.ten = ten;
        this.dangSuDung = false;
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

    @Override
    public String toString() {
        return ma + " - " + ten + " [" + (dangSuDung ? "Đang dùng" : "Trống") + "]";
    }
}
