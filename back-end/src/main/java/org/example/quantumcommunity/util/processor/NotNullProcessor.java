package org.example.quantumcommunity.util.processor;

import org.example.quantumcommunity.annotation.NotNull;

import java.lang.reflect.Field;

/**
 * @author xiaol
 */
public class NotNullProcessor {
    public static void process(Object obj) throws IllegalAccessException, RuntimeException {
        Class<?> objClass = obj.getClass();
        for (Field field : objClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(NotNull.class)) {
                field.setAccessible(true);
                if (field.get(obj) == null) {
                    throw new RuntimeException("Field " + field.getName() + " cannot be null");
                }
            }
        }
    }
}