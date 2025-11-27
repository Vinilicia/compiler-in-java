package org.syntactic;

import org.token.Token;

public class SemanticError {
    private String message;
    private int line;

    public SemanticError(String message, int line) {
        this.message = message;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Semantic Error at line " + line + ": " + message;
    }
}
