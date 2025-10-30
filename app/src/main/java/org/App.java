package org;

import java.io.File;
import java.util.List;
import org.lexer.Lexer;
import org.lexer.SourceReader;
import org.syntactic.SymbolTableManager;
import org.syntactic.Syntactic;
import org.syntactic.SyntacticError;
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
            String outputFileNameLexer =
                "output/" + fileName.replace(".p", "_tokens.json");
            String outputFileNameSyntactic =
                "output/" + fileName.replace(".p", "_syntactic_errors.json");
            String outputFileNameSymbolTable =
                "output/" + fileName.replace(".p", "_symbol_tables.txt");

            // Create output directory if it doesn't exist
            new File("output").mkdirs();

            SourceReader reader = new SourceReader(filePath);

            Lexer lexer = new Lexer(reader.getCharacters());
            List<Token> tokens = lexer.tokenize();
            Token.saveTokensToJsonFile(tokens, outputFileNameLexer);

            Syntactic syntactic = new Syntactic(tokens);
            List<SyntacticError> syntacticErrors =
                syntactic.syntacticAnalysis();
            SyntacticError.saveSyntacticErrorsToJsonFile(
                syntacticErrors,
                outputFileNameSyntactic
            );

            SymbolTableManager symbolManager =
                syntactic.getSymbolTableManager();
            symbolManager.exportAllTablesToFile(outputFileNameSymbolTable);

            System.out.println("Files saved to " + filePath);
        } catch (Exception e) {
            System.err.println("Error processing file: " + filePath);
            e.printStackTrace();
        }
    }
}
