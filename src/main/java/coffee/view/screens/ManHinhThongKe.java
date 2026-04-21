package coffee.view.screens;

import coffee.util.*;
import javax.swing.*;
import java.awt.*;

public class ManHinhThongKe extends JPanel {
    public final JTextArea reportArea = new JTextArea();
    public final JButton refreshButton = new ModernButton("Làm mới thống kê", ModernUITheme.PRIMARY_COLOR, ModernUITheme.TEXT_PRIMARY);

    public ManHinhThongKe() {
        setLayout(new BorderLayout(ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG));
        setBackground(ModernUITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG,
                                                    ModernUITheme.PADDING_LG, ModernUITheme.PADDING_LG));

        // Report area styling
        reportArea.setEditable(false);
        reportArea.setBackground(ModernUITheme.BG_PRIMARY);
        reportArea.setForeground(ModernUITheme.TEXT_PRIMARY);
        reportArea.setFont(ModernUITheme.FONT_BODY);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setMargin(new Insets(ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD,
                                        ModernUITheme.PADDING_MD, ModernUITheme.PADDING_MD));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(reportArea);
        ModernStyler.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ModernUITheme.BG_PRIMARY);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
