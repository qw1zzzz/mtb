package ru.larin.mtBot.service;

import ru.larin.mtBot.exception.ServiceException;


public interface MtBotService {
    String getUSDMt() throws ServiceException;

    String getEURMt() throws ServiceException;

    String getCNYMt() throws ServiceException;

}
