package coffee.dao;

import coffee.model.BanCafe;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BanRepository {
    public List<BanCafe> findAll() {
        String sql = "SELECT id, name, occupied, reserved, disabled FROM cafe_table ORDER BY id";
        List<BanCafe> tables = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BanCafe table = new BanCafe(rs.getInt("id"), rs.getString("name"));
                table.setDangSuDung(rs.getBoolean("occupied"));
                table.setDaDat(rs.getBoolean("reserved"));
                table.setKhongSuDung(rs.getBoolean("disabled"));
                tables.add(table);
            }
            return tables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BanCafe insert(String name) {
        String sql = "INSERT INTO cafe_table(name, occupied, reserved, disabled) VALUES (?, FALSE, FALSE, FALSE) RETURNING id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                BanCafe table = new BanCafe(rs.getInt(1), name);
                return table;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int id, String name, boolean occupied) {
        String sql = "UPDATE cafe_table SET name=?, occupied=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setBoolean(2, occupied);
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Bàn không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setDangSuDung(int id, boolean occupied) {
        String sql = "UPDATE cafe_table SET occupied=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, occupied);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setDaDat(int id, boolean reserved) {
        String sql = "UPDATE cafe_table SET reserved=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, reserved);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM cafe_table WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Bàn không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<BanCafe> findById(int id) {
        String sql = "SELECT id, name, occupied, reserved, disabled FROM cafe_table WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BanCafe table = new BanCafe(rs.getInt("id"), rs.getString("name"));
                    table.setDangSuDung(rs.getBoolean("occupied"));
                    table.setDaDat(rs.getBoolean("reserved"));
                    table.setKhongSuDung(rs.getBoolean("disabled"));
                    return Optional.of(table);
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setKhongSuDung(int id, boolean khongSuDung) {
        String sql = "UPDATE cafe_table SET disabled=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, khongSuDung);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
