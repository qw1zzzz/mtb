package ru.larin.mtBot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.larin.mtBot.exception.ServiceException;
import ru.larin.mtBot.service.MtBotService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.String.valueOf;
import static ru.larin.mtBot.bot.Functions.*;

@Component
public class MtBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(MtBot.class);
    private static final String START= "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String CNY = "/cny";
    private static final String CONV = "/conv";
    private static final String HELP = "/help";
    private static String dateForApi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));


    @Autowired
    private MtBotService mtBotService;

    public MtBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        if (message.contains(" ")) {
            String formattedText;
            String[] amountAndCode = update.getMessage().getText().split(" ");
            if (amountAndCode.length == 2 && containsSomeCodes(amountAndCode)) {
                //dateForApi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String code = amountAndCode[0];
                int amount = Integer.parseInt(amountAndCode[1]);
                if (notNegative(amount)) {


                    String currencyRate = "";
                    switch (code) {
                        case "usd", "Usd", "USD" -> {
                            try {
                                currencyRate = mtBotService.getUSDMt();
                            } catch (ServiceException e) {
                                LOG.error("Error response rate USD");
                            }
                        }
                        case "eur", "Eur", "EUR" -> {
                            try {
                                currencyRate = mtBotService.getEURMt();
                            } catch (ServiceException e) {
                                LOG.error("Error response rate EUR");
                            }
                        }
                        case "cny", "Cny", "CNY" -> {
                            try {
                                currencyRate = mtBotService.getCNYMt();
                            } catch (ServiceException e) {
                                LOG.error("Error response rate CNY");
                            }
                        }
                    }
                    double totalAmount = amount * strToDb(currencyRate);
                    var text = "Вы переводите %s:\n" +
                            "%s*%s = %s";
                    formattedText = String.format(text, amountAndCode[0], String.valueOf(amount), String.valueOf(currencyRate), String.valueOf(totalAmount));
                    sendMessage(chatId, formattedText);
                } else {
                    sendMessage(chatId, "Введите положительную сумму");
                }
            } else if (amountAndCode.length == 3 && containsSomeCodes(amountAndCode)) {
                dateForApi = transDate(amountAndCode[2]);
                String code = amountAndCode[0];
                int amount = Integer.parseInt(amountAndCode[1]);
                if (notNegative(amount)) {
                    String currencyRate = "";
                    switch (code) {
                        case "usd", "Usd", "USD" -> {
                            try {
                                currencyRate = mtBotService.getUSDMt();
                            } catch (ServiceException e) {
                                LOG.error("Error response rate USD");
                            }
                        }
                        case "eur", "Eur", "EUR" -> {
                            try {
                                currencyRate = mtBotService.getEURMt();
                            } catch (ServiceException e) {
                                LOG.error("Error response rate EUR");
                            }
                        }
                        case "cny", "Cny", "CNY" -> {
                            try {
                                currencyRate = mtBotService.getCNYMt();
                            } catch (ServiceException e) {
                                LOG.error("Error response rate CNY");
                            }
                        }
                    }
                    double totalAmount = amount * strToDb(currencyRate);
                    var text = "Вы переводите %s по курсу %s\n" +
                            "%s*%s = %s";
                    formattedText = String.format(text, amountAndCode[0], amountAndCode[2], String.valueOf(amount), String.valueOf(currencyRate), String.valueOf(totalAmount));
                    sendMessage(chatId, formattedText);
                } else {
                    sendMessage(chatId, "Введите положительную сумму");
                }
            } else {
                sendMessage(chatId, "Неизвестная команда\nДля просмотра команд /help");
            }
        } else if (message.contains("/")) {
            dateForApi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            switch (message) {
                case START -> {
                    String firstName = update.getMessage().getFrom().getFirstName();
                    startCommand(chatId, firstName);
                }
                case USD -> {
                    usdCommand(chatId);
                }
                case EUR -> {
                    eurCommand(chatId);
                }
                case CNY -> {
                    cnyCommand(chatId);
                }
                case CONV -> {
                    convCommand(chatId);
                }
                case HELP -> {
                    helpCommand(chatId);
                }
                default -> {
                    sendMessage(chatId, "Извини, я не знаю такую команду(\nНапиши /help, если потерялся.");
                }
            }
        } else {
            sendMessage(chatId, "Неизвестная команда\nДля просмотра команд /help");
        }
    }

    @Override
    public String getBotUsername() {
        return "qw1z_mtBot";
    }

    public static String throwDateToClient() {
        return dateForApi;
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Error send message");
        }
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Привет, %s!
                Меня зовут Эмти
                
                Я помогу тебе переводить наши
                деревянные в другие валюты.
                
                P.s их три: USD, EUR, CNY.
                
                Мои команды:
                /usd - показывает курс USD на сегодня
                /eur - показывает курс EUR на сегодня
                /cny - показывает курс CNY на сегодня
                /conv - перевод валют
                
                Другие команды:
                /help - если потерялся или что-то не работает.
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = mtBotService.getUSDMt();
            var text =
                    "Дата: %s\n" +
                    "Курс: %s";
            formattedText = String.format(text, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), usd);
        } catch (ServiceException e) {
            LOG.error("Error response rate USD");
            formattedText = "Ошибка не получилось получить курс валюты :(\n" +
                    "Мы скоро это исправим";
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var eur = mtBotService.getEURMt();
            var text =
                    "Дата: %s\n" +
                    "Курс: %s";
            formattedText = String.format(text, LocalDate.now(), eur);
        } catch (ServiceException e) {
            LOG.error("Error response rate EUR");
            formattedText = "Ошибка не получилось получить курс валюты :(\n" +
                    "Мы скоро это исправим";
        }
        sendMessage(chatId, formattedText);
    }

    private void cnyCommand(Long chatId) {
        String formattedText;
        try {
            var cny = mtBotService.getCNYMt();
            var text =
                    "Дата: %s\n" +
                    "Курс: %s";
            formattedText = String.format(text, LocalDate.now(), cny);
        } catch (ServiceException e) {
            LOG.error("Error response rate CNY");
            formattedText = "Ошибка не получилось получить курс валюты :(\n" +
                    "Мы скоро это исправим";
        }
        sendMessage(chatId, formattedText);
    }

    private void convCommand(Long chatId) {
        String formattedText;
        formattedText = """
                Перевод по СЕГОДНЯШНЕМУ курсу:
                    Шаблон: "код" "сумма"
                    Пример: usd 100
                
                Перевод по курсу ДРУГОГО дня:
                    Шаблон: "код" "сумма" "дата"
                    Пример: usd 100 27.04.2002
                """;
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Прием-прием, живём-живём)
                
                Мои команды:
                /usd - показывает курс USD на сегодня
                /eur - показывает курс EUR на сегодня
                /cny - показывает курс CNY на сегодня
                /conv - перевод валют
                
                Другие команды:
                /help - если потерялся или что-то не работает.
                
                P.s Вы можете писать код валюты как начиная с прописной буквы,
                так и капсом, вы даже можете менять местами код валюты и сумму,
                но дата при переводе по курсу другого дня должна быть ВСЕГДА последней
                """;
        sendMessage(chatId, text);
    }
}
