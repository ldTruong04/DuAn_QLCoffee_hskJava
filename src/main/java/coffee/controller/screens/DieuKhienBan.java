package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.model.BanCafe;
import coffee.model.HoaDon;
import coffee.model.NhanVien;
import coffee.model.SanPham;
import coffee.view.screens.ManHinhBan;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.util.List;

public class DieuKhienBan {
    private final DieuKhienUngDung appController;
    private final PhienUngDung session;
    private final ManHinhBan view;
    private Integer selectedTableId;
    private Integer currentInvoiceId;

    public DieuKhienBan(DieuKhienUngDung appController, PhienUngDung session, ManHinhBan view) {
        this.appController = appController;
        this.session = session;
        this.view = view;
        bind();
    }

    private void bind() {
        view.bindStoreProductSelection();
        view.orderButton.addActionListener(e -> runAction(() -> {
            ensureTableSelected();
            SanPham product = view.getSelectedProduct();
            if (product == null) {
                throw new IllegalArgumentException("Vui lòng chọn món");
            }
            NhanVien currentUser = requireLoggedInUser();
            int invoiceId = appController.placeOrder(selectedTableId, currentUser, product.getMa(), view.getQuantity());
            JOptionPane.showMessageDialog(view, "Đã gọi món vào hóa đơn #" + invoiceId);
        }));

        view.itemTableModel.addTableModelListener(e -> {
            if (e.getType() != TableModelEvent.UPDATE || e.getColumn() != 1) {
                return;
            }
            runAction(() -> applyQuantityEditFromCell(e.getFirstRow()));
        });

        view.reserveButton.addActionListener(e -> runAction(() -> {
            ensureTableSelected();
            appController.setTableReserved(selectedTableId, true);
            JOptionPane.showMessageDialog(view, "Đã đặt bàn thành công");
        }));

        view.cancelReserveButton.addActionListener(e -> runAction(() -> {
            ensureTableSelected();
            appController.setTableReserved(selectedTableId, false);
            JOptionPane.showMessageDialog(view, "Đã hủy đặt bàn");
        }));

        view.maintenanceButton.addActionListener(e -> runAction(() -> {
            ensureTableSelected();
            BanCafe table = appController.getTables().stream()
                    .filter(t -> t.getMa() == selectedTableId).findFirst().orElse(null);
            if (table != null) {
                boolean newStatus = !table.isKhongSuDung();
                appController.setTableDisabled(selectedTableId, newStatus);
                JOptionPane.showMessageDialog(view, newStatus ? "Đã chuyển bàn sang trạng thái bảo trì" : "Đã mở lại bàn");
            }
        }));

        view.onAddTableRequested = () -> runAction(() -> {
            String tableName = JOptionPane.showInputDialog(view, "Nhập tên bàn mới:", "Thêm bàn", JOptionPane.PLAIN_MESSAGE);
            if (tableName != null && !tableName.trim().isEmpty()) {
                appController.addTable(tableName.trim());
                JOptionPane.showMessageDialog(view, "Đã thêm bàn mới thành công!");
            }
        });
    }

    public void refresh() {
        List<BanCafe> tables = appController.getTables();
        if (selectedTableId == null && !tables.isEmpty()) {
            selectedTableId = tables.get(0).getMa();
        }
        view.setProducts(appController.getProducts());
        view.selectFirstProductIfNeeded();
        view.renderTableCards(tables, this::selectTable, selectedTableId);
        updateSelectedTableDetail(tables);
    }

    private void runAction(Runnable action) {
        try {
            action.run();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage());
        }
    }

    private void selectTable(int tableId) {
        selectedTableId = tableId;
        refresh();
    }

    private void updateSelectedTableDetail(List<BanCafe> tables) {
        if (selectedTableId == null) {
            view.showSelectedTable(null, null);
            return;
        }

        BanCafe selectedTable = tables.stream()
                .filter(table -> table.getMa() == selectedTableId)
                .findFirst()
                .orElse(null);

        if (selectedTable == null) {
            selectedTableId = null;
            view.showSelectedTable(null, null);
            return;
        }

        HoaDon openInvoice = appController.getInvoices().stream()
                .filter(invoice -> invoice.getBan().getMa() == selectedTableId && !invoice.isDaThanhToan())
                .findFirst()
                .orElse(null);
        currentInvoiceId = openInvoice == null ? null : openInvoice.getMa();
        view.showSelectedTable(selectedTable, openInvoice);
    }

    private void ensureTableSelected() {
        if (selectedTableId == null) {
            throw new IllegalStateException("Vui lòng chọn bàn");
        }
    }

    private NhanVien requireLoggedInUser() {
        if (session.getCurrentUser() == null) {
            throw new IllegalStateException("Phiên đăng nhập không hợp lệ");
        }
        return session.getCurrentUser();
    }

    private void ensureEditableInvoice() {
        if (currentInvoiceId == null) {
            throw new IllegalStateException("Bàn này chưa có hóa đơn để chỉnh sửa");
        }
    }

    private void applyQuantityEditFromCell(int row) {
        ensureEditableInvoice();
        var item = view.getOrderItemAtRow(row);
        if (item == null) {
            return;
        }

        Object editedValue = view.itemTableModel.getValueAt(row, 1);
        int targetQuantity = parseEditedQuantity(editedValue, item.getSoLuong());
        int productId = item.getSanPham().getMa();

        if (targetQuantity <= 0) {
            appController.removeInvoiceItem(currentInvoiceId, productId);
        } else {
            appController.updateInvoiceItemQuantity(currentInvoiceId, productId, targetQuantity);
        }
    }

    private int parseEditedQuantity(Object editedValue, int currentQuantity) {
        if (editedValue == null) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }
        String text = editedValue.toString().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }

        if (text.startsWith("+") || text.startsWith("-")) {
            int delta = Integer.parseInt(text);
            return currentQuantity + delta;
        }
        return Integer.parseInt(text);
    }

}
