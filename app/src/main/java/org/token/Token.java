package org.token;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Token {

    private final String lexeme;
    private final TokenType type;
    private final int lineNumber;

    // Construtor
    public Token(String lexeme, TokenType type, int lineNumber) {
        this.lexeme = lexeme;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    // Getters
    public String getLexeme() {
        return lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return (
            "Token{" +
            "lexeme='" +
            lexeme +
            '\'' +
            ", type=" +
            type +
            ", lineNumber=" +
            lineNumber +
            '}'
        );
    }

    public static void saveTokensToJsonFile(List<Token> tokens, String filename)
        throws IOException {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(tokens, writer);
        }
    }
}
