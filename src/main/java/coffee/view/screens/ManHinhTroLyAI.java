package coffee.view.screens;

import coffee.util.ModernButton;
import coffee.util.ModernTextField;
import coffee.util.ModernUITheme;

import javax.swing.*;
import java.awt.*;

public class ManHinhTroLyAI extends JPanel {
    public final JTextArea userQueryArea = new JTextArea(3, 40);
    public final JButton sendButton = new ModernButton("Gửi", ModernUITheme.PRIMARY_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton clearButton = new ModernButton("Xóa", ModernUITheme.BG_TERTIARY, ModernUITheme.TEXT_PRIMARY);
    public final JButton revenueButton = new ModernButton("Doanh thu", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton productButton = new ModernButton("Sản phẩm", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JButton orderButton = new ModernButton("Tạo đơn", ModernUITheme.INFO_COLOR, ModernUITheme.TEXT_PRIMARY);
    public final JTextArea responseArea = new JTextArea(12, 40);
    public final JLabel statusLabel = new JLabel("Trợ lý AI sẵn sàng. Hãy hỏi hoặc dùng nút nhanh.");

    public ManHinhTroLyAI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("TRỢ LÝ AI");
        title.setFont(ModernUITheme.FONT_HEADING);
        title.setForeground(ModernUITheme.PRIMARY_DARK);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        JPanel queryPanel = new JPanel(new BorderLayout(0, 10));
        queryPanel.setOpaque(false);
        queryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel queryLabel = new JLabel("Nhập câu hỏi hoặc lệnh:");
        queryLabel.setFont(ModernUITheme.FONT_SMALL);
        queryLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        queryPanel.add(queryLabel, BorderLayout.NORTH);

        userQueryArea.setLineWrap(true);
        userQueryArea.setWrapStyleWord(true);
        userQueryArea.setFont(ModernUITheme.FONT_BODY);
        JScrollPane queryScroll = new JScrollPane(userQueryArea);
        queryScroll.setBorder(BorderFactory.createEmptyBorder());
        queryPanel.add(queryScroll, BorderLayout.CENTER);

        JPanel queryButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        queryButtons.setOpaque(false);
        sendButton.setPreferredSize(new Dimension(100, 32));
        clearButton.setPreferredSize(new Dimension(90, 32));
        queryButtons.add(clearButton);
        queryButtons.add(sendButton);
        queryPanel.add(queryButtons, BorderLayout.SOUTH);

        center.add(queryPanel, BorderLayout.NORTH);

        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quickPanel.setOpaque(false);
        quickPanel.add(new JLabel("Lệnh nhanh:"));
        quickPanel.add(revenueButton);
        quickPanel.add(productButton);
        quickPanel.add(orderButton);
        center.add(quickPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel responsePanel = new JPanel(new BorderLayout(0, 10));
        responsePanel.setOpaque(false);
        responsePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel responseLabel = new JLabel("Phản hồi:");
        responseLabel.setFont(ModernUITheme.FONT_SMALL);
        responseLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        responsePanel.add(responseLabel, BorderLayout.NORTH);

        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setFont(ModernUITheme.FONT_BODY);
        JScrollPane responseScroll = new JScrollPane(responseArea);
        responseScroll.setBorder(BorderFactory.createEmptyBorder());
        responsePanel.add(responseScroll, BorderLayout.CENTER);

        statusLabel.setFont(ModernUITheme.FONT_SMALL);
        statusLabel.setForeground(ModernUITheme.TEXT_SECONDARY);
        responsePanel.add(statusLabel, BorderLayout.SOUTH);

        add(responsePanel, BorderLayout.SOUTH);
    }
}
