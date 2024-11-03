package ru.larin.mtBot.client;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.larin.mtBot.exception.ServiceException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ru.larin.mtBot.bot.MtBot.throwDateToClient;


@Component
public class CbrClient {
    @Autowired
    private OkHttpClient client;

    @Value("${cbr.currency.rates.mxl.url}")
    private String url;


    @SneakyThrows
    public String getCurrencyRatesXML() throws ServiceException {
        String date = throwDateToClient();
        var request = new Request.Builder()
                .url(url + date)
                .build();
        var response = client.newCall(request).execute();
        try {
            var body = response.body();
            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new ServiceException("Error response rates", e);
        }
    }
}
