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
                new String(view.passwordField.getPassword()),
                view.emailField.getText().trim()),
            "Thêm nhân viên thành công"
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
                        new String(view.passwordField.getPassword()),
                        view.emailField.getText().trim()),
                "Cập nhật nhân viên thành công"
        ));
        view.deleteButton.addActionListener(e -> runAdminAction(() ->
                appController.deleteEmployee(parseInt(view.idField.getText())),
                "Xóa nhân viên thành công"
        ));
        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.table.getSelectedRow() >= 0) {
                int row = view.table.getSelectedRow();
                String formattedId = view.tableModel.getValueAt(row, 0).toString();
                int employeeId = Integer.parseInt(formattedId.replaceAll("[^0-9]", ""));
                view.idField.setText(String.valueOf(employeeId));
                view.fullNameField.setText(view.tableModel.getValueAt(row, 1).toString());
                view.birthYearField.setText(view.tableModel.getValueAt(row, 2).toString());
                view.salaryField.setText(view.tableModel.getValueAt(row, 3).toString());
                view.datGioiTinh(GioiTinh.valueOf(view.tableModel.getValueAt(row, 4).toString()));
                view.roleBox.setSelectedItem(VaiTro.valueOf(view.tableModel.getValueAt(row, 5).toString()));
                view.usernameField.setText(view.tableModel.getValueAt(row, 6).toString());
                view.emailField.setText(view.tableModel.getValueAt(row, 7).toString());
                view.capNhatXemTruocAnh(view.tableModel.getValueAt(row, 8).toString());

                NhanVien selected = appController.getEmployees().stream()
                        .filter(emp -> emp.getMa() == employeeId)
                        .findFirst()
                        .orElse(null);
                if (selected != null) {
                    view.passwordField.setText(selected.getMatKhau() == null ? "" : selected.getMatKhau());
                } else {
                    view.passwordField.setText("");
                }
            }
        });
    }

    public void refresh() {
        view.tableModel.setRowCount(0);
        for (NhanVien e : appController.getEmployees()) {
            view.tableModel.addRow(new Object[]{
                String.format("NV%03d", e.getMa()),
                e.getHoTen(),
                e.getNamSinh(),
                String.format("%.0f", e.getLuong()),
                e.getGioiTinh().name(),
                e.getVaiTro().name(),
                e.getTenDangNhap(),
                e.getEmail() == null ? "" : e.getEmail(),
                e.getAnhDaiDien() == null ? "" : e.getAnhDaiDien()
            });
        }
    }

    private void runAdminAction(Runnable action, String successMessage) {
        if (session.getCurrentUser() == null || session.getCurrentUser().getVaiTro() != VaiTro.ADMIN) {
            JOptionPane.showMessageDialog(view, "Chỉ ADMIN mới có quyền quản lý nhân viên");
            return;
        }
        try {
            action.run();
            refresh();
            JOptionPane.showMessageDialog(view, successMessage);
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
