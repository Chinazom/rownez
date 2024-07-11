package com.scenic.rownezcoreservice.service.email;

import com.scenic.rownezcoreservice.model.EmailTemplateParam;

import java.util.Map;
public interface EmailServiceInterface {
     boolean send (String toEmail, Map<EmailTemplateParam,String> templateParameterMapStringMap);

}
