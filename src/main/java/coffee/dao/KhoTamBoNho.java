package coffee.dao;

import coffee.model.BanCafe;
import coffee.model.GioiTinh;
import coffee.model.NhanVien;
import coffee.model.HoaDon;
import coffee.model.SanPham;
import coffee.model.VaiTro;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class KhoTamBoNho {
    private final List<SanPham> products = new ArrayList<>();
    private final List<BanCafe> tables = new ArrayList<>();
    private final List<NhanVien> employees = new ArrayList<>();
    private final List<HoaDon> invoices = new ArrayList<>();

    private final AtomicInteger productId = new AtomicInteger(1);
    private final AtomicInteger tableId = new AtomicInteger(1);
    private final AtomicInteger employeeId = new AtomicInteger(1);
    private final AtomicInteger invoiceId = new AtomicInteger(1);

    public KhoTamBoNho() {
        seedData();
    }

    private void seedData() {
        addProduct("Cà phê đen", "Cà phê", 25000, "Cà phê đậm vị", null);
        addProduct("Cà phê sữa", "Cà phê", 30000, "Cà phê sữa béo thơm", null);
        addProduct("Trà đào", "Trà", 35000, "Trà đào thanh mát", null);

        for (int i = 1; i <= 8; i++) {
            tables.add(new BanCafe(tableId.getAndIncrement(), "B" + i));
        }

        employees.add(new NhanVien(employeeId.getAndIncrement(), "Quản lý", 1985, 15000000, GioiTinh.NAM, null, VaiTro.ADMIN, "admin", "admin123"));
        employees.add(new NhanVien(employeeId.getAndIncrement(), "Nhân viên A", 1998, 8000000, GioiTinh.NU, null, VaiTro.STAFF, "staff", "staff123"));
    }

    public SanPham addProduct(String name, String category, double price, String moTa, String duongDanHinhAnh) {
        SanPham product = new SanPham(productId.getAndIncrement(), name, category, price, moTa, duongDanHinhAnh);
        products.add(product);
        return product;
    }

    public BanCafe addTable(String name) {
        BanCafe table = new BanCafe(tableId.getAndIncrement(), name);
        tables.add(table);
        return table;
    }

    public NhanVien addEmployee(String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password) {
        NhanVien employee = new NhanVien(employeeId.getAndIncrement(), hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password);
        employees.add(employee);
        return employee;
    }

    public HoaDon addInvoice(BanCafe table, NhanVien staff) {
        HoaDon invoice = new HoaDon(invoiceId.getAndIncrement(), table, staff);
        invoices.add(invoice);
        return invoice;
    }

    public List<SanPham> getProducts() {
        return products;
    }

    public List<BanCafe> getTables() {
        return tables;
    }

    public List<NhanVien> getEmployees() {
        return employees;
    }

    public List<HoaDon> getInvoices() {
        return invoices;
    }

    public Optional<NhanVien> findEmployeeByCredentials(String username, String password) {
        return employees.stream()
                .filter(e -> e.getTenDangNhap().equals(username) && e.getMatKhau().equals(password))
                .findFirst();
    }

    public Optional<SanPham> findProductById(int id) {
        return products.stream().filter(p -> p.getMa() == id).findFirst();
    }

    public Optional<BanCafe> findTableById(int id) {
        return tables.stream().filter(t -> t.getMa() == id).findFirst();
    }

    public Optional<HoaDon> findInvoiceById(int id) {
        return invoices.stream().filter(i -> i.getMa() == id).findFirst();
    }

    public List<SanPham> getTopProducts(int limit) {
        return products.stream()
                .sorted(Comparator.comparingInt(SanPham::getSoLuongDaBan).reversed())
                .limit(limit)
                .toList();
    }
}
