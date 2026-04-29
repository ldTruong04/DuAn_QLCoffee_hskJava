package coffee.view.screens;

import coffee.util.*;
import javax.swing.*;
import java.awt.*;

/**
 * Modern Login Screen with dark theme and rounded components
 */
public class ManHinhDangNhap extends JPanel {
    public final JTextField usernameField;
    public final JPasswordField passwordField;
    public final JButton loginButton;
    public final JButton forgotPasswordButton;

    public ManHinhDangNhap() {
        // Initialize modern components
        usernameField = new ModernTextField(20);
        passwordField = new ModernPasswordField(20);
        loginButton = new ModernButton("Đăng nhập", ModernUITheme.PRIMARY_COLOR, ModernUITheme.TEXT_PRIMARY);
        
        forgotPasswordButton = new ModernButton("Quên mật khẩu?", ModernUITheme.BG_SECONDARY, ModernUITheme.TEXT_SECONDARY);

        setBackground(ModernUITheme.BG_PRIMARY);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ModernUITheme.BG_PRIMARY);
        card.setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_XXL, ModernUITheme.PADDING_XXL,
            ModernUITheme.PADDING_XXL, ModernUITheme.PADDING_XXL));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG, 
                                ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Create title
        JLabel titleLabel = new JLabel("☕ QL Coffee");
        titleLabel.setFont(ModernUITheme.FONT_TITLE);
        titleLabel.setForeground(ModernUITheme.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_XXL, 0);
        card.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        JLabel subtitleLabel = new JLabel("Đăng nhập để quản lý bán hàng, bàn, hóa đơn và báo cáo.");
        subtitleLabel.setFont(ModernUITheme.FONT_SMALL);
        subtitleLabel.setForeground(ModernUITheme.TEXT_TERTIARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_LG, 0);
        card.add(subtitleLabel, gbc);

        // Username label
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setFont(ModernUITheme.FONT_SUBHEADING);
        usernameLabel.setForeground(ModernUITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_SM, 0);
        card.add(usernameLabel, gbc);

        // Username field
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_LG, 0);
        card.add(usernameField, gbc);

        // Password label
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(ModernUITheme.FONT_SUBHEADING);
        passwordLabel.setForeground(ModernUITheme.TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_SM, 0);
        card.add(passwordLabel, gbc);

        // Password field
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_LG, 0);
        card.add(passwordField, gbc);

        // Login button
        gbc.gridy = 6;
        gbc.insets = new Insets(ModernUITheme.PADDING_XL, 0, ModernUITheme.PADDING_XL, 0);
        card.add(loginButton, gbc);

        // Forgot password button
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, ModernUITheme.PADDING_XL, 0);
        card.add(forgotPasswordButton, gbc);

        // Demo credentials info
        JLabel demoLabel = new JLabel("<html><center>Demo: admin/admin123<br/>hoặc staff/staff123</center></html>");
        demoLabel.setFont(ModernUITheme.FONT_SMALL);
        demoLabel.setForeground(ModernUITheme.TEXT_TERTIARY);
        gbc.gridy = 8;
        gbc.insets = new Insets(ModernUITheme.PADDING_MD, 0, 0, 0);
        card.add(demoLabel, gbc);

        add(card);
    }
}
