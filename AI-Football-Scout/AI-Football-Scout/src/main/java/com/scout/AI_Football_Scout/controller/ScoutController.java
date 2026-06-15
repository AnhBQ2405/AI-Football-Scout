package com.scout.AI_Football_Scout.controller;

import com.scout.AI_Football_Scout.entity.Player;
import com.scout.AI_Football_Scout.service.ScoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scout")
public class ScoutController {
    
    @Autowired
    private ScoutService scoutService;

    // Lấy bài phân tích chi tiết (điểm mạnh, yếu) của một cầu thủ cụ thể
    @GetMapping("/analyze")
    public String analyzePlayer(@RequestParam String playerName) throws Exception {
        return scoutService.analyzePlayer(playerName);
    }

    // Gợi ý danh sách 5 cầu thủ tiềm năng dựa trên vị trí (VD: ST, CM, CB...)
    @GetMapping("/discover")
    public List<Player> discoverPlayers(@RequestParam String position) throws Exception {
        return scoutService.discoverPlayers(position);
    }
}