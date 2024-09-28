package com.example.rcmd_sys.controller;

import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins="*", allowedHeaders = "*") // 다른 서버에서 호출해야 하거든 ㅋㅋ..
@RequestMapping("/rcmd")
public class RedisController {

    @GetMapping("/user/{userId}/recommendations")
    public List<String> getRecommendations(@PathVariable Long userId) {
        // Redis에서 리스트 가져오기
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String key = "user:" + userId + ":recommendations";
            return jedis.lrange(key, 0, -1);  // 모든 리스트 요소 반환
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
