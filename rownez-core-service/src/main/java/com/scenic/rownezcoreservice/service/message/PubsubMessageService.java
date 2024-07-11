package com.scenic.rownezcoreservice.service.message;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.service.message", havingValue = "pubsub")
public class PubsubMessageService implements MessageServiceInterface{
    @Override
    public boolean sendMessage() {
        return false;
    }

    @Override
    public void readMessage() {

    }
}
