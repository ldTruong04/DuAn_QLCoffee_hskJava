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
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class DieuKhienThongKe {
    private final DieuKhienUngDung appController;
    private final ManHinhThongKe view;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DieuKhienThongKe(DieuKhienUngDung appController, ManHinhThongKe view) {
        this.appController = appController;
        this.view = view;
        this.view.refreshButton.addActionListener(e -> refresh());
        this.view.applyFilterButton.addActionListener(e -> refresh());
    }

    public void refresh() {
        List<HoaDon> invoices = appController.getInvoices();
        LocalDate fromDate = parseDate(view.fromDateField.getText());
        LocalDate toDate = parseDate(view.toDateField.getText());
        LocalDate shiftDate = parseDate(view.shiftDateField.getText());

        if (fromDate == null || toDate == null || shiftDate == null) {
            return;
        }
        if (fromDate.isAfter(toDate)) {
            JOptionPane.showMessageDialog(view, "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<HoaDon> filteredInvoices = filterInvoicesByDateRange(invoices, fromDate, toDate);
        List<HoaDon> paidInvoices = filteredInvoices.stream()
                .filter(HoaDon::isDaThanhToan)
                .toList();

        double revenue = paidInvoices.stream().mapToDouble(HoaDon::getTongTien).sum();
        double averagePerInvoice = paidInvoices.isEmpty() ? 0 : revenue / paidInvoices.size();

        view.doanhThuValueLabel.setText(String.format("%.0f VND", revenue));
        view.soHoaDonValueLabel.setText(String.valueOf(filteredInvoices.size()));
        view.hoaDonDaThanhToanValueLabel.setText(String.valueOf(paidInvoices.size()));
        view.giaTriTrungBinhValueLabel.setText(String.format("%.0f VND", averagePerInvoice));
        view.capNhatLanCuoiLabel.setText("Cập nhật: " + LocalDateTime.now().format(timeFormatter));

        refreshTopProducts(filteredInvoices);
        refreshRecentInvoices(filteredInvoices);
        refreshRevenueCharts(paidInvoices, fromDate, toDate, shiftDate);
    }

    private void refreshTopProducts(List<HoaDon> invoices) {
        view.topSanPhamTableModel.setRowCount(0);
        var productStats = invoices.stream()
                .flatMap(invoice -> invoice.getDanhSachMon().stream())
                .collect(Collectors.toMap(
                        item -> item.getSanPham().getMa(),
                        item -> new ProductStat(item.getSanPham(), item.getSoLuong(), item.getThanhTien()),
                        (a, b) -> {
                            a.quantity += b.quantity;
                            a.revenue += b.revenue;
                            return a;
                        },
                        LinkedHashMap::new
                ));

        productStats.values().stream()
                .sorted((a, b) -> Integer.compare(b.quantity, a.quantity))
                .limit(7)
                .forEach(stat -> view.topSanPhamTableModel.addRow(new Object[]{
                        stat.product.getTen(),
                        stat.product.getDanhMuc(),
                        stat.quantity,
                        String.format("%.0f", stat.revenue)
                }));
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

    private void refreshRevenueCharts(List<HoaDon> paidInvoices, LocalDate fromDate, LocalDate toDate, LocalDate shiftDate) {
        RevenueSplit day = buildRevenueByDay(paidInvoices, fromDate, toDate);
        view.bieuDoTheoNgayPanel.setStackedChartData(day.cashData(), day.transferData(), "Tiền mặt", "Chuyển khoản");

        RevenueSplit month = buildRevenueByMonth(paidInvoices, fromDate, toDate);
        view.bieuDoTheoThangPanel.setStackedChartData(month.cashData(), month.transferData(), "Tiền mặt", "Chuyển khoản");

        RevenueSplit shift = buildRevenueByShift(paidInvoices, shiftDate);
        view.bieuDoTheoCaPanel.setStackedChartData(shift.cashData(), shift.transferData(), "Tiền mặt", "Chuyển khoản");
    }

    private RevenueSplit buildRevenueByDay(List<HoaDon> paidInvoices, LocalDate fromDate, LocalDate toDate) {
        LinkedHashMap<String, Double> cashData = new LinkedHashMap<>();
        LinkedHashMap<String, Double> transferData = new LinkedHashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");

        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            String key = date.format(dayFormatter);
            cashData.put(key, 0.0);
            transferData.put(key, 0.0);
        }

        for (HoaDon invoice : paidInvoices) {
            LocalDate day = invoice.getThoiGianTao().toLocalDate();
            if (!day.isBefore(fromDate) && !day.isAfter(toDate)) {
                String key = day.format(dayFormatter);
                addByPaymentMethod(invoice, key, cashData, transferData);
            }
        }
        return new RevenueSplit(cashData, transferData);
    }

    private RevenueSplit buildRevenueByMonth(List<HoaDon> paidInvoices, LocalDate fromDate, LocalDate toDate) {
        LinkedHashMap<String, Double> cashData = new LinkedHashMap<>();
        LinkedHashMap<String, Double> transferData = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");

        YearMonth startMonth = YearMonth.from(fromDate);
        YearMonth endMonth = YearMonth.from(toDate);
        for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
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

    private RevenueSplit buildRevenueByShift(List<HoaDon> paidInvoices, LocalDate shiftDate) {
        LinkedHashMap<String, Double> cashData = new LinkedHashMap<>();
        LinkedHashMap<String, Double> transferData = new LinkedHashMap<>();
        cashData.put("Ca sáng", 0.0);
        cashData.put("Ca chiều", 0.0);
        cashData.put("Ca tối", 0.0);
        transferData.put("Ca sáng", 0.0);
        transferData.put("Ca chiều", 0.0);
        transferData.put("Ca tối", 0.0);

        for (HoaDon invoice : paidInvoices) {
            if (!invoice.getThoiGianTao().toLocalDate().equals(shiftDate)) {
                continue;
            }
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

    private List<HoaDon> filterInvoicesByDateRange(List<HoaDon> invoices, LocalDate fromDate, LocalDate toDate) {
        return invoices.stream()
                .filter(invoice -> {
                    LocalDate date = invoice.getThoiGianTao().toLocalDate();
                    return !date.isBefore(fromDate) && !date.isAfter(toDate);
                })
                .toList();
    }

    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập ngày theo định dạng dd/MM/yyyy.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
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

    private static class ProductStat {
        private final SanPham product;
        private int quantity;
        private double revenue;

        public ProductStat(SanPham product, int quantity, double revenue) {
            this.product = product;
            this.quantity = quantity;
            this.revenue = revenue;
        }
    }
}
