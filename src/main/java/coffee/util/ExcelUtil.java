package coffee.util;

import coffee.model.HoaDon;
import coffee.model.ChiTietHoaDon;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Export all invoices to a CSV file (which can be opened as Excel)
     */
    public static void exportInvoicesToExcel(List<HoaDon> invoices) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel danh sách hóa đơn");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Set default filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setSelectedFile(new File("DanhSachHoaDon_" + timestamp + ".csv"));
        
        int result = fileChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // Write BOM for UTF-8 to ensure Vietnamese characters display correctly in Excel
            writer.write("\uFEFF");
            
            // Write header
            writer.println("Mã đơn,Khách hàng,Nhân viên,Tổng tiền,Tổng thanh toán,Ngày giờ tạo,Bàn");
            
            // Write invoice data
            for (HoaDon invoice : invoices) {
                String line = String.format("\"%s\",\"Khách lẻ\",\"%s\",\"%.0f\",\"%d\",\"%s\",\"%s\"",
                        String.format("HD%03d", invoice.getMa()),
                        escapeCSV(invoice.getNhanVien().getHoTen()),
                        invoice.getTongTien(),
                        invoice.getTongThanhToan(),
                        invoice.getThoiGianTao().format(formatter),
                        escapeCSV(invoice.getBan().getTen())
                );
                writer.println(line);
            }
            
            JOptionPane.showMessageDialog(null, 
                    "Xuất file thành công!\nĐường dẫn: " + file.getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất file: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return value.replace("\"", "\"\"");
        }
        return value;
    }
}
