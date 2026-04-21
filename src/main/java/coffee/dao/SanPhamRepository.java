package coffee.dao;

import coffee.model.SanPham;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SanPhamRepository {
    public List<SanPham> findAll() {
        String sql = "SELECT id, name, category, price, description, image_path FROM product ORDER BY id";
        List<SanPham> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new SanPham(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_path")
                ));
            }
            return products;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SanPham insert(String name, String category, double price, String description, String imagePath) {
        String sql = "INSERT INTO product(name, category, price, description, image_path) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.setString(5, imagePath);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new SanPham(rs.getInt(1), name, category, price, description, imagePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int id, String name, String category, double price, String description, String imagePath) {
        String sql = "UPDATE product SET name=?, category=?, price=?, description=?, image_path=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.setString(5, imagePath);
            ps.setInt(6, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM product WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SanPham> findById(int id) {
        String sql = "SELECT id, name, category, price, description, image_path FROM product WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SanPham(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getString("description"),
                            rs.getString("image_path")
                    ));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<SanPham> findTopSelling(int limit) {
        String sql = """
              SELECT p.id, p.name, p.category, p.price, p.description, p.image_path,
                       COALESCE(SUM(CASE WHEN i.paid = TRUE THEN ii.quantity ELSE 0 END), 0) sold
                FROM product p
                LEFT JOIN invoice_item ii ON p.id = ii.product_id
                LEFT JOIN invoice i ON i.id = ii.invoice_id
              GROUP BY p.id, p.name, p.category, p.price, p.description, p.image_path
                ORDER BY sold DESC, p.id ASC
                LIMIT ?
                """;
        List<SanPham> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham p = new SanPham(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_path")
                    );
                    p.setSoLuongDaBan(rs.getInt("sold"));
                    products.add(p);
                }
            }
            return products;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
