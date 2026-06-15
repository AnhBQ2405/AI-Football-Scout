package com.scout.AI_Football_Scout.controller;

import com.scout.AI_Football_Scout.entity.Player;
import com.scout.AI_Football_Scout.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players") 
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository; 
    @PostMapping("/add")
    public String addPlayer(@RequestBody Player player) {
        playerRepository.save(player); 
        return "Đã lưu thành công cầu thủ: " + player.getName() + " của CLB " + player.getClub();
    }
}