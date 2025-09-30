package org.lexer;

import java.util.HashMap;
import java.util.Map;

import org.token.TokenType;

public class ReservedWords {
    private final Map<String, TokenType> hashmap = new HashMap<>();

    public ReservedWords() {
        hashmap.put("fn", TokenType.FUNCTION);    
        hashmap.put("main", TokenType.MAIN);
        hashmap.put("let", TokenType.LET);
        hashmap.put("int", TokenType.INT);
        hashmap.put("float", TokenType.FLOAT);
        hashmap.put("char", TokenType.CHAR);
        hashmap.put("if", TokenType.IF);
        hashmap.put("else", TokenType.ELSE);
        hashmap.put("while", TokenType.WHILE);
        hashmap.put("println", TokenType.PRINTLN);
        hashmap.put("return", TokenType.RETURN);
    }

    public Map<String, TokenType> getReservedWords() {
        return Map.copyOf(hashmap);
    }
}
