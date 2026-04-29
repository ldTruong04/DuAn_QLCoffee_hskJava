package coffee.model;

public class NhanVien {
    private final int ma;
    private String hoTen;
    private int namSinh;
    private double luong;
    private GioiTinh gioiTinh;
    private String anhDaiDien;
    private VaiTro vaiTro;
    private String tenDangNhap;
    private String matKhau;
    private String email;

    public NhanVien(int ma,
                    String hoTen,
                    int namSinh,
                    double luong,
                    GioiTinh gioiTinh,
                    String anhDaiDien,
                    VaiTro vaiTro,
                    String tenDangNhap,
                    String matKhau,
                    String email) {
        this.ma = ma;
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.luong = luong;
        this.gioiTinh = gioiTinh;
        this.anhDaiDien = anhDaiDien;
        this.vaiTro = vaiTro;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
    }

    public NhanVien(int ma, String hoTen, VaiTro vaiTro, String tenDangNhap, String matKhau) {
        this(ma, hoTen, 1990, 0.0, GioiTinh.KHAC, null, vaiTro, tenDangNhap, matKhau, null);
    }

    public int getMa() {
        return ma;
    }

    public String getHoTen() {
        return hoTen;
    }

    public int getNamSinh() {
        return namSinh;
    }

    public double getLuong() {
        return luong;
    }

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getEmail() {
        return email;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setNamSinh(int namSinh) {
        this.namSinh = namSinh;
    }

    public void setLuong(double luong) {
        this.luong = luong;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Compatibility aliases for existing code paths
    public String getTen() {
        return getHoTen();
    }

    public void setTen(String ten) {
        setHoTen(ten);
    }

    @Override
    public String toString() {
        return ma + " - " + hoTen + " (" + vaiTro + ")";
    }
}
