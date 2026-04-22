package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.model.KhuyenMai;
import coffee.view.screens.ManHinhKhuyenMai;

import javax.swing.*;

public class DieuKhienKhuyenMai {
    private final DieuKhienUngDung appController;
    private final ManHinhKhuyenMai view;

    public DieuKhienKhuyenMai(DieuKhienUngDung appController, ManHinhKhuyenMai view) {
        this.appController = appController;
        this.view = view;
        bind();
    }

    private void bind() {
        view.addButton.addActionListener(e -> runAction(() ->
                appController.addPromotion(
                        view.codeField.getText().trim(),
                        view.loaiBox.getSelectedIndex() == 0,
                        parseDouble(view.giaTriField.getText()),
                        view.activeBox.isSelected()
                )
        ));

        view.updateButton.addActionListener(e -> runAction(() ->
                appController.updatePromotion(
                        parseInt(view.idField.getText()),
                        view.codeField.getText().trim(),
                        view.loaiBox.getSelectedIndex() == 0,
                        parseDouble(view.giaTriField.getText()),
                        view.activeBox.isSelected()
                )
        ));

        view.deleteButton.addActionListener(e -> runAction(() ->
                appController.deletePromotion(parseInt(view.idField.getText()))
        ));

        view.clearButton.addActionListener(e -> view.clearForm());

        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.table.getSelectedRow() >= 0) {
                int row = view.table.getSelectedRow();
                view.idField.setText(view.tableModel.getValueAt(row, 0).toString());
                view.codeField.setText(view.tableModel.getValueAt(row, 1).toString());
                String loai = view.tableModel.getValueAt(row, 2).toString();
                view.loaiBox.setSelectedIndex("Phần trăm".equals(loai) ? 0 : 1);
                String valueText = view.tableModel.getValueAt(row, 3).toString().replace("%", "").replace("VND", "").trim();
                view.giaTriField.setText(valueText);
                view.activeBox.setSelected("Đang áp dụng".equals(view.tableModel.getValueAt(row, 4).toString()));
            }
        });
    }

    public void refresh() {
        view.tableModel.setRowCount(0);
        for (KhuyenMai km : appController.getPromotions()) {
            view.tableModel.addRow(new Object[]{
                    km.getMa(),
                    km.getMaCode(),
                    km.getLoaiHienThi(),
                    km.getGiaTriHienThi(),
                    km.isKichHoat() ? "Đang áp dụng" : "Tạm dừng"
            });
        }
    }

    private void runAction(Runnable action) {
        try {
            action.run();
            refresh();
            view.clearForm();
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
}
