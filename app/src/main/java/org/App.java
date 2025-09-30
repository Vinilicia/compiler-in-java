package org;

import java.util.List;
import org.lexer.Lexer;
import org.lexer.SourceReader;
import org.token.Token;

public class App {

    public static void main(String[] args) {
        try {
            String inputFile = "loop_simples.p";
            String outputFile = inputFile.replace(".p", "_tokens.json");

            SourceReader reader = new SourceReader(inputFile);

            Lexer lexer = new Lexer(reader.getCharacters());
            List<Token> tokens = lexer.tokenize();

            Token.saveTokensToJsonFile(tokens, outputFile);

            System.out.println("Tokens saved to " + outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
