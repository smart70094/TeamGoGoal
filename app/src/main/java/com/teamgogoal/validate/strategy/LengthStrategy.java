package com.teamgogoal.validate.strategy;

import com.teamgogoal.validate.annotation.Length;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class LengthStrategy implements ValidateStrategy {

    @Override
    public String execute(Object object, Field field, Annotation annotation) throws IllegalAccessException {
        Length lengthAnnotation = (Length) annotation;
        String value = String.valueOf(field.get(object));
        int valueLength = value.length();
        int min = lengthAnnotation.min();
        int max = lengthAnnotation.max();
        String message = lengthAnnotation.message();

        if(valueLength < min || valueLength > max) {
            return message;
        }

        return "";
    }
}
