package org.lexer;

import java.util.ArrayList;
import java.util.List;
import org.token.Token;
import org.token.TokenType;

public class Lexer {

    private final List<Character> characters;

    ReservedWords reservedWords = new ReservedWords();

    public Lexer(List<Character> characters) {
        this.characters = characters;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        String lexeme = new String();
        int lineNumber = 1;
        int state = 0;
        boolean isLexemeOver = false;
        boolean isLastCharacterUsed = true;
        TokenType actualTokenType = null;
        int i = 0;

        while (i < characters.size()) {
            isLastCharacterUsed = true;
            char c = characters.get(i);

            if (lexeme == "" && Character.isWhitespace(c)) {
                if (c == '\n') {
                    lineNumber++;
                }
                i++;
                continue; // skip to next character
            }

            switch (state) {
                case 0:
                    switch (c) {
                        case '(':
                            actualTokenType = TokenType.LBRACE;
                            isLexemeOver = true;
                            break;
                        case ')':
                            actualTokenType = TokenType.RBRACE;
                            isLexemeOver = true;
                            break;
                        case '{':
                            actualTokenType = TokenType.LBRACKET;
                            isLexemeOver = true;
                            break;
                        case '}':
                            actualTokenType = TokenType.RBRACKET;
                            isLexemeOver = true;
                            break;
                        case ':':
                            actualTokenType = TokenType.COLON;
                            isLexemeOver = true;
                            break;
                        case ';':
                            actualTokenType = TokenType.SEMICOLON;
                            isLexemeOver = true;
                            break;
                        case ',':
                            actualTokenType = TokenType.COMMA;
                            isLexemeOver = true;
                            break;
                        case '+':
                            actualTokenType = TokenType.PLUS;
                            isLexemeOver = true;
                            break;
                        case '*':
                            actualTokenType = TokenType.MULT;
                            isLexemeOver = true;
                            break;
                        case '/':
                            actualTokenType = TokenType.DIV;
                            isLexemeOver = true;
                            break;
                        case '-':
                            state = 1;
                            break;
                        case '!':
                            state = 2;
                            break;
                        case '=':
                            state = 3;
                            break;
                        case '>':
                            state = 4;
                            break;
                        default:
                            if (Character.isLetter(c)) {
                                state = 6;
                            } else if (Character.isDigit(c)) {
                                state = 7;
                            }
                            break;
                    }
                    break;
                case 1:
                    if (c == '>') {
                        actualTokenType = TokenType.ARROW;
                    } else {
                        actualTokenType = TokenType.MINUS;
                        isLastCharacterUsed = false;
                    }
                    isLexemeOver = true;
                    state = 0;
                    break;
                case 2:
                    if (c == '=') {
                        actualTokenType = TokenType.NE;
                        isLexemeOver = true;
                        state = 0;
                    } else {
                        System.out.println("Erro na linha " + lineNumber);
                    }
                    break;
                case 3:
                    if (c == '=') {
                        actualTokenType = TokenType.EQ;
                    } else {
                        actualTokenType = TokenType.ASSIGN;
                        isLastCharacterUsed = false;
                    }
                    isLexemeOver = true;
                    state = 0;
                    break;
                case 4:
                    if (c == '=') {
                        actualTokenType = TokenType.GE;
                    } else {
                        actualTokenType = TokenType.GT;
                        isLastCharacterUsed = false;
                    }
                    isLexemeOver = true;
                    state = 0;
                    break;
                case 5:
                    if (c == '=') {
                        actualTokenType = TokenType.LE;
                    } else {
                        actualTokenType = TokenType.LT;
                        isLastCharacterUsed = false;
                    }
                    state = 0;
                    isLexemeOver = true;
                    break;
                case 6:
                    if (Character.isLetterOrDigit(c) || c == '_') {
                        // continue accumulating the identifier
                    } else {
                        var reservedMap = reservedWords.getReservedWords();

                        if (!reservedMap.containsKey(lexeme)) {
                            actualTokenType = TokenType.ID;
                        } else {
                            actualTokenType = reservedMap.get(lexeme);
                        }
                        isLastCharacterUsed = false;
                        state = 0;
                        isLexemeOver = true;
                    }
                    break;
                case 7:
                    if (!Character.isDigit(c)) {
                        if (c == '.') {
                            state = 8;
                        } else {
                            actualTokenType = TokenType.INT_CONST;
                            isLastCharacterUsed = false;
                            isLexemeOver = true;
                        }
                    }
                    break;
                case 8:
                    if (Character.isDigit(c)) {
                        state = 9;
                    } else {
                        System.out.println("Erro na linha " + lineNumber);
                    }
                    break;
                case 9:
                    if (!Character.isDigit(c)) {
                        actualTokenType = TokenType.FLOAT_CONST;
                        isLastCharacterUsed = false;
                        isLexemeOver = true;
                        state = 0;
                    }
                    break;
                default:
                    break;
            }

            if (isLastCharacterUsed) {
                i++;
                lexeme += c;
            }

            if (isLexemeOver) {
                Token newToken = new Token(lexeme, actualTokenType, lineNumber);
                tokens.add(newToken);
                lexeme = "";
                isLexemeOver = false;
            }
        }
        return tokens;
    }
}
