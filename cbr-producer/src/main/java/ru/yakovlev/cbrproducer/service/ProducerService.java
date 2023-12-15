package ru.yakovlev.cbrproducer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static ru.yakovlev.cbrproducer.config.KafkaConfig.TOPIC_NAME;

@Service
@Slf4j
public class ProducerService extends TelegramLongPollingBot {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String botUserName;

    private final ObjectMapper objectMapper;


    @Autowired
    public ProducerService(@Value("${org.telegram.cbrexchangebot.token}") String botToken,
                           @Value("${org.telegram.cbrexchangebot.name}") String botUserName,
                           KafkaTemplate<String, String> kafkaTemplate) {
        super(botToken);
        this.kafkaTemplate = kafkaTemplate;
        this.botUserName = botUserName;
        this.objectMapper = new ObjectMapper();
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        kafkaTemplate.send(TOPIC_NAME, objectMapper.writeValueAsString(update));
    }

    @Override
    public String getBotUsername() {
        return this.botUserName;
    }
}
