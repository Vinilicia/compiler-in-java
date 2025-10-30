package org.syntactic;

import java.util.ArrayList;
import java.util.List;
import org.token.Token;
import org.token.TokenType;

public class Syntactic {

    private final List<Token> tokens;
    private List<SyntacticError> syntacticErrors;
    private SymbolTableManager symbolManager = new SymbolTableManager();
    private Token token;
    private int i;

    public Syntactic(List<Token> tokens) {
        this.tokens = tokens;
        this.token = tokens.get(0);
        this.i = 0;
        this.syntacticErrors = new ArrayList<>();
    }

    public SymbolTableManager getSymbolTableManager() {
        return symbolManager;
    }

    public void PrintError() {
        SyntacticError newError = new SyntacticError(
            token,
            token.getLineNumber()
        );
        System.err.println(
            "Syntactic error. Token " +
                token.toString() +
                " not expected in input."
        );
        syntacticErrors.add(newError);
        i++;
        if (i < tokens.size()) {
            token = tokens.get(i);
        }
    }

    public boolean match(TokenType expectedType) {
        if (token.getType() == expectedType) {
            i++;
            if (i < tokens.size()) {
                token = tokens.get(i);
            }
            return true;
        } else {
            PrintError();
            return false;
        }
    }

    public List<SyntacticError> syntacticAnalysis() {
        Programa();
        return syntacticErrors;
    }

    private void Programa() {
        if (token.getType() == TokenType.FUNCTION) {
            Funcao();
            FuncaoSeq();
        } else {
            PrintError();
        }
    }

    private void FuncaoSeq() {
        if (token.getType() == TokenType.FUNCTION) {
            Funcao();
            FuncaoSeq();
        }
    }

    private void Funcao() {
        match(TokenType.FUNCTION);
        String funcName = token.getLexeme();
        NomeFuncao();
        symbolManager.createFunctionTable(funcName, DataType.VOID);
        SymbolTable currentTable = symbolManager.getCurrentTable();
        match(TokenType.LBRACKET);
        ListaParams();
        match(TokenType.RBRACKET);
        DataType retType = TipoRetornoFuncao();
        currentTable.setReturnType(retType);
        Bloco();
        symbolManager.closeCurrentTable();
    }

    private void NomeFuncao() {
        if (token.getType() == TokenType.ID) {
            match(TokenType.ID);
        } else if (token.getType() == TokenType.MAIN) {
            match(TokenType.MAIN);
        } else {
            PrintError();
        }
    }

    private void ListaParams() {
        if (token.getType() == TokenType.ID) {
            String paramName = token.getLexeme();
            match(TokenType.ID);
            match(TokenType.COLON);
            DataType type = Type();
            symbolManager.addParam(paramName, type);
            ListaParams2();
        }
    }

    private void ListaParams2() {
        if (token.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            String paramName = token.getLexeme();
            match(TokenType.ID);
            match(TokenType.COLON);
            DataType type = Type();
            symbolManager.addParam(paramName, type);
            ListaParams2();
        }
    }

    private DataType TipoRetornoFuncao() {
        if (token.getType() == TokenType.ARROW) {
            match(TokenType.ARROW);
            return Type();
        }
        return DataType.VOID;
    }

    private void Bloco() {
        match(TokenType.LBRACE);
        Sequencia();
        match(TokenType.RBRACE);
    }

    private void Sequencia() {
        if (token.getType() == TokenType.LET) {
            Declaracao();
            Sequencia();
        } else if (
            token.getType() == TokenType.ID ||
            token.getType() == TokenType.IF ||
            token.getType() == TokenType.WHILE ||
            token.getType() == TokenType.PRINTLN ||
            token.getType() == TokenType.RETURN
        ) {
            Comando();
            Sequencia();
        }
    }

    private void Declaracao() {
        match(TokenType.LET);
        List<String> vars = VarList();
        match(TokenType.COLON);
        DataType type = Type();
        match(TokenType.SEMICOLON);
        for (String v : vars) {
            symbolManager.addVariable(v, type);
        }
    }

