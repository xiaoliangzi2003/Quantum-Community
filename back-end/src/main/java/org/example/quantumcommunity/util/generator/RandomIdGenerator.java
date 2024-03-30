package org.example.quantumcommunity.util.generator;


/**
 * @author xiaol
 */
public class RandomIdGenerator {
    public static int generateId(){
        return (int) (Math.random() * 100000000);
    }
}
