package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.model.SanPham;
import coffee.model.MonOrderBep;
import coffee.util.PDFUtil;
import coffee.view.screens.ManHinhSanPham;

import javax.swing.*;

public class DieuKhienSanPham {
    private final DieuKhienUngDung appController;
    private final ManHinhSanPham view;

    public DieuKhienSanPham(DieuKhienUngDung appController, ManHinhSanPham view) {
        this.appController = appController;
        this.view = view;
        bind();
    }

    private void bind() {
        view.addButton.addActionListener(e -> runAction(() ->
                appController.addProduct(
                        view.nameField.getText().trim(),
                        view.categoryField.getText().trim(),
                        parseDouble(view.priceField.getText()),
                        view.descriptionArea.getText().trim(),
                        normalizePath(view.imagePathField.getText())
                )
        ));
        view.updateButton.addActionListener(e -> runAction(() ->
                appController.updateProduct(
                        parseInt(view.idField.getText()),
                        view.nameField.getText().trim(),
                        view.categoryField.getText().trim(),
                        parseDouble(view.priceField.getText()),
                        view.descriptionArea.getText().trim(),
                        normalizePath(view.imagePathField.getText())
                )
        ));
        view.deleteButton.addActionListener(e -> runAction(() ->
                appController.deleteProduct(parseInt(view.idField.getText()))
        ));
        view.browseImageButton.addActionListener(e -> chooseImageFile());
        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.table.getSelectedRow() >= 0) {
                int row = view.table.getSelectedRow();
                String formattedId = view.tableModel.getValueAt(row, 0).toString();
                view.idField.setText(formattedId.replaceAll("[^0-9]", ""));
                view.nameField.setText(view.tableModel.getValueAt(row, 1).toString());
                view.categoryField.setText(view.tableModel.getValueAt(row, 2).toString());
                view.priceField.setText(view.tableModel.getValueAt(row, 3).toString());
                view.descriptionArea.setText(view.tableModel.getValueAt(row, 4).toString());
                view.capNhatXemTruocAnh(view.tableModel.getValueAt(row, 5).toString());
            }
        });

        // Bếp
        view.completeOrderButton.addActionListener(e -> runAction(() -> {
            int row = view.kitchenOrderTable.getSelectedRow();
            if (row < 0) {
                throw new IllegalArgumentException("Vui lòng chọn món để hoàn thành");
            }
            
            // Get the item directly from the appController list since order matters
            java.util.List<MonOrderBep> items = appController.getPendingOrderItems();
            MonOrderBep item = items.get(row);
            
            appController.updateKitchenOrderItemStatus(item.getInvoiceId(), item.getProductId(), "COMPLETED");
            PDFUtil.inDonHang(item);
            JOptionPane.showMessageDialog(view, "Đã hoàn thành món và in PDF thành công!");
        }));

        view.cancelOrderButton.addActionListener(e -> runAction(() -> {
            int row = view.kitchenOrderTable.getSelectedRow();
            if (row < 0) {
                throw new IllegalArgumentException("Vui lòng chọn món để hủy");
            }
            java.util.List<MonOrderBep> items = appController.getPendingOrderItems();
            MonOrderBep item = items.get(row);
            
            appController.removeInvoiceItem(item.getInvoiceId(), item.getProductId());
            JOptionPane.showMessageDialog(view, "Đã hủy món và gỡ khỏi hóa đơn thành công!");
        }));

        view.refreshKitchenOrdersButton.addActionListener(e -> runAction(this::refresh));
    }

    public void refresh() {
        view.tableModel.setRowCount(0);
        for (SanPham p : appController.getProducts()) {
            view.tableModel.addRow(new Object[]{
                    String.format("SP%03d", p.getMa()),
                    p.getTen(),
                    p.getDanhMuc(),
                    String.format("%.0f", p.getGia()),
                    p.getMoTa() == null ? "" : p.getMoTa(),
                    p.getDuongDanHinhAnh() == null ? "" : p.getDuongDanHinhAnh()
            });
        }
        
        view.kitchenOrderTableModel.setRowCount(0);
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd/MM");
        java.util.List<MonOrderBep> pendingItems = appController.getPendingOrderItems();
        for (MonOrderBep item : pendingItems) {
            view.kitchenOrderTableModel.addRow(new Object[]{
                    String.format("HD%03d", item.getInvoiceId()),
                    item.getThoiGian().format(dtf),
                    item.getTenBan(),
                    item.getTenMon(),
                    item.getSoLuong(),
                    "Chờ chế biến"
            });
        }
        
        int pendingCount = pendingItems.size();
        if (pendingCount > 0) {
            view.tabbedPane.setTitleAt(1, "Đơn hàng Bếp (" + pendingCount + ")");
        } else {
            view.tabbedPane.setTitleAt(1, "Đơn hàng Bếp");
        }
    }

    private void chooseImageFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn hình ảnh sản phẩm");
        int result = chooser.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            view.capNhatXemTruocAnh(chooser.getSelectedFile().getAbsolutePath());
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

    private int parseInt(String value) {
        return Integer.parseInt(value.trim());
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    private String normalizePath(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
