package coffee.view.screens;

import coffee.model.GioiTinh;
import coffee.model.VaiTro;
import coffee.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ManHinhNhanVien extends JPanel {
    // GIỮ NGUYÊN TẤT CẢ CÁC BIẾN CŨ
    public final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Mã", "Họ tên", "Năm sinh", "Lương", "Giới tính", "Vai trò", "Username", "Email", "Ảnh"}, 0);
    public final JTable table = new JTable(tableModel);
    public final JTextField idField = new ModernTextField(15);
    public final JTextField fullNameField = new ModernTextField(15);
    public final JTextField emailField = new ModernTextField(15);
    public final JTextField birthYearField = new ModernTextField(15);
    public final JTextField salaryField = new ModernTextField(15);
    public final JRadioButton maleRadio = new JRadioButton("Nam");
    public final JRadioButton femaleRadio = new JRadioButton("Nữ");
    public final JRadioButton otherRadio = new JRadioButton("Khác");
    private final ButtonGroup genderGroup = new ButtonGroup();
    public final JComboBox<VaiTro> roleBox = new JComboBox<>(VaiTro.values());
    public final JTextField usernameField = new ModernTextField(15);
    public final JPasswordField passwordField = new ModernPasswordField(15);
    public final JTextField avatarPathField = new ModernTextField(15);
    public final JButton chooseAvatarButton = new ModernButton("Chọn ảnh", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JLabel avatarPreviewLabel = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
    public final JButton addButton = new ModernButton("Thêm", ModernUITheme.SUCCESS_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton updateButton = new ModernButton("Sửa", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton deleteButton = new ModernButton("Xóa", ModernUITheme.DANGER_COLOR, ModernUITheme.TEXT_PRIMARY);

    public ManHinhNhanVien() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(242, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildSplitCard(), BorderLayout.CENTER);
    }

    private JPanel buildSplitCard() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildDetailCard(), buildTableCard());
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);
        splitPane.setDividerSize(6);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(splitPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildDetailCard() {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 233, 237), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("THÔNG TIN CHI TIẾT NHÂN VIÊN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(52, 73, 94));
        card.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Cột ảnh bên trái
        JPanel avatarBox = new JPanel(new BorderLayout(5, 5));
        avatarBox.setOpaque(false);
        avatarPreviewLabel.setPreferredSize(new Dimension(150, 150));
        avatarPreviewLabel.setBorder(new LineBorder(new Color(240, 240, 240), 2));
        avatarBox.add(avatarPreviewLabel, BorderLayout.CENTER);
        avatarBox.add(chooseAvatarButton, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.weightx = 0.4;
        content.add(avatarBox, gbc);

        // Cột thông tin bên phải
        JPanel details = new JPanel(new GridLayout(4, 2, 25, 12));
        details.setOpaque(false);

        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        otherRadio.setSelected(true);
        styleGenderRadio(maleRadio);
        styleGenderRadio(femaleRadio);
        styleGenderRadio(otherRadio);

        idField.setVisible(false);
        addInput(details, "Họ và tên", fullNameField);
        addInput(details, "Email", emailField);
        addInput(details, "Năm sinh", birthYearField);
        addInput(details, "Lương cơ bản", salaryField);
        addInput(details, "Giới tính", buildGenderPanel());
        addInput(details, "Vị trí công việc", roleBox);
        addInput(details, "Tên đăng nhập", usernameField);
        addInput(details, "Mật khẩu", passwordField);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        content.add(details, gbc);

        card.add(content, BorderLayout.CENTER);

        // Nút hành động
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setOpaque(false);
        addButton.setPreferredSize(new Dimension(110, 38));
        updateButton.setPreferredSize(new Dimension(110, 38));
        deleteButton.setPreferredSize(new Dimension(110, 38));
        footer.add(addButton);
        footer.add(updateButton);
        footer.add(deleteButton);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 233, 237), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("DANH SÁCH NHÂN VIÊN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(127, 140, 141));
        card.add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        ModernStyler.styleScrollPane(scrollPane);
        ModernStyler.styleTable(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void addInput(JPanel p, String label, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(100, 100, 100));
        group.add(l, BorderLayout.NORTH);
        
        // Style cho ComboBox nếu cần
        if (field instanceof JComboBox) {
            field.setBackground(Color.WHITE);
        }
        
        group.add(field, BorderLayout.CENTER);
        p.add(group);
    }

    private JPanel buildGenderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setOpaque(false);
        panel.add(maleRadio);
        panel.add(femaleRadio);
        panel.add(otherRadio);
        return panel;
    }

    private void styleGenderRadio(JRadioButton radioButton) {
        radioButton.setOpaque(false);
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radioButton.setForeground(new Color(70, 70, 70));
    }

    public GioiTinh layGioiTinhDaChon() {
        if (maleRadio.isSelected()) {
            return GioiTinh.NAM;
        }
        if (femaleRadio.isSelected()) {
            return GioiTinh.NU;
        }
        return GioiTinh.KHAC;
    }

    public void datGioiTinh(GioiTinh gioiTinh) {
        if (gioiTinh == GioiTinh.NAM) {
            maleRadio.setSelected(true);
            return;
        }
        if (gioiTinh == GioiTinh.NU) {
            femaleRadio.setSelected(true);
            return;
        }
        otherRadio.setSelected(true);
    }

    public void capNhatXemTruocAnh(String duongDan) {
        avatarPathField.setText(duongDan == null ? "" : duongDan);
        if (duongDan == null || duongDan.isBlank()) {
            avatarPreviewLabel.setIcon(null);
            avatarPreviewLabel.setText("Chưa có ảnh");
            return;
        }
        File file = new File(duongDan);
        if (!file.exists()) return;

        ImageIcon icon = new ImageIcon(duongDan);
        Image scaled = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        avatarPreviewLabel.setIcon(new ImageIcon(scaled));
        avatarPreviewLabel.setText("");
    }
}