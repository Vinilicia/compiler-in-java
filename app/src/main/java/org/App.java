package org;

import org.lexer.SourceReader;
import org.token.Token;
import org.token.TokenType;

public class App {

    public static void main(String[] args) {
        // // Cria um token de exemplo
        // Token token = new Token("myVariable", TokenType.EQ, 1);

        // // Mostra no console
        // System.out.println(token);

        // // Escreve no JSON (arquivo criado na pasta do app)
        // token.saveToJsonFile("tokens.json");

        // System.out.println("Token saved to tokens.json!");
        //
        try {
            SourceReader reader = new SourceReader("test.p");

            System.out.println("Characters loaded:");
            System.out.println(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
