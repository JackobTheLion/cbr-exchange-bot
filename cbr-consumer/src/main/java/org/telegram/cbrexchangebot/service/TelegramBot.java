package org.telegram.cbrexchangebot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.cbrexchangebot.model.Rate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUserName;

    private final RateService rateService;

    @Autowired
    public TelegramBot(@Value("${org.telegram.cbrexchangebot.token}") String botToken,
                       @Value("${org.telegram.cbrexchangebot.name}") String botUserName,
                       RateService rateService) {
        super(botToken);
        this.botUserName = botUserName;
        this.rateService = rateService;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info("Chat id: {}. Message:'{}'", chatId, messageText);

            SendMessage sendMessage = prepareSendMessage(messageText, chatId);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            SendMessage sendMessage = prepareSendMessage(call_data, update.getCallbackQuery().getMessage().getChatId());
            execute(sendMessage);
        }
    }

    private SendMessage prepareSendMessage(String request, long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        request = request.toLowerCase();
        switch (request) {
            case "/start":
                sendMessage.setText("Please choose currency");
                sendMessage.setReplyMarkup(getCurrenciesButtons());
                break;
            case "":
                break;
            default:
                Rate rate = rateService.getRate(request);
                sendMessage.setText(rate.toString());
        }
        return sendMessage;
    }

    private InlineKeyboardMarkup getCurrenciesButtons() {
        List<String> knownCurrencies = rateService.getKnownCurrencies();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        Queue<InlineKeyboardButton> inlineKeyboardButtonQueue = new LinkedList<>();
        for (String knownCurrency : knownCurrencies) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(knownCurrency);
            inlineKeyboardButton.setCallbackData(knownCurrency);
            inlineKeyboardButtonQueue.add(inlineKeyboardButton);
        }
        while (!inlineKeyboardButtonQueue.isEmpty()) {
            List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                InlineKeyboardButton poll = inlineKeyboardButtonQueue.poll();
                if (poll == null) {
                    break;
                } else inlineKeyboardButtons.add(poll);
            }
            rowsInline.add(inlineKeyboardButtons);
        }
        return new InlineKeyboardMarkup(rowsInline);
    }

    private void sendMessage(String message, long chatId) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUserName;
    }
}
