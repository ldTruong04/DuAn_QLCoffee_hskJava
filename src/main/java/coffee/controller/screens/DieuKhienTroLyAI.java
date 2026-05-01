package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.service.GeminiService;
import coffee.view.screens.ManHinhTroLyAI;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class DieuKhienTroLyAI {
    private final DieuKhienUngDung appController;
    private final ManHinhTroLyAI view;

    public DieuKhienTroLyAI(DieuKhienUngDung appController, ManHinhTroLyAI view) {
        this.appController = appController;
        this.view = view;
        bind();
    }

    private void bind() {
        view.sendButton.addActionListener(e -> sendQuery(view.userQueryArea.getText().trim()));
        view.clearButton.addActionListener(e -> {
            view.userQueryArea.setText("");
            view.responseArea.setText("");
            view.statusLabel.setText("Trợ lý AI sẵn sàng. Hãy hỏi hoặc dùng nút nhanh.");
        });

        view.revenueButton.addActionListener(e -> sendQuery("Cho tôi biết doanh thu hiện tại của quán"));
        view.productButton.addActionListener(e -> sendQuery("Liệt kê các sản phẩm đang có"));
        view.orderButton.addActionListener(e -> sendQuery("Hướng dẫn tạo đơn hàng mới"));
    }

    private void sendQuery(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập câu hỏi hoặc lệnh.");
            return;
        }
        view.statusLabel.setText("Đang gửi yêu cầu đến trợ lý AI...");
        view.responseArea.setText("");
        view.sendButton.setEnabled(false);
        view.revenueButton.setEnabled(false);
        view.productButton.setEnabled(false);
        view.orderButton.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String rawResponse = GeminiService.askGemini(prompt);
                return interpretResponse(rawResponse, prompt);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    view.responseArea.setText(response);
                    view.statusLabel.setText("Hoàn tất. Bạn có thể hỏi tiếp hoặc dùng lệnh nhanh.");
                } catch (Exception ex) {
                    view.responseArea.setText("Lỗi khi gọi trợ lý AI: " + ex.getMessage());
                    view.statusLabel.setText("Có lỗi xảy ra. Vui lòng thử lại.");
                } finally {
                    view.sendButton.setEnabled(true);
                    view.revenueButton.setEnabled(true);
                    view.productButton.setEnabled(true);
                    view.orderButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private String interpretResponse(String rawResponse, String prompt) {
        String normalized = rawResponse == null ? "" : rawResponse.trim();
        if (normalized.isBlank()) {
            return "Trợ lý AI không trả về dữ liệu. Vui lòng thử lại hoặc kiểm tra cấu hình API.";
        }

        if (prompt.toLowerCase().contains("doanh thu") || normalized.contains("GET_REVENUE")) {
            return "Doanh thu hiện tại: " + String.format("%.0f", appController.getRevenue()) + " VND";
        }

        if (prompt.toLowerCase().contains("sản phẩm") || normalized.contains("GET_PRODUCTS")) {
            return formatProducts(appController.getProducts());
        }

        if (prompt.toLowerCase().contains("bàn") || normalized.contains("GET_TABLES")) {
            return formatTables(appController.getTables());
        }

        if (prompt.toLowerCase().contains("tạo đơn") || normalized.contains("CREATE_ORDER")) {
            return "Trợ lý AI có thể hướng dẫn bạn tạo đơn: hãy mở màn hình Thanh toán, chọn bàn, chọn món và nhấn Thanh toán.";
        }

        return normalized;
    }

    private String formatProducts(List<coffee.model.SanPham> products) {
        if (products == null || products.isEmpty()) {
            return "Không tìm thấy sản phẩm nào.";
        }
        return products.stream()
                .limit(10)
                .map(p -> String.format("- %s | %s | %.0f VND", p.getTen(), p.getDanhMuc(), p.getGia()))
                .collect(Collectors.joining("\n"));
    }

    private String formatTables(List<coffee.model.BanCafe> tables) {
        if (tables == null || tables.isEmpty()) {
            return "Không có bàn nào.";
        }
        return tables.stream()
                .map(b -> String.format("- %s (ID %d) → %s", b.getTen(), b.getMa(), b.isDaDat() ? "Đã đặt" : b.isDangSuDung() ? "Đang sử dụng" : "Trống"))
                .collect(Collectors.joining("\n"));
    }
}
