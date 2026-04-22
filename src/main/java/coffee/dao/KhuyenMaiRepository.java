package coffee.dao;

import coffee.model.KhuyenMai;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiRepository {
    public List<KhuyenMai> findAll() {
        String sql = "SELECT id, code, discount_type, discount_value, active FROM promotion ORDER BY id";
        List<KhuyenMai> promotions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                promotions.add(new KhuyenMai(
                        rs.getInt("id"),
                        rs.getString("code"),
                        "PERCENT".equalsIgnoreCase(rs.getString("discount_type")),
                        rs.getDouble("discount_value"),
                        rs.getBoolean("active")
                ));
            }
            return promotions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KhuyenMai insert(String code, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        String sql = """
                INSERT INTO promotion(code, discount_type, discount_value, active)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, theoPhanTram ? "PERCENT" : "FIXED");
            ps.setDouble(3, giaTriGiam);
            ps.setBoolean(4, kichHoat);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new KhuyenMai(rs.getInt("id"), code, theoPhanTram, giaTriGiam, kichHoat);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int id, String code, boolean theoPhanTram, double giaTriGiam, boolean kichHoat) {
        String sql = """
                UPDATE promotion
                SET code=?, discount_type=?, discount_value=?, active=?
                WHERE id=?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, theoPhanTram ? "PERCENT" : "FIXED");
            ps.setDouble(3, giaTriGiam);
            ps.setBoolean(4, kichHoat);
            ps.setInt(5, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Mã khuyến mãi không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM promotion WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Mã khuyến mãi không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
