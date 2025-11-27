package org;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import org.ast.AstPrinter;
import org.lexer.Lexer;
import org.lexer.SourceReader;
import org.semantic.SemanticAnalyzer;
import org.symbol_table.SymbolTableManager;
import org.syntactic.SemanticError;
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
            String outputFileNameLexer = "output/" + fileName.replace(".p", "_tokens.json");
            String outputFileNameSyntactic = "output/" + fileName.replace(".p", "_syntactic_errors.json");
            String outputFileNameSemantic = "output/" + fileName.replace(".p", "_semantic_errors.txt");
            String outputFileNameSymbolTable = "output/" + fileName.replace(".p", "_symbol_tables.txt");
            String outputFileNameAst = "output/" + fileName.replace(".p", "_ast.txt");

            // Create output directory if it doesn't exist
            new File("output").mkdirs();

            SourceReader reader = new SourceReader(filePath);

            Lexer lexer = new Lexer(reader.getCharacters());
            List<Token> tokens = lexer.tokenize();
            Token.saveTokensToJsonFile(tokens, outputFileNameLexer);

            Syntactic syntactic = new Syntactic(tokens);
            List<SyntacticError> syntacticErrors = syntactic.syntacticAnalysis();
            SyntacticError.saveSyntacticErrorsToJsonFile(
                    syntacticErrors,
                    outputFileNameSyntactic);

            SymbolTableManager symbolManager = syntactic.getSymbolTableManager();
            symbolManager.exportAllTablesToFile(outputFileNameSymbolTable);

            // Semantic Analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolManager);
            List<SemanticError> semanticErrors = semanticAnalyzer.analyze(syntactic.getFunctions());

            // Add errors found during syntactic phase (redeclaration/undeclared)
            semanticErrors.addAll(syntactic.getSemanticErrors());

            // Save Semantic Errors
            try (PrintWriter out = new PrintWriter(new FileWriter(outputFileNameSemantic))) {
                for (SemanticError error : semanticErrors) {
                    out.println(error);
                }
            }

            // Save AST
            AstPrinter astPrinter = new AstPrinter();
            astPrinter.print(syntactic.getFunctions(), outputFileNameAst);

            System.out.println("Processing complete for " + filePath);
        } catch (Exception e) {
            System.err.println("Error processing file: " + filePath);
            e.printStackTrace();
        }
    }
}
