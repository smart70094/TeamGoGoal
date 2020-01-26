package com.teamgogoal.validate.strategy;

import com.teamgogoal.utils.StringUtils;
import com.teamgogoal.validate.annotation.NotBlank;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class NotBlankStrategy implements ValidateStrategy {
    @Override
    public String execute(Object object, Field field, Annotation annotation) throws IllegalAccessException {
        NotBlank notBlankAnnotation = (NotBlank) annotation;
        String value = String.valueOf(field.get(object));
        String message = notBlankAnnotation.message();

        if(StringUtils.isNullOrEmpty(value))
            return message;

        return "";
    }
}
