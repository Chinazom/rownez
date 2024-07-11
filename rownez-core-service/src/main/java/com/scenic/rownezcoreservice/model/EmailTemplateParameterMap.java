package com.scenic.rownezcoreservice.model;

import com.scenic.rownezcoreservice.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static com.scenic.rownezcoreservice.model.EmailTemplateParam.*;

public class EmailTemplateParameterMap {
    static Set<EmailTemplateParam> onboardingTemplateParam = EnumSet.of(STAFF_NAME ,
            STAFF_ID ,
            STAFF_ROLE,
            DEPARTMENT,
            DATE_OF_BIRTH,
            MOBILE_NUMBER);

    public static void validateTemplateTypeParam ( Set<EmailTemplateParam> paramSet){
        if (!onboardingTemplateParam.equals(paramSet)){
            throw new ApiException( "Invalid email template element", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
