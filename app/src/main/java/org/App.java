package org;

import java.io.File;
import java.util.List;
import org.lexer.Lexer;
import org.lexer.SourceReader;
import org.token.Token;

public class App {

    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                processFile(arg);
            }
        } else {
            processFile("src/main/resources/soma.p");
        }
    }

    private static void processFile(String filePath) {
        try {
            File inputFile = new File(filePath);
            String fileName = inputFile.getName();
            String outputFileName = "output/" + fileName.replace(".p", "_tokens.json");

            // Create output directory if it doesn't exist
            new File("output").mkdirs();

            SourceReader reader = new SourceReader(filePath);
            Lexer lexer = new Lexer(reader.getCharacters());
            List<Token> tokens = lexer.tokenize();

            Token.saveTokensToJsonFile(tokens, outputFileName);

            System.out.println("Tokens saved to " + outputFileName);
        } catch (Exception e) {
            System.err.println("Error processing file: " + filePath);
            e.printStackTrace();
        }
    }
}
