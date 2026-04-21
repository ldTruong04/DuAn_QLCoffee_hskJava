package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.model.HoaDon;
import coffee.model.SanPham;
import coffee.view.screens.ManHinhThongKe;

public class DieuKhienThongKe {
    private final DieuKhienUngDung appController;
    private final ManHinhThongKe view;

    public DieuKhienThongKe(DieuKhienUngDung appController, ManHinhThongKe view) {
        this.appController = appController;
        this.view = view;
        this.view.refreshButton.addActionListener(e -> refresh());
    }

    public void refresh() {
        StringBuilder sb = new StringBuilder();
        sb.append("DOANH THU: ").append(String.format("%.0f", appController.getRevenue())).append(" VND\n\n");
        sb.append("TOP SAN PHAM BAN CHAY\n");
        for (SanPham product : appController.getTopProducts(5)) {
            sb.append("- ").append(product.getTen()).append(": ").append(product.getSoLuongDaBan()).append(" ly\n");
        }
        sb.append("\nHOA DON DA THANH TOAN\n");
        for (HoaDon invoice : appController.getInvoices()) {
            if (invoice.isDaThanhToan()) {
                sb.append("- HD#").append(invoice.getMa())
                        .append(" | Ban ").append(invoice.getBan().getTen())
                        .append(" | Tong ").append(String.format("%.0f", invoice.getTongTien()))
                        .append(" VND\n");
            }
        }
        view.reportArea.setText(sb.toString());
    }
}