    private List<String> VarList() {
        List<String> vars = new ArrayList<>();
        vars.add(token.getLexeme());
        match(TokenType.ID);
        vars.addAll(VarList2());
        return vars;
    }

    private List<String> VarList2() {
        List<String> vars = new ArrayList<>();
        if (token.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            vars.add(token.getLexeme());
            match(TokenType.ID);
            vars.addAll(VarList2());
        }
        return vars;
    }

    private DataType Type() {
        switch (token.getType()) {
            case INT:
                match(TokenType.INT);
                return DataType.INT;
            case FLOAT:
                match(TokenType.FLOAT);
                return DataType.FLOAT;
            case CHAR:
                match(TokenType.CHAR);
                return DataType.CHAR;
            default:
                PrintError();
                return DataType.ERROR;
        }
    }

    private void Comando() {
        switch (token.getType()) {
            case ID:
                match(TokenType.ID);
                AtribuicaoOuChamada();
                break;
            case IF:
                ComandoSe();
                break;
            case WHILE:
                match(TokenType.WHILE);
                Expr();
                Bloco();
                break;
            case PRINTLN:
                match(TokenType.PRINTLN);
                match(TokenType.LBRACKET);
                match(TokenType.FMT_STRING);
                match(TokenType.COMMA);
                ListaArgs();
                match(TokenType.RBRACKET);
                match(TokenType.SEMICOLON);
                break;
            case RETURN:
                match(TokenType.RETURN);
                Expr();
                match(TokenType.SEMICOLON);
                break;
            default:
                PrintError();
                break;
        }
    }

    private void AtribuicaoOuChamada() {
        if (token.getType() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            Expr();
            match(TokenType.SEMICOLON);
        } else if (token.getType() == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            ListaArgs();
            match(TokenType.RBRACKET);
            match(TokenType.SEMICOLON);
        } else {
            PrintError();
        }
    }

    private void ComandoSe() {
        if (token.getType() == TokenType.IF) {
            match(TokenType.IF);
            Expr();
            Bloco();
            ComandoSenao();
        } else if (token.getType() == TokenType.LBRACE) {
            match(TokenType.LBRACE);
            Bloco();
        } else {
            PrintError();
        }
    }

    private void ComandoSenao() {
        if (token.getType() == TokenType.ELSE) {
            match(TokenType.ELSE);
            ComandoSe();
        }
    }

    private void Expr() {
        if (
            token.getType() == TokenType.ID ||
            token.getType() == TokenType.INT_CONST ||
            token.getType() == TokenType.FLOAT_CONST ||
            token.getType() == TokenType.CHAR_LITERAL ||
            token.getType() == TokenType.LBRACKET
        ) {
            Rel();
            ExprOpc();
        } else {
            PrintError();
        }
    }

    private void ExprOpc() {
        if (token.getType() == TokenType.EQ) {
            OpIgual();
            Rel();
            ExprOpc();
        }
    }

    private void OpIgual() {
        if (token.getType() == TokenType.EQ) {
            match(TokenType.EQ);
        } else if (token.getType() == TokenType.NE) {
            match(TokenType.NE);
        } else {
            PrintError();
        }
    }

    private void Rel() {
        if (
            token.getType() == TokenType.ID ||
            token.getType() == TokenType.INT_CONST ||
            token.getType() == TokenType.FLOAT_CONST ||
            token.getType() == TokenType.CHAR_LITERAL ||
            token.getType() == TokenType.LBRACKET
        ) {
            Adicao();
            RelOpc();
        } else {
            PrintError();
        }
    }

    private void RelOpc() {
        if (token.getType() == TokenType.LT) {
            OpRel();
            Adicao();
            RelOpc();
        }
    }

    private void OpRel() {
        if (token.getType() == TokenType.LT) {
            match(TokenType.LT);
        } else if (token.getType() == TokenType.LE) {
            match(TokenType.LE);
        } else if (token.getType() == TokenType.GT) {
            match(TokenType.GT);
        } else if (token.getType() == TokenType.GE) {
            match(TokenType.GE);
        } else {
            PrintError();
        }
    }

