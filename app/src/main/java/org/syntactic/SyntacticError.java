package org.syntactic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.token.Token;

public class SyntacticError {
   
    private final Token token;
    private final int lineNumber;

    // Construtor
    public SyntacticError(Token token,int lineNumber) {
        this.token = token;
        this.lineNumber = lineNumber;
    }

    // Getters
    public Token getToken() {
        return token;
    }

    public int getLineNumber() {
        return lineNumber;
    }


    @Override
    public String toString() {
        return (
            "SyntacticError{" +
            "token='" +
            token +
            '\'' +
            ", lineNumber=" +
            lineNumber +
            '}'
        );
    }

    public static void saveSyntacticErrorsToJsonFile(List<SyntacticError> syntacticErrors, String filename)
        throws IOException {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(syntacticErrors, writer);
        }
    } 
}    

