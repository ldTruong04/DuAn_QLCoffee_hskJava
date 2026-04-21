package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.controller.PhienUngDung;
import coffee.view.screens.ManHinhDangNhap;

import javax.swing.*;

public class DieuKhienDangNhap {
    public DieuKhienDangNhap(DieuKhienUngDung appController, PhienUngDung session, ManHinhDangNhap view, Runnable onLoginSuccess) {
        view.loginButton.addActionListener(e -> {
            String username = view.usernameField.getText().trim();
            String password = new String(view.passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
                return;
            }
            appController.login(username, password).ifPresentOrElse(user -> {
                session.setCurrentUser(user);
                onLoginSuccess.run();
            }, () -> JOptionPane.showMessageDialog(view, "Sai tài khoản hoặc mật khẩu"));
        });
    }
}
