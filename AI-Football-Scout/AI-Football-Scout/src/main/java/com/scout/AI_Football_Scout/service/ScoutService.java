package com.scout.AI_Football_Scout.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scout.AI_Football_Scout.entity.Player;
import com.scout.AI_Football_Scout.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoutService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private PlayerRepository playerRepository;

    public String analyzePlayer(String playerName) throws Exception {
        Player existingPlayer = null;
        
        // Quét xem tên user nhập vào có nằm trong DB ko 
        List<Player> allPlayers = playerRepository.findAll();
        for (Player p : allPlayers) {
            if (playerName.toLowerCase().contains(p.getName().toLowerCase())) {
                existingPlayer = p; 
                System.out.println("Tên chuẩn là: " + p.getName());
                break;
            }
        }

        // nếu quét vòng for ở trên vẫn ko thấy thì thử query gần đúng tìm lại lần nữa
        if (existingPlayer == null) {
            existingPlayer = playerRepository.findFirstByNameContainingIgnoreCase(playerName);
        }

        // Check cache: có bài phân tích rồi thì lấy ra luôn, ko phải gọi AI tốn request
        if (existingPlayer != null && existingPlayer.getAiReport() != null) {
            System.out.println("Lấy bài phân tích từ MySQL!");
            return existingPlayer.getAiReport();
        }

        System.out.println("Đang nhờ AI phân tích");
        String nameToAsk = (existingPlayer != null) ? existingPlayer.getName() : playerName;
        
        // --- FIX BUG AI CHÉM GIÓ: Thêm luật thép vào prompt ---
        String prompt = "Đóng vai tuyển trạch viên bóng đá. Phân tích điểm mạnh, yếu của cầu thủ: " + nameToAsk + 
                ". LƯU Ý QUAN TRỌNG: Nếu tên này là một chuỗi ký tự vô nghĩa (như asdfgh...) hoặc không phải tên người thật, CHỈ TRẢ VỀ ĐÚNG 1 CÂU: 'Dữ liệu không hợp lệ'. Tuyệt đối không tự bịa thông tin.";
        
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        // Cấu hình payload ném lên Google Gemini
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.postForObject(url, requestBody, String.class);
        
        // Bóc cục JSON lồng nhau để lấy mỗi đoạn text phân tích
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);
        String actualText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        
        // --- FIX BUG LƯU RÁC: Chỉ lưu vào DB nếu AI không báo lỗi ---
        if (!actualText.contains("Dữ liệu không hợp lệ")) {
            if (existingPlayer == null) {
                existingPlayer = new Player();
                existingPlayer.setName(playerName);
                existingPlayer.setClub("Chưa rõ"); 
            }
            existingPlayer.setAiReport(actualText);
            playerRepository.save(existingPlayer);
            System.out.println("Đã lưu bài phân tích vào DB!");
        } else {
            System.out.println("Phát hiện tên rác, từ chối lưu DB!");
        }
        
        return actualText;
    }

    public List<Player> discoverPlayers(String position) throws Exception {
        System.out.println("Đang tìm danh sách cầu thủ vị trí: " + position);
        
        // Lục DB xem đã lưu đủ 5 ông ở vị trí này chưa
        List<Player> existingPlayers = playerRepository.findByPositionIgnoreCase(position);
        
        if (existingPlayers != null && existingPlayers.size() >= 5) {
            System.out.println("Lấy danh sách từ MySQL");
            return existingPlayers.subList(0, 5); 
        }

        System.out.println("Kho chưa có! Đang gọi Google Gemini tìm 5 siêu sao...");
        String prompt = "Đóng vai tuyển trạch viên bóng đá. Tìm 5 cầu thủ trẻ tiềm năng nhất đang đá vị trí " + position + ". " +
                "BẮT BUỘC TRẢ VỀ một mảng JSON chuẩn, không giải thích gì thêm. " +
                "Cấu trúc mỗi phần tử: {\"name\": \"Tên cầu thủ\", \"club\": \"Tên CLB\"}.";

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.postForObject(url, requestBody, String.class);
        
        ObjectMapper mapper = new ObjectMapper();
        String aiJsonString = mapper.readTree(jsonResponse).path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        aiJsonString = aiJsonString.replace("```json", "").replace("```", "").trim();

        JsonNode playerArray = mapper.readTree(aiJsonString);
        List<Player> savedPlayers = new ArrayList<>();

        // Quét mảng JSON, lưu từng cầu thủ vào DB và gán tag vị trí
        for (JsonNode pNode : playerArray) {
            String pName = pNode.get("name").asText();
            String pClub = pNode.get("club").asText();

            Player existing = playerRepository.findFirstByNameContainingIgnoreCase(pName);
            if (existing == null) {
                existing = new Player();
                existing.setName(pName);
                existing.setClub(pClub);
                existing.setPosition(position); 
                playerRepository.save(existing);
            }
            savedPlayers.add(existing);
        }
        System.out.println("Đã lưu xong cầu thủ mới vào Database!");
        return savedPlayers;
    }
}