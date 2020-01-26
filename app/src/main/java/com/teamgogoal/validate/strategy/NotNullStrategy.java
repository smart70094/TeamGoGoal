package com.teamgogoal.validate.strategy;

import com.teamgogoal.validate.annotation.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class NotNullStrategy implements ValidateStrategy {

    @Override
    public String execute(Object object, Field field, Annotation annotation) throws IllegalAccessException {
        NotNull notNullAnnotation = (NotNull) annotation;
        Object value = field.get(object);
        String message = notNullAnnotation.message();

        if(value == null)
            return message;

        return "";
    }
}
