package org.telegram.cbrexchangebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.cbrexchangebot.config.KafkaConsumerConfig.TOPIC_NAME;

@Service
@Slf4j
public class KafkaListener {

    private final ObjectMapper objectMapper;
    private final TelegramBot telegramBot;

    public KafkaListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.objectMapper = new ObjectMapper();
    }

    @org.springframework.kafka.annotation.KafkaListener(
            topics = TOPIC_NAME,
            groupId = "groupId"

    )
    public void listener(String data) {
        try {
            Update update = objectMapper.readValue(data, Update.class);


        } catch (JsonProcessingException e) {
            log.error("Deserialization error: {}", data);
        }
    }
}
