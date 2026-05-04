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

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection)
                url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String systemPrompt = "Bạn là trợ lý quản lý quán cà phê.\n"
                + "Nếu người dùng hỏi về doanh thu, hãy trả về GET_REVENUE.\n"
                + "Nếu hỏi về sản phẩm, hãy trả về GET_PRODUCTS.\n"
                + "Nếu hỏi về bàn, hãy trả về GET_TABLES.\n"
                + "Nếu hỏi cách tạo đơn, hãy trả về CREATE_ORDER.\n"
                + "Nếu không thể trả lời, hãy trả về câu trả lời ngắn gọn phù hợp với ứng dụng.\n";

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