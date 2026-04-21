package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.model.SanPham;
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
                view.idField.setText(view.tableModel.getValueAt(row, 0).toString());
                view.nameField.setText(view.tableModel.getValueAt(row, 1).toString());
                view.categoryField.setText(view.tableModel.getValueAt(row, 2).toString());
                view.priceField.setText(view.tableModel.getValueAt(row, 3).toString());
                view.descriptionArea.setText(view.tableModel.getValueAt(row, 4).toString());
                view.capNhatXemTruocAnh(view.tableModel.getValueAt(row, 5).toString());
            }
        });
    }

    public void refresh() {
        view.tableModel.setRowCount(0);
        for (SanPham p : appController.getProducts()) {
            view.tableModel.addRow(new Object[]{
                    p.getMa(),
                    p.getTen(),
                    p.getDanhMuc(),
                    String.format("%.0f", p.getGia()),
                    p.getMoTa() == null ? "" : p.getMoTa(),
                    p.getDuongDanHinhAnh() == null ? "" : p.getDuongDanHinhAnh()
            });
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
