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
            String safeBan = item.getTenBan().replaceAll("[^a-zA-Z0-9_-]", "");
            String filename = "DonHang_" + safeBan + "_" + timestamp + ".pdf";
            File pdfFile = new File(dir, filename);

            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Note: Since iText 5 default font doesn't support Vietnamese well without a TTF font,
            // we use the default Helvetica. For a real app, you should load a Unicode font (e.g., Arial.ttf).
            // Here we try to use default if TTF is not provided, but Vietnamese characters might be lost.
            // Ideally load a font from OS, but to keep it simple and portable we use default for now.
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph("PHIEU CHE BIEN - " + item.getTenBan(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2});

            addTableRow(table, "Ban:", item.getTenBan(), normalFont);
            addTableRow(table, "Ten mon:", removeAccent(item.getTenMon()), normalFont);
            addTableRow(table, "So luong:", String.valueOf(item.getSoLuong()), normalFont);
            addTableRow(table, "Gia:", String.format("%.0f VND", item.getGia()), normalFont);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            addTableRow(table, "Thoi gian dat:", item.getThoiGian().format(formatter), normalFont);

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
    
    public static String removeAccent(String s) {
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D');
    }
}
