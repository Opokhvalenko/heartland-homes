package booking.service.service.impl;

import booking.service.service.NotificationService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${telegram.bot.token}")
    private String telegramBotToken;
    @Value("${telegram.chat.id}")
    private String telegramChatId;

    @Override
    public void sendMessage(String message) {
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> body = new HashMap<>();
        body.put("chat_id", telegramChatId);
        body.put("text", message);

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            log.error("Failed to send Telegram message", e);
        }
    }
}
