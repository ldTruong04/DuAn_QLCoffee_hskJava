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

        view.forgotPasswordButton.addActionListener(e -> {
            String email = JOptionPane.showInputDialog(view, "Nhập email của bạn:");
            if (email == null || email.trim().isEmpty()) {
                return;
            }
            try {
                appController.generateAndSendOTP(email.trim());
                String otp = JOptionPane.showInputDialog(view, "Mã OTP đã được gửi. Nhập mã OTP (6 số):");
                if (otp == null || otp.trim().isEmpty()) {
                    return;
                }
                
                JPasswordField pwdField = new JPasswordField(15);
                Object[] message = {
                    "Nhập mật khẩu mới:", pwdField
                };
                int option = JOptionPane.showConfirmDialog(view, message, "Đổi mật khẩu", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String newPassword = new String(pwdField.getPassword());
                    if (newPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Mật khẩu không được để trống");
                        return;
                    }
                    appController.verifyOTPAndResetPassword(email.trim(), otp.trim(), newPassword);
                    JOptionPane.showMessageDialog(view, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
