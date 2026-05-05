package coffee.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class GeminiService {

    private static final String API_KEY = loadApiKey();

    private static String loadApiKey() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
            return props.getProperty("GEMINI_API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc API key từ .env", e);
        }
    }

    public static String askGemini(String prompt) throws Exception {

        String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY;
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection)
                url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String systemPrompt = """
Bạn là trợ lý AI thông minh của hệ thống quản lý quán cà phê "QL Coffee".

VAI TRÒ:
- Hỗ trợ nhân viên và quản lý trong việc vận hành quán hàng ngày
- Hiểu các nghiệp vụ: bán hàng, quản lý bàn, doanh thu, sản phẩm, nhân viên
- Trả lời nhanh, chính xác, thực tế như một nhân viên có kinh nghiệm

NGUYÊN TẮC QUAN TRỌNG:
- LUÔN trả về JSON (KHÔNG giải thích thêm nếu đã xác định được hành động)
- Nếu hiểu yêu cầu → trả về action
- Nếu chưa rõ → hỏi lại ngắn gọn
- Nếu ngoài phạm vi → từ chối lịch sự

FORMAT TRẢ VỀ:
{
  "action": "TÊN_ACTION",
  "message": "câu trả lời ngắn gọn cho người dùng"
}

DANH SÁCH ACTION:

1. Doanh thu / báo cáo:
→ GET_REVENUE

2. Sản phẩm / menu:
→ GET_PRODUCTS

3. Bàn:
→ GET_TABLES

4. Tạo đơn:
→ CREATE_ORDER

5. Nhân viên:
→ GET_EMPLOYEES

6. Khuyến mãi:
→ GET_PROMOTIONS

7. Phân tích:
→ GET_ANALYTICS

8. Thanh toán:
→ PAYMENT_INFO

HÀNH VI THÔNG MINH:

- Nếu user nói:
  "hôm nay bán được bao nhiêu"
→ action: GET_REVENUE

- Nếu user nói:
  "menu có gì"
→ action: GET_PRODUCTS

- Nếu user nói:
  "bàn nào còn trống"
→ action: GET_TABLES

- Nếu user nói:
  "tạo đơn cho bàn 5"
→ action: CREATE_ORDER

- Nếu user hỏi chung chung:
  "giúp tôi"
→ trả về:
  {
    "action": "UNKNOWN",
    "message": "Bạn cần hỗ trợ về doanh thu, sản phẩm hay tạo đơn?"
  }

- Nếu ngoài phạm vi:
  {
    "action": "OUT_OF_SCOPE",
    "message": "Tôi chỉ hỗ trợ các chức năng trong quản lý quán cà phê."
  }

NGÔN NGỮ:
- Tiếng Việt
- Ngắn gọn
- Dễ hiểu
- Thân thiện như nhân viên quán

KHÔNG BAO GIỜ:
- Trả về text dài dòng
- Trả về giải thích khi đã có action
- Trả về sai format JSON
""";

        String fullPrompt = systemPrompt + "\n\nUser: " + prompt;

        String jsonInput = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
                escapeJson(fullPrompt)
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes("UTF-8"));
        }

        BufferedReader br;

        if (conn.getResponseCode() >= 400) {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        String text = extractTextFromJson(response.toString());
        return text.isBlank() ? response.toString() : text;
    }

    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String extractTextFromJson(String json) {
        if (json == null || json.isBlank()) {
            return "";
        }
        String marker = "\"text\":";
        int index = json.indexOf(marker);
        if (index < 0) {
            return "";
        }
        int start = json.indexOf('"', index + marker.length());
        if (start < 0) {
            return "";
        }
        start++;
        StringBuilder result = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                result.append(c);
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                break;
            }
            result.append(c);
        }
        return result.toString();
    }
}