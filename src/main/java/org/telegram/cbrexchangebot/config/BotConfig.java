package org.telegram.cbrexchangebot.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    private final String botName = "test";

    public String getBotName() {
        return botName;
    }
}
