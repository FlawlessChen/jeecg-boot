package org.jeecg.modules.deepseek;

import org.springframework.web.bind.annotation.*;
import org.jeecg.modules.deepseek.DeepSeekApiClient;

import java.util.Map;

@RestController
@RequestMapping("/deepSeekApi")
public class deepseekController {
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> payload) {
        try {
            String message = payload.get("message"); // 从请求体中获取 message
            return DeepSeekApiClient.callDeepSeekApi(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
