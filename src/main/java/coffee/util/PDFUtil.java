package coffee.util;

import coffee.model.MonOrderBep;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class PDFUtil {

    public static Font getVietnameseFont(int size, int style) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String fontPath = "";
            if (os.contains("win")) {
                fontPath = "C:\\Windows\\Fonts\\arial.ttf";
            } else if (os.contains("mac")) {
                File f1 = new File("/System/Library/Fonts/Supplemental/Arial.ttf");
                File f2 = new File("/Library/Fonts/Arial.ttf");
                if (f1.exists()) fontPath = f1.getAbsolutePath();
                else if (f2.exists()) fontPath = f2.getAbsolutePath();
            } else {
                File f1 = new File("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf");
                if (f1.exists()) fontPath = f1.getAbsolutePath();
            }
            if (fontPath.isEmpty() || !new File(fontPath).exists()) {
                return new Font(Font.FontFamily.HELVETICA, size, style);
            }
            BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(bf, size, style);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, size, style);
        }
    }

    public static void inDonHang(MonOrderBep item) {
        try {
            // Create directory
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            File dir = new File(desktopPath, "DonHangBep");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate filename
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeBan = item.getTenBan().replaceAll("[\\\\/:*?\"<>|]", "_");
            String filename = "DonHang_" + safeBan + "_" + timestamp + ".pdf";
            File pdfFile = new File(dir, filename);

            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            Font titleFont = getVietnameseFont(18, Font.BOLD);
            Font normalFont = getVietnameseFont(12, Font.NORMAL);

            Paragraph title = new Paragraph("PHIẾU CHẾ BIẾN - " + item.getTenBan(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2});

            addTableRow(table, "Bàn:", item.getTenBan(), normalFont);
            addTableRow(table, "Tên món:", item.getTenMon(), normalFont);
            addTableRow(table, "Số lượng:", String.valueOf(item.getSoLuong()), normalFont);
            addTableRow(table, "Giá:", String.format("%.0f VND", item.getGia()), normalFont);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            addTableRow(table, "Thời gian đặt:", item.getThoiGian().format(formatter), normalFont);

            document.add(table);
            document.close();
            
            System.out.println("Đã in PDF tại: " + pdfFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi in PDF: " + e.getMessage());
        }
    }

    private static void addTableRow(PdfPTable table, String col1, String col2, Font font) {
        PdfPCell cell1 = new PdfPCell(new Phrase(col1, font));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(5);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(col2, font));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(5);
        table.addCell(cell2);
    }
    
    public static String exportTextToPdf(String text, String fileBaseName) {
        try {
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            File dir = new File(desktopPath, "HoaDon");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filename = fileBaseName + ".pdf";
            File pdfFile = new File(dir, filename);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            Font titleFont = getVietnameseFont(16, Font.BOLD);
            Font normalFont = getVietnameseFont(12, Font.NORMAL);
            document.add(new Paragraph("HÓA ĐƠN", titleFont));
            document.add(new Paragraph(" ", normalFont));

            for (String line : text.split("\\r?\\n")) {
                document.add(new Paragraph(line, normalFont));
            }

            document.close();
            return pdfFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
