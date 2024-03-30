package org.example.quantumcommunity.util.processor;
import org.example.quantumcommunity.annotation.RandomId;
import org.example.quantumcommunity.util.generator.RandomIdGenerator;

import java.lang.reflect.Field;

/**
 * @author xiaol
 */
public class RandomIdProcessor {
    public static void process(Object obj) throws IllegalAccessException {
        Class<?> objClass = obj.getClass();
        for (Field field : objClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(RandomId.class)) {
                field.setAccessible(true);
                field.set(obj, RandomIdGenerator.generateId());
            }
        }
    }
}