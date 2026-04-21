package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.model.BanCafe;
import coffee.model.HoaDon;
import coffee.model.NhanVien;
import coffee.model.SanPham;
import coffee.view.screens.ManHinhBan;

import javax.swing.*;
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

        view.increaseItemButton.addActionListener(e -> runAction(() -> {
            ensureEditableInvoice();
            var item = view.getSelectedOrderItem();
            if (item == null) {
                throw new IllegalArgumentException("Vui lòng chọn món đã gọi");
            }
            int newQuantity = item.getSoLuong() + 1;
            appController.updateInvoiceItemQuantity(currentInvoiceId, item.getSanPham().getMa(), newQuantity);
        }));

        view.decreaseItemButton.addActionListener(e -> runAction(() -> {
            ensureEditableInvoice();
            var item = view.getSelectedOrderItem();
            if (item == null) {
                throw new IllegalArgumentException("Vui lòng chọn món đã gọi");
            }
            int newQuantity = item.getSoLuong() - 1;
            if (newQuantity <= 0) {
                appController.removeInvoiceItem(currentInvoiceId, item.getSanPham().getMa());
            } else {
                appController.updateInvoiceItemQuantity(currentInvoiceId, item.getSanPham().getMa(), newQuantity);
            }
        }));
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
}
