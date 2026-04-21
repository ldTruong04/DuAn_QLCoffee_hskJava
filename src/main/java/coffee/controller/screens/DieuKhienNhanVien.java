package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.model.GioiTinh;
import coffee.model.NhanVien;
import coffee.model.VaiTro;
import coffee.view.screens.ManHinhNhanVien;

import javax.swing.*;
import java.io.File;

public class DieuKhienNhanVien {
    private final DieuKhienUngDung appController;
    private final PhienUngDung session;
    private final ManHinhNhanVien view;

    public DieuKhienNhanVien(DieuKhienUngDung appController, PhienUngDung session, ManHinhNhanVien view) {
        this.appController = appController;
        this.session = session;
        this.view = view;
        bind();
    }

    private void bind() {
        view.chooseAvatarButton.addActionListener(e -> chooseAvatar());
        view.addButton.addActionListener(e -> runAdminAction(() ->
                appController.addEmployee(
                view.fullNameField.getText().trim(),
                parseInt(view.birthYearField.getText()),
                parseDouble(view.salaryField.getText()),
            view.layGioiTinhDaChon(),
                normalizePath(view.avatarPathField.getText()),
                        (VaiTro) view.roleBox.getSelectedItem(),
                        view.usernameField.getText().trim(),
                new String(view.passwordField.getPassword()))
        ));
        view.updateButton.addActionListener(e -> runAdminAction(() ->
                appController.updateEmployee(
                        parseInt(view.idField.getText()),
                view.fullNameField.getText().trim(),
                parseInt(view.birthYearField.getText()),
                parseDouble(view.salaryField.getText()),
                view.layGioiTinhDaChon(),
                normalizePath(view.avatarPathField.getText()),
                        (VaiTro) view.roleBox.getSelectedItem(),
                        view.usernameField.getText().trim(),
                new String(view.passwordField.getPassword()))
        ));
        view.deleteButton.addActionListener(e -> runAdminAction(() ->
                appController.deleteEmployee(parseInt(view.idField.getText()))
        ));
        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.table.getSelectedRow() >= 0) {
                int row = view.table.getSelectedRow();
                view.idField.setText(view.tableModel.getValueAt(row, 0).toString());
            view.fullNameField.setText(view.tableModel.getValueAt(row, 1).toString());
            view.birthYearField.setText(view.tableModel.getValueAt(row, 2).toString());
            view.salaryField.setText(view.tableModel.getValueAt(row, 3).toString());
            view.datGioiTinh(GioiTinh.valueOf(view.tableModel.getValueAt(row, 4).toString()));
            view.roleBox.setSelectedItem(VaiTro.valueOf(view.tableModel.getValueAt(row, 5).toString()));
            view.usernameField.setText(view.tableModel.getValueAt(row, 6).toString());
            view.capNhatXemTruocAnh(view.tableModel.getValueAt(row, 7).toString());
            }
        });
    }

    public void refresh() {
        view.tableModel.setRowCount(0);
        for (NhanVien e : appController.getEmployees()) {
            view.tableModel.addRow(new Object[]{
                e.getMa(),
                e.getHoTen(),
                e.getNamSinh(),
                String.format("%.0f", e.getLuong()),
                e.getGioiTinh().name(),
                e.getVaiTro().name(),
                e.getTenDangNhap(),
                e.getAnhDaiDien() == null ? "" : e.getAnhDaiDien()
            });
        }
    }

    private void runAdminAction(Runnable action) {
        if (session.getCurrentUser() == null || session.getCurrentUser().getVaiTro() != VaiTro.ADMIN) {
            JOptionPane.showMessageDialog(view, "Chỉ ADMIN mới có quyền quản lý nhân viên");
            return;
        }
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

    private void chooseAvatar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn ảnh đại diện");
        int result = chooser.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            view.capNhatXemTruocAnh(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private String normalizePath(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
