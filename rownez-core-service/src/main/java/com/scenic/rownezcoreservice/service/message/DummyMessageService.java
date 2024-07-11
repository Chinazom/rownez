package com.scenic.rownezcoreservice.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty (name = "app.service.message",havingValue = "dummy")
public class DummyMessageService implements MessageServiceInterface{
    @Override
    public boolean sendMessage() {
        Logger logger = LoggerFactory.getLogger(DummyMessageService.class);
        logger.info("message sent");
        return true;
    }

    @Override
    public void readMessage() {

    }
}
