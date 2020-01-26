package com.teamgogoal.validate.strategy;

import com.teamgogoal.validate.annotation.Number;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class NumberStrategy implements ValidateStrategy {

    @Override
    public String execute(Object object, Field field, Annotation annotation) throws IllegalAccessException {
        Number numberAnnotation = (Number) annotation;
        String value = String.valueOf(field.get(object));
        String message = numberAnnotation.message();

        try {
            Integer.parseInt(value);
            return "";
        } catch (Exception e) {
            return message;
        }
    }
}
