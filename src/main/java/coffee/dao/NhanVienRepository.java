package coffee.dao;

import coffee.model.NhanVien;
import coffee.model.GioiTinh;
import coffee.model.VaiTro;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienRepository {
    public Optional<NhanVien> findByCredentials(String username, String password) {
        String sql = "SELECT id, ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password, email FROM employee WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<NhanVien> findAll() {
        String sql = "SELECT id, ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password, email FROM employee ORDER BY id";
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapEmployee(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NhanVien insert(String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password, String email) {
        String sql = "INSERT INTO employee(ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setInt(2, namSinh);
            ps.setDouble(3, luong);
            ps.setString(4, gioiTinh.name());
            ps.setString(5, anhDaiDien);
            ps.setString(6, role.name());
            ps.setString(7, username);
            ps.setString(8, password);
            ps.setString(9, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new NhanVien(rs.getInt(1), hoTen, namSinh, luong, gioiTinh, anhDaiDien, role, username, password, email);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int id, String hoTen, int namSinh, double luong, GioiTinh gioiTinh, String anhDaiDien, VaiTro role, String username, String password, String email) {
        String sql = "UPDATE employee SET ho_ten=?, nam_sinh=?, luong=?, gioi_tinh=?, anh_dai_dien=?, role=?, username=?, password=?, email=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setInt(2, namSinh);
            ps.setDouble(3, luong);
            ps.setString(4, gioiTinh.name());
            ps.setString(5, anhDaiDien);
            ps.setString(6, role.name());
            ps.setString(7, username);
            ps.setString(8, password);
            ps.setString(9, email);
            ps.setInt(10, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Nhân viên không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM employee WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Nhân viên không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<NhanVien> findById(int id) {
        String sql = "SELECT id, ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password, email FROM employee WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private NhanVien mapEmployee(ResultSet rs) throws Exception {
        return new NhanVien(
                rs.getInt("id"),
                rs.getString("ho_ten"),
                rs.getInt("nam_sinh"),
                rs.getDouble("luong"),
                GioiTinh.valueOf(rs.getString("gioi_tinh")),
                rs.getString("anh_dai_dien"),
                VaiTro.valueOf(rs.getString("role")),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email")
        );
    }

    public Optional<NhanVien> findByEmail(String email) {
        String sql = "SELECT id, ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password, email FROM employee WHERE email=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NhanVien insert(String name, VaiTro role, String username, String password) {
        return insert(name, 1990, 0.0, GioiTinh.KHAC, null, role, username, password, null);
    }

    public void update(int id, String name, VaiTro role, String username, String password) {
        update(id, name, 1990, 0.0, GioiTinh.KHAC, null, role, username, password, null);
    }
}
