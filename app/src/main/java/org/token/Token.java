package org.token;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

    public void saveToJsonFile(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Token> tokens = new ArrayList<>();

        try {
            File file = new File(filename);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Token[] existingTokens = gson.fromJson(reader, Token[].class);
                if (existingTokens != null) {
                    for (Token t : existingTokens) {
                        tokens.add(t);
                    }
                }
                reader.close();
            }

            tokens.add(this);

            FileWriter writer = new FileWriter(file);
            gson.toJson(tokens, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
