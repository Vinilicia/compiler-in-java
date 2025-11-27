package org.syntactic;

import java.util.ArrayList;
import java.util.List;
import org.ast.*;
import org.symbol_table.DataType;
import org.symbol_table.SymbolTable;
import org.symbol_table.SymbolTableManager;
import org.symbol_table.TableEntry;
import org.token.Token;
import org.token.TokenType;

public class Syntactic {

    private final List<Token> tokens;
    private List<SyntacticError> syntacticErrors;
    private List<SemanticError> semanticErrors;
    private List<FunctionNode> functions;
    private SymbolTableManager symbolManager = new SymbolTableManager();
    private Token token;
    private int i;

    public Syntactic(List<Token> tokens) {
        this.tokens = tokens;
        this.token = tokens.get(0);
        this.i = 0;
        this.syntacticErrors = new ArrayList<>();
        this.semanticErrors = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public SymbolTableManager getSymbolTableManager() {
        return symbolManager;
    }

    public void PrintError() {
        SyntacticError newError = new SyntacticError(
                token,
                token.getLineNumber());
        System.err.println(
                "Syntactic error. Token " +
                        token.toString() +
                        " not expected in input.");
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

    public List<FunctionNode> getFunctions() {
        return functions;
    }

    public List<SemanticError> getSemanticErrors() {
        return semanticErrors;
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

        FunctionNode funcNode = new FunctionNode(funcName, retType.toString());
        BlocoNode bloco = Bloco();
        funcNode.addChild(bloco);
        functions.add(funcNode);

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

    private BlocoNode Bloco() {
        BlocoNode bloco = new BlocoNode();
        match(TokenType.LBRACE);
        Sequencia(bloco);
        match(TokenType.RBRACE);
        return bloco;
    }

    private void Sequencia(BlocoNode bloco) {
        if (token.getType() == TokenType.LET) {
            Declaracao();
            Sequencia(bloco);
        } else if (token.getType() == TokenType.ID ||
                token.getType() == TokenType.IF ||
                token.getType() == TokenType.WHILE ||
                token.getType() == TokenType.PRINTLN ||
                token.getType() == TokenType.RETURN) {
            AstNode cmd = Comando();
            if (cmd != null) {
                bloco.addChild(cmd);
            }
            Sequencia(bloco);
        }
    }

    private void Declaracao() {
        match(TokenType.LET);
        List<String> vars = VarList();
        match(TokenType.COLON);
        DataType type = Type();
        match(TokenType.SEMICOLON);
        for (String v : vars) {
            if (!symbolManager.addVariable(v, type)) {
                semanticErrors.add(new SemanticError("Variable " + v + " already declared.", token.getLineNumber()));
            }
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

    private AstNode Comando() {
        switch (token.getType()) {
            case ID:
                String idName = token.getLexeme();
                match(TokenType.ID);
                return AtribuicaoOuChamada(idName);
            case IF:
                return ComandoSe();
            case WHILE:
                match(TokenType.WHILE);
                AstNode cond = Expr();
                BlocoNode loopBlock = Bloco();
                WhileNode whileNode = new WhileNode();
                whileNode.addChild(cond);
                whileNode.addChild(loopBlock);
                return whileNode;
            case PRINTLN:
                match(TokenType.PRINTLN);
                match(TokenType.LBRACKET);
                match(TokenType.FMT_STRING);
                match(TokenType.COMMA);
                List<AstNode> args = ListaArgs();
                match(TokenType.RBRACKET);
                match(TokenType.SEMICOLON);
                PrintNode printNode = new PrintNode();
                for (AstNode arg : args) {
                    printNode.addChild(arg);
                }
                return printNode;
            case RETURN:
                match(TokenType.RETURN);
                AstNode retExpr = Expr();
                match(TokenType.SEMICOLON);
                ReturnNode retNode = new ReturnNode();
                retNode.addChild(retExpr);
                return retNode;
            default:
                PrintError();
                return null;
        }
    }

    private AstNode AtribuicaoOuChamada(String idName) {
        if (token.getType() == TokenType.ASSIGN) {
            // Check if variable is declared
            if (symbolManager.lookup(idName) == null) {
                semanticErrors.add(new SemanticError("Variable " + idName + " not declared.", token.getLineNumber()));
            }
            match(TokenType.ASSIGN);
            AstNode expr = Expr();
            match(TokenType.SEMICOLON);
            AssignNode assign = new AssignNode();
            assign.addChild(new IdNode(idName));
            assign.addChild(expr);
            return assign;
        } else if (token.getType() == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            List<AstNode> args = ListaArgs();
            match(TokenType.RBRACKET);
            match(TokenType.SEMICOLON);
            // Check if function is declared? Or add call ref.
            // Requirement says "vetor de referências". Logic in addFunctionCall handles it.
            List<String> argNames = new ArrayList<>(); // We need strings for addFunctionCall, but we have nodes now.
            // This is tricky. addFunctionCall expects List<String>.
            // But args are expressions now.
            // The original code passed List<String> from ListaArgs.
            // But ListaArgs can contain expressions.
            // Let's keep addFunctionCall for Symbol Table purposes, but we might need to
            // extract names if possible, or just skip it if it's complex expr.
            // Actually, the original code only supported simple args?
            // "ListaArgs" calls "Arg". "Arg" handles ID, CONSTs.
            // So we can extract text from nodes if they are simple.

            List<String> strArgs = new ArrayList<>();
            for (AstNode node : args) {
                if (node instanceof IdNode)
                    strArgs.add(((IdNode) node).getName());
                else if (node instanceof IntConstNode)
                    strArgs.add(String.valueOf(((IntConstNode) node).getValue()));
                else if (node instanceof FloatConstNode)
                    strArgs.add(String.valueOf(((FloatConstNode) node).getValue()));
                else if (node instanceof CharConstNode)
                    strArgs.add(String.valueOf(((CharConstNode) node).getValue()));
                else
                    strArgs.add("expr");
            }
            symbolManager.addFunctionCall(idName, strArgs);

            CallNode call = new CallNode(idName);
            for (AstNode arg : args)
                call.addChild(arg);
            return call;
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode ComandoSe() {
        if (token.getType() == TokenType.IF) {
            match(TokenType.IF);
            AstNode cond = Expr();
            BlocoNode trueBlock = Bloco();
            AstNode elseBlock = ComandoSenao();

            IfNode ifNode = new IfNode();
            ifNode.addChild(cond);
            ifNode.addChild(trueBlock);
            if (elseBlock != null) {
                ifNode.addChild(elseBlock);
            }
            return ifNode;
        } else if (token.getType() == TokenType.LBRACE) {
            return Bloco();
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode ComandoSenao() {
        if (token.getType() == TokenType.ELSE) {
            match(TokenType.ELSE);
            // Recursively call ComandoSe?
            // Original: ComandoSe().
            // But ComandoSe returns AstNode (IfNode or Bloco).
            return ComandoSe();
        }
        return null;
    }

    private AstNode Expr() {
        if (token.getType() == TokenType.ID ||
                token.getType() == TokenType.INT_CONST ||
                token.getType() == TokenType.FLOAT_CONST ||
                token.getType() == TokenType.CHAR_LITERAL ||
                token.getType() == TokenType.LBRACKET) {
            AstNode left = Rel();
            return ExprOpc(left);
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode ExprOpc(AstNode left) {
        if (token.getType() == TokenType.EQ || token.getType() == TokenType.NE) {
            String op = OpIgual();
            AstNode right = Rel();
            RelOpNode node = new RelOpNode(op);
            node.addChild(left);
            node.addChild(right);
            return ExprOpc(node);
        }
        return left;
    }

    private String OpIgual() {
        if (token.getType() == TokenType.EQ) {
            match(TokenType.EQ);
            return "==";
        } else if (token.getType() == TokenType.NE) {
            match(TokenType.NE);
            return "!=";
        } else {
            PrintError();
            return "";
        }
    }

    private AstNode Rel() {
        if (token.getType() == TokenType.ID ||
                token.getType() == TokenType.INT_CONST ||
                token.getType() == TokenType.FLOAT_CONST ||
                token.getType() == TokenType.CHAR_LITERAL ||
                token.getType() == TokenType.LBRACKET) {
            AstNode left = Adicao();
            return RelOpc(left);
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode RelOpc(AstNode left) {
        if (token.getType() == TokenType.LT || token.getType() == TokenType.LE ||
                token.getType() == TokenType.GT || token.getType() == TokenType.GE) {
            String op = OpRel();
            AstNode right = Adicao();
            RelOpNode node = new RelOpNode(op);
            node.addChild(left);
            node.addChild(right);
            return RelOpc(node);
        }
        return left;
    }

    private String OpRel() {
        if (token.getType() == TokenType.LT) {
            match(TokenType.LT);
            return "<";
        } else if (token.getType() == TokenType.LE) {
            match(TokenType.LE);
            return "<=";
        } else if (token.getType() == TokenType.GT) {
            match(TokenType.GT);
            return ">";
        } else if (token.getType() == TokenType.GE) {
            match(TokenType.GE);
            return ">=";
        } else {
            PrintError();
            return "";
        }
    }

    private AstNode Adicao() {
        if (token.getType() == TokenType.ID ||
                token.getType() == TokenType.INT_CONST ||
                token.getType() == TokenType.FLOAT_CONST ||
                token.getType() == TokenType.CHAR_LITERAL ||
                token.getType() == TokenType.LBRACKET) {
            AstNode left = Termo();
            return AdicaoOpc(left);
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode AdicaoOpc(AstNode left) {
        if (token.getType() == TokenType.PLUS ||
                token.getType() == TokenType.MINUS) {
            String op = OpAdicao();
            AstNode right = Termo();
            AritOpNode node = new AritOpNode(op);
            node.addChild(left);
            node.addChild(right);
            return AdicaoOpc(node);
        }
        return left;
    }

    private String OpAdicao() {
        if (token.getType() == TokenType.PLUS) {
            match(TokenType.PLUS);
            return "+";
        } else if (token.getType() == TokenType.MINUS) {
            match(TokenType.MINUS);
            return "-";
        } else {
            PrintError();
            return "";
        }
    }

    private AstNode Termo() {
        if (token.getType() == TokenType.ID ||
                token.getType() == TokenType.INT_CONST ||
                token.getType() == TokenType.FLOAT_CONST ||
                token.getType() == TokenType.CHAR_LITERAL ||
                token.getType() == TokenType.LBRACKET) {
            AstNode left = Fator();
            return TermoOpc(left);
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode TermoOpc(AstNode left) {
        if (token.getType() == TokenType.MULT ||
                token.getType() == TokenType.DIV) {
            String op = OpMult();
            AstNode right = Fator();
            AritOpNode node = new AritOpNode(op);
            node.addChild(left);
            node.addChild(right);
            return TermoOpc(node);
        }
        return left;
    }

    private String OpMult() {
        if (token.getType() == TokenType.MULT) {
            match(TokenType.MULT);
            return "*";
        } else if (token.getType() == TokenType.DIV) {
            match(TokenType.DIV);
            return "/";
        } else {
            PrintError();
            return "";
        }
    }

    private AstNode Fator() {
        if (token.getType() == TokenType.ID) {
            String idName = token.getLexeme();
            match(TokenType.ID);
            AstNode call = ChamadaFuncao(idName);
            if (call != null) {
                return call;
            } else {
                // Variable usage check
                if (symbolManager.lookup(idName) == null) {
                    semanticErrors
                            .add(new SemanticError("Variable " + idName + " not declared.", token.getLineNumber()));
                }
                return new IdNode(idName);
            }
        } else if (token.getType() == TokenType.INT_CONST) {
            int val = Integer.parseInt(token.getLexeme());
            match(TokenType.INT_CONST);
            return new IntConstNode(val);
        } else if (token.getType() == TokenType.FLOAT_CONST) {
            float val = Float.parseFloat(token.getLexeme());
            match(TokenType.FLOAT_CONST);
            return new FloatConstNode(val);
        } else if (token.getType() == TokenType.CHAR_LITERAL) {
            char val = token.getLexeme().charAt(1); // 'c' -> c
            match(TokenType.CHAR_LITERAL);
            return new CharConstNode(val);
        } else if (token.getType() == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            AstNode expr = Expr();
            match(TokenType.RBRACKET);
            return expr;
        } else {
            PrintError();
            return null;
        }
    }

    private AstNode ChamadaFuncao(String funcName) {
        if (token.getType() == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            List<AstNode> args = ListaArgs();
            match(TokenType.RBRACKET);

            // Add function call to symbol table
            List<String> strArgs = new ArrayList<>();
            for (AstNode node : args) {
                if (node instanceof IdNode)
                    strArgs.add(((IdNode) node).getName());
                else if (node instanceof IntConstNode)
                    strArgs.add(String.valueOf(((IntConstNode) node).getValue()));
                else if (node instanceof FloatConstNode)
                    strArgs.add(String.valueOf(((FloatConstNode) node).getValue()));
                else if (node instanceof CharConstNode)
                    strArgs.add("'" + ((CharConstNode) node).getValue() + "'");
                else
                    strArgs.add("expr");
            }
            symbolManager.addFunctionCall(funcName, strArgs);

            CallNode call = new CallNode(funcName);
            for (AstNode arg : args)
                call.addChild(arg);
            return call;
        }
        return null;
    }

    private List<AstNode> ListaArgs() {
        List<AstNode> args = new ArrayList<>();
        if (token.getType() == TokenType.ID ||
                token.getType() == TokenType.INT_CONST ||
                token.getType() == TokenType.FLOAT_CONST ||
                token.getType() == TokenType.CHAR_LITERAL) {
            args.add(Arg());
            args.addAll(ListaArgs2());
        }
        return args;
    }

    private List<AstNode> ListaArgs2() {
        List<AstNode> args = new ArrayList<>();
        if (token.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            args.add(Arg());
            args.addAll(ListaArgs2());
        }
        return args;
    }

    private AstNode Arg() {
        AstNode node = null;
        if (token.getType() == TokenType.ID) {
            String value = token.getLexeme();
            match(TokenType.ID);
            AstNode call = ChamadaFuncao(value);
            if (call != null) {
                node = call;
            } else {
                if (symbolManager.lookup(value) == null) {
                    semanticErrors
                            .add(new SemanticError("Variable " + value + " not declared.", token.getLineNumber()));
                }
                node = new IdNode(value);
            }
        } else if (token.getType() == TokenType.INT_CONST) {
            int val = Integer.parseInt(token.getLexeme());
            match(TokenType.INT_CONST);
            node = new IntConstNode(val);
        } else if (token.getType() == TokenType.FLOAT_CONST) {
            float val = Float.parseFloat(token.getLexeme());
            match(TokenType.FLOAT_CONST);
            node = new FloatConstNode(val);
        } else if (token.getType() == TokenType.CHAR_LITERAL) {
            char val = token.getLexeme().charAt(1);
            match(TokenType.CHAR_LITERAL);
            node = new CharConstNode(val);
        } else {
            PrintError();
        }
        return node;
    }
}
