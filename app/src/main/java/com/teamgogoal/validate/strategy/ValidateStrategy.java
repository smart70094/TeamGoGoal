package com.teamgogoal.validate.strategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface ValidateStrategy {
    public String execute(Object object, Field field, Annotation annotation) throws IllegalAccessException;
}
