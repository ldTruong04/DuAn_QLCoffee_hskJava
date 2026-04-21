package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.model.BanCafe;
import coffee.model.HoaDon;
import coffee.model.NhanVien;
import coffee.model.SanPham;
import coffee.view.screens.ManHinhHoaDon;

import javax.swing.*;
import java.util.List;

public class DieuKhienHoaDon {
    private final DieuKhienUngDung appController;
    private final PhienUngDung session;
    private final ManHinhHoaDon view;
    private final Runnable onDataChanged;

    private Integer currentContextTableId;
    private Integer currentInvoiceId;
    private boolean updatingView;

    public DieuKhienHoaDon(DieuKhienUngDung appController, PhienUngDung session, ManHinhHoaDon view, Runnable onDataChanged) {
        this.appController = appController;
        this.session = session;
        this.view = view;
        this.onDataChanged = onDataChanged;
        bind();
    }

    private void bind() {
        view.bindStoreProductSelection();
        view.bindInvoiceTypeChange(this::onContextChanged);
        view.bindTableChange(this::onContextChanged);

        view.addProductButton.addActionListener(e -> runAction(() -> {
            SanPham product = view.getSelectedStoreProduct();
            if (product == null) {
                throw new IllegalArgumentException("Vui lòng chọn món");
            }
            int quantity = view.getQuantity();
            if (quantity <= 0) {
                throw new IllegalArgumentException("Số lượng phải > 0");
            }

            Integer tableId = resolveContextTableId(true);
            if (tableId == null) {
                throw new IllegalStateException("Không tìm thấy bàn mang đi");
            }
            NhanVien currentUser = requireCurrentUser();
            int invoiceId = appController.placeOrder(tableId, currentUser, product.getMa(), quantity);
            JOptionPane.showMessageDialog(view, "Đã thêm món vào hóa đơn #" + invoiceId);
            onDataChanged.run();
        }));

        view.increaseItemButton.addActionListener(e -> runAction(() -> {
            ensureCurrentInvoice();
            var item = view.getSelectedInvoiceItem();
            if (item == null) {
                throw new IllegalArgumentException("Vui lòng chọn món đã gọi");
            }
            int newQuantity = item.getSoLuong() + 1;
            appController.updateInvoiceItemQuantity(currentInvoiceId, item.getSanPham().getMa(), newQuantity);
            onDataChanged.run();
        }));

        view.decreaseItemButton.addActionListener(e -> runAction(() -> {
            ensureCurrentInvoice();
            var item = view.getSelectedInvoiceItem();
            if (item == null) {
                throw new IllegalArgumentException("Vui lòng chọn món đã gọi");
            }
            int newQuantity = item.getSoLuong() - 1;
            if (newQuantity <= 0) {
                appController.removeInvoiceItem(currentInvoiceId, item.getSanPham().getMa());
            } else {
                appController.updateInvoiceItemQuantity(currentInvoiceId, item.getSanPham().getMa(), newQuantity);
            }
            onDataChanged.run();
        }));

        view.payButton.addActionListener(e -> runAction(() -> {
            if (currentInvoiceId == null) {
                throw new IllegalStateException("Chưa có hóa đơn để thanh toán");
            }
            double total = appController.payInvoice(currentInvoiceId);
            JOptionPane.showMessageDialog(view, "Thanh toán thành công: " + String.format("%.0f", total) + " VND");
            onDataChanged.run();
        }));

        view.printButton.addActionListener(e -> runAction(() -> {
            if (currentInvoiceId == null) {
                throw new IllegalStateException("Chưa có hóa đơn để in");
            }
            String content = appController.getInvoiceDetailText(currentInvoiceId);
            JTextArea area = new JTextArea(content);
            area.setEditable(false);
            JScrollPane pane = new JScrollPane(area);
            pane.setPreferredSize(new java.awt.Dimension(540, 320));
            JOptionPane.showMessageDialog(view, pane, "Chi tiết hóa đơn", JOptionPane.INFORMATION_MESSAGE);
            try {
                area.print();
            } catch (java.awt.print.PrinterException ex) {
                throw new RuntimeException("Lỗi in tài liệu", ex);
            }
        }));
    }

    public void refresh() {
        updatingView = true;
        try {
            NhanVien currentUser = session.getCurrentUser();
            view.setEmployeeName(currentUser == null ? null : currentUser.getHoTen());

            List<BanCafe> allTables = appController.getTables();
            List<BanCafe> dineInTables = allTables.stream()
                    .filter(table -> !isTakeawayTable(table))
                    .toList();

            BanCafe selectedForCombo = null;
            if (currentContextTableId != null) {
                selectedForCombo = dineInTables.stream()
                        .filter(table -> table.getMa() == currentContextTableId)
                        .findFirst()
                        .orElse(null);
            }

            view.setTables(dineInTables, selectedForCombo);
            view.setStoreProducts(appController.getProducts());
            view.selectFirstProductIfNeeded();
        } finally {
            updatingView = false;
        }

        applyContextToView();
    }

    private void onContextChanged() {
        if (updatingView) {
            return;
        }
        applyContextToView();
    }

    private void applyContextToView() {
        Integer tableId = resolveContextTableId(false);
        currentContextTableId = tableId;

        HoaDon openInvoice = null;
        if (tableId != null) {
            openInvoice = appController.getInvoices().stream()
                    .filter(invoice -> invoice.getBan().getMa() == tableId && !invoice.isDaThanhToan())
                    .findFirst()
                    .orElse(null);
        }

        currentInvoiceId = openInvoice == null ? null : openInvoice.getMa();
        view.setInvoiceItems(openInvoice == null ? List.of() : openInvoice.getDanhSachMon(), currentInvoiceId);
        view.setTableSelectionEnabled(!view.isTakeawaySelected());
        view.setActionButtonsEnabled(view.isTakeawaySelected() || tableId != null);
    }

    private Integer resolveContextTableId(boolean createTakeawayIfMissing) {
        if (view.isTakeawaySelected()) {
            BanCafe takeaway = findTakeawayTable(createTakeawayIfMissing);
            return takeaway == null ? null : takeaway.getMa();
        }

        BanCafe selectedTable = view.getSelectedTable();
        if (selectedTable == null) {
            return null;
        }
        return selectedTable.getMa();
    }

    private BanCafe findTakeawayTable(boolean createIfMissing) {
        BanCafe existing = appController.getTables().stream()
                .filter(this::isTakeawayTable)
                .findFirst()
                .orElse(null);
        if (existing != null || !createIfMissing) {
            return existing;
        }

        appController.addTable("Mang đi");
        return appController.getTables().stream()
                .filter(this::isTakeawayTable)
                .findFirst()
                .orElse(null);
    }

    private boolean isTakeawayTable(BanCafe table) {
        if (table == null || table.getTen() == null) {
            return false;
        }
        String normalized = table.getTen().trim().toLowerCase();
        return normalized.equals("mang đi") || normalized.equals("bán mang đi");
    }

    private NhanVien requireCurrentUser() {
        if (session.getCurrentUser() == null) {
            throw new IllegalStateException("Phiên đăng nhập không hợp lệ");
        }
        return session.getCurrentUser();
    }

    private void ensureCurrentInvoice() {
        if (currentInvoiceId == null) {
            throw new IllegalStateException("Chưa có hóa đơn để chỉnh sửa");
        }
    }

    private void runAction(Runnable action) {
        try {
            action.run();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage());
        }
    }
}