    private void Adicao() {
        if (
            token.getType() == TokenType.ID ||
            token.getType() == TokenType.INT_CONST ||
            token.getType() == TokenType.FLOAT_CONST ||
            token.getType() == TokenType.CHAR_LITERAL ||
            token.getType() == TokenType.LBRACKET
        ) {
            Termo();
            AdicaoOpc();
        } else {
            PrintError();
        }
    }

    private void AdicaoOpc() {
        if (
            token.getType() == TokenType.PLUS ||
            token.getType() == TokenType.MINUS
        ) {
            OpAdicao();
            Termo();
            AdicaoOpc();
        }
    }

    private void OpAdicao() {
        if (token.getType() == TokenType.PLUS) {
            match(TokenType.PLUS);
        } else if (token.getType() == TokenType.MINUS) {
            match(TokenType.MINUS);
        } else {
            PrintError();
        }
    }

    private void Termo() {
        if (
            token.getType() == TokenType.ID ||
            token.getType() == TokenType.INT_CONST ||
            token.getType() == TokenType.FLOAT_CONST ||
            token.getType() == TokenType.CHAR_LITERAL ||
            token.getType() == TokenType.LBRACKET
        ) {
            Fator();
            TermoOpc();
        } else {
            PrintError();
        }
    }

    private void TermoOpc() {
        if (
            token.getType() == TokenType.MULT ||
            token.getType() == TokenType.DIV
        ) {
            OpMult();
            Fator();
            TermoOpc();
        }
    }

    private void OpMult() {
        if (token.getType() == TokenType.MULT) {
            match(TokenType.MULT);
        } else if (token.getType() == TokenType.DIV) {
            match(TokenType.DIV);
        } else {
            PrintError();
        }
    }

    private void Fator() {
        if (token.getType() == TokenType.ID) {
            String idName = token.getLexeme();
            match(TokenType.ID);
            ChamadaFuncao(idName);
        } else if (token.getType() == TokenType.INT_CONST) {
            match(TokenType.INT_CONST);
        } else if (token.getType() == TokenType.FLOAT_CONST) {
            match(TokenType.FLOAT_CONST);
        } else if (token.getType() == TokenType.CHAR_LITERAL) {
            match(TokenType.CHAR_LITERAL);
        } else if (token.getType() == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            Expr();
            match(TokenType.RBRACKET);
        } else {
            PrintError();
        }
    }

    private void ChamadaFuncao(String funcName) {
        if (token.getType() == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            List<String> args = ListaArgs();
            match(TokenType.RBRACKET);
            symbolManager.addFunctionCall(funcName, args);
        }
    }

    private List<String> ListaArgs() {
        List<String> args = new ArrayList<>();
        if (
            token.getType() == TokenType.ID ||
            token.getType() == TokenType.INT_CONST ||
            token.getType() == TokenType.FLOAT_CONST ||
            token.getType() == TokenType.CHAR_LITERAL
        ) {
            args.add(Arg());
            args.addAll(ListaArgs2());
        }
        return args;
    }

    private List<String> ListaArgs2() {
        List<String> args = new ArrayList<>();
        if (token.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            args.add(Arg());
            args.addAll(ListaArgs2());
        }
        return args;
    }

    private String Arg() {
        String value = "";
        if (token.getType() == TokenType.ID) {
            value = token.getLexeme();
            match(TokenType.ID);
            ChamadaFuncao(value);
        } else if (token.getType() == TokenType.INT_CONST) {
            value = token.getLexeme();
            match(TokenType.INT_CONST);
        } else if (token.getType() == TokenType.FLOAT_CONST) {
            value = token.getLexeme();
            match(TokenType.FLOAT_CONST);
        } else if (token.getType() == TokenType.CHAR_LITERAL) {
            value = token.getLexeme();
            match(TokenType.CHAR_LITERAL);
        } else {
            PrintError();
        }
        return value;
    }
}
