package ru.larin.mtBot.service.impl;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.larin.mtBot.client.CbrClient;
import ru.larin.mtBot.exception.ServiceException;
import ru.larin.mtBot.service.MtBotService;


import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;

@Service
public class MtBotServiceImpl implements MtBotService {
    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
    private static final String CNY_XPATH = "/ValCurs//Valute[@ID='R01375']/Value";

    @Autowired
    private CbrClient client;

    @Override
    public String getUSDMt() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURMt() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    @Override
    public String getCNYMt() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, CNY_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Error parsing XML", e);
        }
    }
}
