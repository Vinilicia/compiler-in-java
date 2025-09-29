package org.lexer;

import java.util.ArrayList;
import java.util.List;
import org.token.Token;
import org.token.TokenType;

public class Lexer {

    private final List<Character> characters;

    public Lexer(List<Character> characters) {
        this.characters = characters;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        int lineNumber = 1;
        int state = 0;

        for (char c : characters) {
            System.out.println(c);
        }
        return tokens;
    }
}
