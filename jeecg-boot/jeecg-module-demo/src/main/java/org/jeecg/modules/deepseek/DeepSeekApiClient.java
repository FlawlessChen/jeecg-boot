package org.jeecg.modules.deepseek;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class DeepSeekApiClient {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "sk-7692dc510d664b21bbf5a7d292ecff09"; // 替换为你的 API 密钥

    public static String callDeepSeekApi(String userMessage) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);

            // 设置请求头
            request.setHeader("Authorization", "Bearer " + API_KEY);
            request.setHeader("Content-Type", "application/json");

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");

            // 创建 messages 数组
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", userMessage);

            requestBody.put("messages", new Object[]{userMessageMap});

            // 将请求体转换为 JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            request.setEntity(new StringEntity(jsonBody));

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
             responseMap.get("choices").toString(); // 返回 AI 的回复
            return responseMap.get("choices").toString(); // 返回 AI 的回复

        }
    }
}