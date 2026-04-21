package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.model.ChiTietHoaDon;
import coffee.model.HoaDon;
import coffee.view.screens.ManHinhDanhSachHoaDon;

import java.time.format.DateTimeFormatter;

public class DieuKhienDanhSachHoaDon {
    private final DieuKhienUngDung appController;
    private final ManHinhDanhSachHoaDon view;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DieuKhienDanhSachHoaDon(DieuKhienUngDung appController, ManHinhDanhSachHoaDon view) {
        this.appController = appController;
        this.view = view;
        bind();
    }

    private void bind() {
        view.invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedInvoiceDetails();
            }
        });
    }

    public void refresh() {
        Integer selectedInvoiceId = getSelectedInvoiceId();
        view.invoiceTableModel.setRowCount(0);
        for (HoaDon invoice : appController.getInvoices()) {
            view.invoiceTableModel.addRow(new Object[]{
                    invoice.getMa(),
                    "Khách lẻ",
                    invoice.getNhanVien().getHoTen(),
                    String.format("%.0f", invoice.getTongTien()),
                    invoice.getThoiGianTao().format(formatter),
                    invoice.getBan().getTen()
            });
        }

        if (selectedInvoiceId != null) {
            reselectInvoice(selectedInvoiceId);
        } else {
            showSelectedInvoiceDetails();
        }
    }

    private void showSelectedInvoiceDetails() {
        view.detailTableModel.setRowCount(0);
        Integer invoiceId = getSelectedInvoiceId();
        if (invoiceId == null) {
            return;
        }

        HoaDon invoice = appController.getInvoices().stream()
                .filter(item -> item.getMa() == invoiceId)
                .findFirst()
                .orElse(null);
        if (invoice == null) {
            return;
        }

        for (ChiTietHoaDon item : invoice.getDanhSachMon()) {
            view.detailTableModel.addRow(new Object[]{
                    item.getSanPham().getTen(),
                    item.getSoLuong(),
                    String.format("%.0f", item.getSanPham().getGia()),
                    String.format("%.0f", item.getThanhTien())
            });
        }
    }

    private Integer getSelectedInvoiceId() {
        int row = view.invoiceTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        Object value = view.invoiceTableModel.getValueAt(row, 0);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void reselectInvoice(int invoiceId) {
        for (int i = 0; i < view.invoiceTableModel.getRowCount(); i++) {
            Object value = view.invoiceTableModel.getValueAt(i, 0);
            if (value != null && value.toString().equals(String.valueOf(invoiceId))) {
                view.invoiceTable.setRowSelectionInterval(i, i);
                showSelectedInvoiceDetails();
                return;
            }
        }
        showSelectedInvoiceDetails();
    }
}
