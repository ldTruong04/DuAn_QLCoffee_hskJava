package coffee.controller.screens;

import coffee.controller.DieuKhienUngDung;
import coffee.model.HoaDon;
import coffee.model.SanPham;
import coffee.view.screens.ManHinhThongKe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class DieuKhienThongKe {
    private final DieuKhienUngDung appController;
    private final ManHinhThongKe view;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DieuKhienThongKe(DieuKhienUngDung appController, ManHinhThongKe view) {
        this.appController = appController;
        this.view = view;
        this.view.refreshButton.addActionListener(e -> refresh());
    }

    public void refresh() {
        List<HoaDon> invoices = appController.getInvoices();
        List<HoaDon> paidInvoices = invoices.stream()
                .filter(HoaDon::isDaThanhToan)
                .toList();

        double revenue = appController.getRevenue();
        double averagePerInvoice = paidInvoices.isEmpty() ? 0 : revenue / paidInvoices.size();

        view.doanhThuValueLabel.setText(String.format("%.0f VND", revenue));
        view.soHoaDonValueLabel.setText(String.valueOf(invoices.size()));
        view.hoaDonDaThanhToanValueLabel.setText(String.valueOf(paidInvoices.size()));
        view.giaTriTrungBinhValueLabel.setText(String.format("%.0f VND", averagePerInvoice));
        view.capNhatLanCuoiLabel.setText("Cập nhật: " + LocalDateTime.now().format(timeFormatter));

        refreshTopProducts();
        refreshRecentInvoices(invoices);
        refreshRevenueCharts(paidInvoices);
    }

    private void refreshTopProducts() {
        view.topSanPhamTableModel.setRowCount(0);
        for (SanPham product : appController.getTopProducts(7)) {
            double estimatedRevenue = product.getSoLuongDaBan() * product.getGia();
            view.topSanPhamTableModel.addRow(new Object[]{
                    product.getTen(),
                    product.getDanhMuc(),
                    product.getSoLuongDaBan(),
                    String.format("%.0f", estimatedRevenue)
            });
        }
    }

    private void refreshRecentInvoices(List<HoaDon> invoices) {
        view.hoaDonGanDayTableModel.setRowCount(0);
        invoices.stream()
                .sorted(Comparator.comparing(HoaDon::getThoiGianTao).reversed())
                .limit(20)
                .forEach(invoice -> view.hoaDonGanDayTableModel.addRow(new Object[]{
                        invoice.getMa(),
                        invoice.getBan().getTen(),
                        invoice.getNhanVien().getHoTen(),
                        String.format("%.0f", invoice.getTongTien()),
                        invoice.getThoiGianTao().format(timeFormatter),
                        invoice.isDaThanhToan() ? "Đã thanh toán" : "Chưa thanh toán"
                }));
    }

    private void refreshRevenueCharts(List<HoaDon> paidInvoices) {
        RevenueSplit day = buildRevenueByDay(paidInvoices);
        view.bieuDoTheoNgayPanel.setStackedChartData(day.cashData(), day.transferData(), "Tiền mặt", "Chuyển khoản");

        RevenueSplit month = buildRevenueByMonth(paidInvoices);
        view.bieuDoTheoThangPanel.setStackedChartData(month.cashData(), month.transferData(), "Tiền mặt", "Chuyển khoản");

        RevenueSplit shift = buildRevenueByShift(paidInvoices);
        view.bieuDoTheoCaPanel.setStackedChartData(shift.cashData(), shift.transferData(), "Tiền mặt", "Chuyển khoản");
    }

    private RevenueSplit buildRevenueByDay(List<HoaDon> paidInvoices) {
        LinkedHashMap<String, Double> cashData = new LinkedHashMap<>();
        LinkedHashMap<String, Double> transferData = new LinkedHashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            String key = day.format(dayFormatter);
            cashData.put(key, 0.0);
            transferData.put(key, 0.0);
        }

        for (HoaDon invoice : paidInvoices) {
            LocalDate day = invoice.getThoiGianTao().toLocalDate();
            String key = day.format(dayFormatter);
            if (cashData.containsKey(key)) {
                addByPaymentMethod(invoice, key, cashData, transferData);
            }
        }
        return new RevenueSplit(cashData, transferData);
    }

    private RevenueSplit buildRevenueByMonth(List<HoaDon> paidInvoices) {
        LinkedHashMap<String, Double> cashData = new LinkedHashMap<>();
        LinkedHashMap<String, Double> transferData = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth current = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            String key = month.format(monthFormatter);
            cashData.put(key, 0.0);
            transferData.put(key, 0.0);
        }

        for (HoaDon invoice : paidInvoices) {
            YearMonth month = YearMonth.from(invoice.getThoiGianTao());
            String key = month.format(monthFormatter);
            if (cashData.containsKey(key)) {
                addByPaymentMethod(invoice, key, cashData, transferData);
            }
        }
        return new RevenueSplit(cashData, transferData);
    }

    private RevenueSplit buildRevenueByShift(List<HoaDon> paidInvoices) {
        LinkedHashMap<String, Double> cashData = new LinkedHashMap<>();
        LinkedHashMap<String, Double> transferData = new LinkedHashMap<>();
        cashData.put("Ca sáng", 0.0);
        cashData.put("Ca chiều", 0.0);
        cashData.put("Ca tối", 0.0);
        transferData.put("Ca sáng", 0.0);
        transferData.put("Ca chiều", 0.0);
        transferData.put("Ca tối", 0.0);

        for (HoaDon invoice : paidInvoices) {
            int hour = invoice.getThoiGianTao().getHour();
            if (hour < 12) {
                addByPaymentMethod(invoice, "Ca sáng", cashData, transferData);
            } else if (hour < 18) {
                addByPaymentMethod(invoice, "Ca chiều", cashData, transferData);
            } else {
                addByPaymentMethod(invoice, "Ca tối", cashData, transferData);
            }
        }
        return new RevenueSplit(cashData, transferData);
    }

    private void addByPaymentMethod(HoaDon invoice,
                                    String key,
                                    LinkedHashMap<String, Double> cashData,
                                    LinkedHashMap<String, Double> transferData) {
        if ("CHUYEN_KHOAN".equalsIgnoreCase(invoice.getPhuongThucThanhToan())) {
            transferData.put(key, transferData.get(key) + invoice.getTongTien());
        } else {
            cashData.put(key, cashData.get(key) + invoice.getTongTien());
        }
    }

    private record RevenueSplit(LinkedHashMap<String, Double> cashData,
                                LinkedHashMap<String, Double> transferData) {
    }
}
