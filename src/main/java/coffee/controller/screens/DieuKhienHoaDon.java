package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.model.BanCafe;
import coffee.model.HoaDon;
import coffee.model.KhuyenMai;
import coffee.model.NhanVien;
import coffee.model.SanPham;
import coffee.util.PDFUtil;
import coffee.view.screens.ManHinhHoaDon;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
        view.bindTableStatusRefresh(this::refresh);
        view.bindCashInputChange(this::capNhatTienThoiTamTinh);
        view.bindPromotionSelection(this::onPromotionSelected);
        view.bindApplyPromotion(this::apDungKhuyenMaiTuNhapTay);
        view.bindClearPromotion(this::boKhuyenMai);

        view.quickExactButton.addActionListener(e -> setQuickCashByOffset(0));
        view.refreshButton.addActionListener(e -> refresh());

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

        view.invoiceItemTableModel.addTableModelListener(e -> {
            if (e.getType() != TableModelEvent.UPDATE) {
                return;
            }
            int column = e.getColumn();
            int row = e.getFirstRow();
            if (column == 1) {
                runAction(() -> {
                    applyQuantityEditFromCell(e.getFirstRow());
                    onDataChanged.run();
                });
            } else if (column == 3) {
                runAction(() -> {
                    applyNoteEditFromCell(row);
                    onDataChanged.run();
                });
            }
        });

        view.payCashButton.addActionListener(e -> runAction(() -> {
            if (currentInvoiceId == null) {
                throw new IllegalStateException("Chưa có hóa đơn để thanh toán");
            }
            long tongTien = view.getTongTienCanThu();
            long tienKhachDua = view.getTienKhachDua();
            if (tienKhachDua < tongTien) {
                view.capNhatTienThoi(0, false);
                throw new IllegalStateException("Tiền khách đưa chưa đủ để thanh toán");
            }
            long tienThoi = tienKhachDua - tongTien;
            boolean exportBeforePayment = askToExportInvoice();
            if (!xacNhanThanhToan("tiền mặt", tongTien, tienKhachDua, tienThoi)) {
                return;
            }
            view.capNhatTienThoi(tienKhachDua - tongTien, true);
            int invoiceIdToExport = currentInvoiceId;
            String invoiceText = appController.getInvoiceDetailText(invoiceIdToExport);
            saveInvoicePromotion(currentInvoiceId);
            appController.payInvoice(currentInvoiceId, "TIEN_MAT");
            String exportPath = exportInvoiceIfRequested(invoiceIdToExport, invoiceText, "TIEN_MAT", tongTien, tienKhachDua, tienThoi, exportBeforePayment);
            JOptionPane.showMessageDialog(view, "Thanh toán thành công: " + String.format("%,.0f", (double) tongTien) + " VND");
            if (exportPath != null) {
                JOptionPane.showMessageDialog(view, "Đã xuất hóa đơn: " + exportPath);
            }
            onDataChanged.run();
        }));

        view.payTransferButton.addActionListener(e -> runAction(() -> {
            if (currentInvoiceId == null) {
                throw new IllegalStateException("Chưa có hóa đơn để thanh toán");
            }
            long tongTien = view.getTongTienCanThu();
            boolean exportBeforePayment = askToExportInvoice();
            if (!xacNhanThanhToan("chuyển khoản", tongTien, 0, 0)) {
                return;
            }
            view.capNhatTienThoi(0, true);
            int invoiceIdToExport = currentInvoiceId;
            String invoiceText = appController.getInvoiceDetailText(invoiceIdToExport);
            saveInvoicePromotion(currentInvoiceId);
            appController.payInvoice(currentInvoiceId, "CHUYEN_KHOAN");
            String exportPath = exportInvoiceIfRequested(invoiceIdToExport, invoiceText, "CHUYEN_KHOAN", tongTien, tongTien, 0, exportBeforePayment);
            JOptionPane.showMessageDialog(view, "Thanh toán chuyển khoản thành công: " + String.format("%,.0f", (double) tongTien) + " VND");
            if (exportPath != null) {
                JOptionPane.showMessageDialog(view, "Đã xuất hóa đơn: " + exportPath);
            }
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
            String pdfPath = PDFUtil.exportTextToPdf(content, "hoa_don_" + currentInvoiceId + "_" + System.currentTimeMillis());
            if (pdfPath != null) {
                JOptionPane.showMessageDialog(view, "Đã xuất hóa đơn PDF: " + pdfPath);
            } else {
                throw new RuntimeException("Lỗi xuất PDF hóa đơn");
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
            view.setAvailablePromotions(appController.getPromotions());
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
        view.boKhuyenMai();
        view.setTableSelectionEnabled(!view.isTakeawaySelected());
        if (view.isTakeawaySelected()) {
            view.setTableStatusText("Mang đi");
        } else if (tableId == null) {
            view.setTableStatusText("-");
        } else if (openInvoice == null || openInvoice.getDanhSachMon().isEmpty()) {
            view.setTableStatusText("Bàn trống");
        } else {
            view.setTableStatusText("Đang dùng");
        }
        view.setActionButtonsEnabled(view.isTakeawaySelected() || tableId != null);
        capNhatTienThoiTamTinh();
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

    private void applyQuantityEditFromCell(int row) {
        ensureCurrentInvoice();
        var item = view.getInvoiceItemAtRow(row);
        if (item == null) {
            return;
        }

        Object editedValue = view.invoiceItemTableModel.getValueAt(row, 1);
        int targetQuantity = parseEditedQuantity(editedValue, item.getSoLuong());
        int productId = item.getSanPham().getMa();

        if (targetQuantity <= 0) {
            appController.removeInvoiceItem(currentInvoiceId, productId);
        } else {
            appController.updateInvoiceItemQuantity(currentInvoiceId, productId, targetQuantity);
        }
    }

    private void applyNoteEditFromCell(int row) {
        ensureCurrentInvoice();
        var item = view.getInvoiceItemAtRow(row);
        if (item == null) {
            return;
        }
        Object editedValue = view.invoiceItemTableModel.getValueAt(row, 3);
        String note = editedValue == null ? "" : editedValue.toString().trim();
        appController.updateInvoiceItemNote(currentInvoiceId, item.getSanPham().getMa(), note);
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

    private void runAction(Runnable action) {
        try {
            action.run();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage());
        }
    }

    private void setQuickCashByOffset(long offset) {
        long tongTien = view.getTongTienCanThu();
        if (tongTien <= 0) {
            return;
        }
        view.setTienKhachDua(tongTien + Math.max(0, offset));
        capNhatTienThoiTamTinh();
    }

    private void capNhatTienThoiTamTinh() {
        long tongTien = view.getTongTienCanThu();
        if (tongTien <= 0) {
            view.capNhatTienThoi(0, true);
            return;
        }
        long tienKhachDua = view.getTienKhachDua();
        if (tienKhachDua < tongTien) {
            view.capNhatTienThoi(0, false);
        } else {
            view.capNhatTienThoi(tienKhachDua - tongTien, true);
        }
    }

    private void onPromotionSelected() {
        if (updatingView) {
            return;
        }
        KhuyenMai selected = view.getSelectedPromotion();
        if (selected == null) {
            return;
        }
        view.setPromotionCodeInput(selected.getMaCode());
        apDungKhuyenMai(selected);
    }

    private void apDungKhuyenMaiTuNhapTay() {
        String code = view.getPromotionCodeInput();
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mã khuyến mãi hoặc chọn trong danh sách");
        }
        KhuyenMai km = view.findPromotionByCode(code);
        if (km == null) {
            throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ hoặc đã tạm dừng");
        }
        apDungKhuyenMai(km);
    }

    private void apDungKhuyenMai(KhuyenMai km) {
        long tongTien = view.getTongTienHienTai();
        if (tongTien <= 0) {
            throw new IllegalStateException("Chưa có món trong hóa đơn để áp dụng khuyến mãi");
        }
        long discount = Math.round(km.tinhSoTienGiam(tongTien));
        view.apDungKhuyenMai(km, discount);
        capNhatTienThoiTamTinh();
    }

    private void boKhuyenMai() {
        view.boKhuyenMai();
        capNhatTienThoiTamTinh();
    }

    private boolean xacNhanThanhToan(String phuongThuc, long tongTien, long tienKhachDua, long tienThoi) {
        StringBuilder message = new StringBuilder();
        message.append("Xác nhận thanh toán bằng ").append(phuongThuc).append("?\n")
                .append("Tổng thanh toán: ").append(String.format("%,.0f", (double) tongTien)).append(" VND\n");
        if ("tiền mặt".equals(phuongThuc)) {
            message.append("Khách đưa: ").append(String.format("%,.0f", (double) tienKhachDua)).append(" VND\n")
                    .append("Tiền thối: ").append(String.format("%,.0f", (double) tienThoi)).append(" VND\n");
        }
        if (view.isExportInvoiceSelected()) {
            message.append("Hệ thống sẽ xuất hóa đơn ra Desktop (.pdf).\n");
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                message.toString(),
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return confirm == JOptionPane.YES_OPTION;
    }

    private boolean askToExportInvoice() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có muốn xuất hóa đơn ra PDF trước khi thanh toán?",
                "Xuất hóa đơn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return confirm == JOptionPane.YES_OPTION;
    }

    private String exportInvoiceIfRequested(int invoiceId,
                                           String invoiceText,
                                           String paymentMethod,
                                           long tongTien,
                                           long tienKhachDua,
                                           long tienThoi,
                                           boolean requestExport) {
        if (!requestExport && !view.isExportInvoiceSelected()) {
            return null;
        }
        StringBuilder exportText = new StringBuilder(invoiceText);
        exportText.append("\n\nPhương thức thanh toán: ").append(paymentMethod.replace("TIEN_MAT", "Tiền mặt").replace("CHUYEN_KHOAN", "Chuyển khoản"));
        exportText.append("\nTổng thanh toán: ").append(String.format("%,.0f", (double) tongTien)).append(" VND");
        if (tienKhachDua > 0) {
            exportText.append("\nKhách đưa: ").append(String.format("%,.0f", (double) tienKhachDua)).append(" VND");
            exportText.append("\nTiền thối: ").append(String.format("%,.0f", (double) tienThoi)).append(" VND");
        }
        return PDFUtil.exportTextToPdf(exportText.toString(), "hoa_don_" + invoiceId + "_" + System.currentTimeMillis());
    }

    private void saveInvoicePromotion(int invoiceId) {
        HoaDon invoice = appController.getInvoices().stream()
                .filter(item -> item.getMa() == invoiceId)
                .findFirst()
                .orElse(null);
        if (invoice == null) {
            return;
        }
        KhuyenMai km = view.getKhuyenMaiDangApDung();
        String promoCode = (km != null) ? km.getMaCode() : null;
        long discountAmount = view.getGiamGiaHienTai();

        // Update in database
        appController.updateInvoicePromotionAndDiscount(invoiceId, promoCode, discountAmount);

        // Update in memory model
        if (km != null) {
            invoice.setMaKhuyenMai(promoCode);
        }
        invoice.setGiamGia(discountAmount);
    }
}
