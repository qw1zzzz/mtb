package ru.larin.mtBot.configuration;

import com.squareup.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.larin.mtBot.bot.MtBot;

@Configuration
public class MtBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(MtBot mtBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(mtBot);
        return api;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

}
