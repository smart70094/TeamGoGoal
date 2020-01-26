package com.teamgogoal.validate;

import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.utils.StringUtils;
import com.teamgogoal.validate.annotation.Length;
import com.teamgogoal.validate.annotation.NotBlank;
import com.teamgogoal.validate.annotation.NotNull;
import com.teamgogoal.validate.annotation.Number;
import com.teamgogoal.validate.strategy.LengthStrategy;
import com.teamgogoal.validate.strategy.NotBlankStrategy;
import com.teamgogoal.validate.strategy.NotNullStrategy;
import com.teamgogoal.validate.strategy.NumberStrategy;
import com.teamgogoal.validate.strategy.ValidateStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Validator {

    public static List<String> validate(Object object) {
        Class clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

//        Field field = fields[1];
//        field.setAccessible(true);
//        try {
//            System.out.println(field.get(object));
//        } catch(Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return null;
        return validateField(object, fields);
    }

    private static List<String> validateField(Object object, Field[] fields) {
        List<String> errors = new ArrayList<>();

        for(Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            Annotation[] sortAnnotations = sortValidateaAnnotation(annotations);
            errors.addAll(validateFieldWithAnnotation(object, field, sortAnnotations));
        }

        return  errors;
    }

    private static Annotation[] sortValidateaAnnotation(Annotation[] annotations) {
        List<Class> existAnnotations = Arrays.asList(NotNull.class, NotBlank.class);

        List<Annotation> sortResult = new ArrayList<>();
        List<Annotation> priorityResult = new ArrayList<>();
        List<Annotation> otherResult = new ArrayList<>();

        for(Annotation annotation : annotations) {
            if(existAnnotations.indexOf(annotation.annotationType()) > -1)
                priorityResult.add(annotation);
            else
                otherResult.add(annotation);
        }

        sortResult.addAll(priorityResult);
        sortResult.addAll(otherResult);

        return sortResult.stream().toArray(Annotation[]::new);
    }

    private static List<String> validateFieldWithAnnotation(Object object, Field field, Annotation[] annotations) {
        List<String> errors = new ArrayList<>();

        for(Annotation annotation : annotations) {
            String validateResult = validateAnntotation(object, field, annotation);

            if(StringUtils.hasAssignment(validateResult)) {
                errors.add(validateResult);
            }
        }

        return errors;
    }

    private static String validateAnntotation(Object object, Field field, Annotation annotation) {
        ValidateStrategy validateStrategy = null;

        if(annotation instanceof NotNull) {
            validateStrategy = new NotNullStrategy();
        } else if(annotation instanceof NotBlank) {
            validateStrategy = new NotBlankStrategy();
        } else if(annotation instanceof Length) {
            validateStrategy = new LengthStrategy();
        } else if(annotation instanceof Number) {
            validateStrategy = new NumberStrategy();
        }

        return executeValidateStrategy(object, validateStrategy, field, annotation);
    }

    private static String executeValidateStrategy(Object object, ValidateStrategy validateStrategy, Field field, Annotation annotation) {
        if(validateStrategy != null) {
            String validateResult = "";
            try {
                field.setAccessible(true);
                validateResult = validateStrategy.execute(object, field, annotation);
            } catch (Exception e) {

            }
            return validateResult;
        }

        return "";
    }

    public static void main(String[] args) {
        TaskDto taskDto = TaskDto.builder()
                                    .title("12345")
                                    .content("content")
                                    .time("120")
                                    .iscomplete("y")
                                    .build();

        System.out.println(taskDto);

        System.out.println(validate(taskDto));
    }
}
