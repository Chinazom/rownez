package com.scenic.rownezcoreservice.service.email;

import com.google.api.client.util.Value;
import com.scenic.rownezcoreservice.model.EmailTemplateParam;
import com.scenic.rownezcoreservice.model.EmailTemplateParameterMap;
import com.scenic.rownezcoreservice.model.EmailTemplateType;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.service.email", havingValue = "sendgrid")
public class SendGridEmailService implements EmailServiceInterface {
    @Value ("${app.sendgrid.api.key}")
    String sendGridApiKey;
    String emailId = "edyzena@yahoo.co.uk";
    private static  final Map <EmailTemplateType, String> EMAIL_TEMPLATE_TYPE_ID = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);
    static {
        EMAIL_TEMPLATE_TYPE_ID.put(EmailTemplateType.STAFF_ON_BOARDING,"d-829bfbfc3bcb4f7ba1b7b670290a4b8e");
    }
    @Override
    public boolean send(String toEmail,Map<EmailTemplateParam,String> templateParameterMapStringMap) {
        Mail mail = dynamicTemplateBuilder();
        mail.addPersonalization(setPersonalisation(toEmail, templateParameterMapStringMap));
        try {
            return send(mail);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean send(Mail mail) throws IOException {
        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        if (response.getStatusCode() == HttpStatus.ACCEPTED.value()){
            logger.info("Sendgrid email service trigger successfully");
            return true;
        }
        return false;
    }

    private Mail dynamicTemplateBuilder() {
        Email from = new Email(emailId);
        from.setName("Rownez Resort");
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(EMAIL_TEMPLATE_TYPE_ID.get(EmailTemplateType.STAFF_ON_BOARDING));
        return mail;
    }

    private Personalization setPersonalisation (String toEmail, Map<EmailTemplateParam,String> templateParameterMapStringMap){
        Personalization personalization = new Personalization();

        for (Map.Entry <EmailTemplateParam, String> paramEntry: templateParameterMapStringMap.entrySet()) {
            personalization.addDynamicTemplateData(paramEntry.getKey().getParam(), paramEntry.getValue());
        }
        personalization.addTo(new Email(toEmail));
        return personalization;
    }

}
