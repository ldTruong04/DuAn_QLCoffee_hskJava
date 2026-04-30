package coffee.dao;

import coffee.model.BanCafe;
import coffee.model.NhanVien;
import coffee.model.HoaDon;
import coffee.model.ChiTietHoaDon;
import coffee.model.SanPham;
import coffee.model.VaiTro;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoaDonRepository {
    public HoaDon createInvoice(int tableId, int employeeId) {
        String sql = """
                INSERT INTO invoice(table_id, employee_id, created_at, paid, payment_method)
                VALUES (?, ?, CURRENT_TIMESTAMP, FALSE, NULL)
                RETURNING id, created_at, paid, payment_method
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                BanCafe table = new BanCafe(tableId, "");
                NhanVien emp = new NhanVien(employeeId, "", VaiTro.STAFF, "", "");
                return new HoaDon(
                        rs.getInt("id"),
                        table,
                        emp,
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBoolean("paid"),
                        rs.getString("payment_method")
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void upsertInvoiceItem(int invoiceId, int productId, int quantity, double unitPrice) {
        String sql = """
                INSERT INTO invoice_item(invoice_id, product_id, quantity, unit_price, status)
                VALUES (?, ?, ?, ?, 'PENDING')
                ON CONFLICT (invoice_id, product_id)
                DO UPDATE SET quantity = invoice_item.quantity + EXCLUDED.quantity, status = 'PENDING'
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateInvoiceItemQuantity(int invoiceId, int productId, int quantity) {
        String sql = "UPDATE invoice_item SET quantity=? WHERE invoice_id=? AND product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, invoiceId);
            ps.setInt(3, productId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Món không tồn tại trong hóa đơn");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteInvoiceItem(int invoiceId, int productId) {
        String sql = "DELETE FROM invoice_item WHERE invoice_id=? AND product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, productId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Món không tồn tại trong hóa đơn");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void markPaid(int invoiceId) {
        markPaid(invoiceId, "TIEN_MAT");
    }

    public void markPaid(int invoiceId, String paymentMethod) {
        String normalizedMethod = normalizePaymentMethod(paymentMethod);
        String sql = "UPDATE invoice SET paid=TRUE, payment_method=? WHERE id=? AND paid=FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedMethod);
            ps.setInt(2, invoiceId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Hóa đơn đã thanh toán hoặc không tồn tại");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePromotionAndDiscount(int invoiceId, String promotionCode, long discountAmount) {
        String sql = "UPDATE invoice SET promotion_code=?, discount=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promotionCode);
            ps.setLong(2, discountAmount);
            ps.setInt(3, invoiceId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<HoaDon> findAllDetailed() {
        String sql = """
                SELECT i.id invoice_id, i.created_at, i.paid, i.payment_method,
                       i.promotion_code, i.discount,
                       t.id table_id, t.name table_name, t.occupied,
                      e.id employee_id, e.ho_ten employee_name, e.role, e.username, e.password,
                  p.id product_id, p.name product_name, p.category, p.price, p.description, p.image_path,
                       ii.quantity, ii.unit_price, ii.status
                FROM invoice i
                JOIN cafe_table t ON t.id = i.table_id
                JOIN employee e ON e.id = i.employee_id
                LEFT JOIN invoice_item ii ON ii.invoice_id = i.id
                LEFT JOIN product p ON p.id = ii.product_id
                ORDER BY i.id DESC
                """;
        List<HoaDon> invoices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int currentId = -1;
            HoaDon current = null;
            while (rs.next()) {
                int invoiceId = rs.getInt("invoice_id");
                if (invoiceId != currentId) {
                    currentId = invoiceId;
                    BanCafe table = new BanCafe(rs.getInt("table_id"), rs.getString("table_name"));
                    table.setDangSuDung(rs.getBoolean("occupied"));
                    NhanVien employee = new NhanVien(
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            VaiTro.valueOf(rs.getString("role")),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                    current = new HoaDon(
                            invoiceId,
                            table,
                            employee,
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getBoolean("paid"),
                            rs.getString("payment_method")
                    );
                    String promoCode = rs.getString("promotion_code");
                    if (promoCode != null) {
                        current.setMaKhuyenMai(promoCode);
                    }
                    long discountAmount = rs.getLong("discount");
                    if (discountAmount > 0) {
                        current.setGiamGia(discountAmount);
                    }
                    invoices.add(current);
                }

                int productId = rs.getInt("product_id");
                if (!rs.wasNull()) {
                    SanPham product = new SanPham(
                            productId,
                            rs.getString("product_name"),
                            rs.getString("category"),
                            rs.getDouble("unit_price"),
                            rs.getString("description"),
                            rs.getString("image_path")
                    );
                    current.getDanhSachMon().add(new ChiTietHoaDon(product, rs.getInt("quantity"), rs.getString("status")));
                }
            }
            return invoices;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HoaDon findDetailedById(int invoiceId) {
        return findAllDetailed().stream()
                .filter(i -> i.getMa() == invoiceId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại"));
    }

    public Optional<Integer> findUnpaidInvoiceIdByTableId(int tableId) {
        String sql = "SELECT id FROM invoice WHERE table_id = ? AND paid = FALSE ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id"));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double sumRevenue() {
        String sql = """
                SELECT COALESCE(SUM(ii.quantity * ii.unit_price), 0) revenue
                FROM invoice i
                JOIN invoice_item ii ON ii.invoice_id = i.id
                WHERE i.paid = TRUE
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble("revenue");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Timestamp getInvoiceCreatedAt(int invoiceId) {
        String sql = "SELECT created_at FROM invoice WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Hóa đơn không tồn tại");
                }
                return rs.getTimestamp("created_at");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return "TIEN_MAT";
        }
        String normalized = paymentMethod.trim().toUpperCase();
        if (!"TIEN_MAT".equals(normalized) && !"CHUYEN_KHOAN".equals(normalized)) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
        }
        return normalized;
    }

    public void updateInvoiceItemStatus(int invoiceId, int productId, String status) {
        String sql = "UPDATE invoice_item SET status=? WHERE invoice_id=? AND product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            ps.setInt(3, productId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Món không tồn tại trong hóa đơn");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<coffee.model.MonOrderBep> getPendingOrderItems() {
        String sql = """
                SELECT ii.invoice_id, ii.product_id, ii.quantity, ii.status,
                       p.name AS product_name, p.price AS unit_price,
                       t.name AS table_name, i.created_at
                FROM invoice_item ii
                JOIN invoice i ON i.id = ii.invoice_id
                JOIN product p ON p.id = ii.product_id
                JOIN cafe_table t ON t.id = i.table_id
                WHERE ii.status = 'PENDING'
                ORDER BY i.created_at ASC
                """;
        List<coffee.model.MonOrderBep> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new coffee.model.MonOrderBep(
                        rs.getInt("invoice_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("table_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return items;
    }
}
