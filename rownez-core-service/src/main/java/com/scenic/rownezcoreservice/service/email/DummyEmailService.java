package com.scenic.rownezcoreservice.service.email;

import com.scenic.rownezcoreservice.model.EmailTemplateParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty (name = "app.service.email",havingValue = "dummy")
public class DummyEmailService implements EmailServiceInterface {

    @Override
    public boolean send(String toEmail, Map<EmailTemplateParam,String> templateParameterMapStringMap) {
        return false;
    }
}
