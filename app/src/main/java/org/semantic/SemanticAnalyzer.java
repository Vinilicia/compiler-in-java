package org.semantic;

import java.util.ArrayList;
import java.util.List;
import org.ast.*;
import org.symbol_table.*;
import org.syntactic.SemanticError;

public class SemanticAnalyzer {
    private SymbolTableManager symbolManager;
    private List<SemanticError> errors;
    private SymbolTable currentTable;

    public SemanticAnalyzer(SymbolTableManager symbolManager) {
        this.symbolManager = symbolManager;
        this.errors = new ArrayList<>();
    }

    public List<SemanticError> analyze(List<FunctionNode> functions) {
        for (FunctionNode func : functions) {
            visit(func);
        }
        return errors;
    }

    private void visit(AstNode node) {
        if (node == null)
            return;

        if (node instanceof FunctionNode) {
            visitFunction((FunctionNode) node);
        } else if (node instanceof BlocoNode) {
            visitBloco((BlocoNode) node);
        } else if (node instanceof AssignNode) {
            visitAssign((AssignNode) node);
        } else if (node instanceof IfNode) {
            visitIf((IfNode) node);
        } else if (node instanceof WhileNode) {
            visitWhile((WhileNode) node);
        } else if (node instanceof PrintNode) {
            visitPrint((PrintNode) node);
        } else if (node instanceof ReturnNode) {
            visitReturn((ReturnNode) node);
        } else if (node instanceof CallNode) {
            visitCall((CallNode) node);
        } else if (node instanceof RelOpNode) {
            visitRelOp((RelOpNode) node);
        } else if (node instanceof AritOpNode) {
            visitAritOp((AritOpNode) node);
        } else if (node instanceof IdNode) {
            visitId((IdNode) node);
        } else if (node instanceof IntConstNode) {
            node.setDataType(DataType.INT);
        } else if (node instanceof FloatConstNode) {
            node.setDataType(DataType.FLOAT);
        } else if (node instanceof CharConstNode) {
            node.setDataType(DataType.CHAR);
        }
    }

    private void visitFunction(FunctionNode node) {
        currentTable = symbolManager.getTable(node.getFunctionName());
        if (currentTable == null) {
            errors.add(new SemanticError("Symbol table not found for function " + node.getFunctionName(), 0));
            return;
        }

        for (AstNode child : node.getChildren()) {
            visit(child);
        }
    }

    private void visitBloco(BlocoNode node) {
        for (AstNode child : node.getChildren()) {
            visit(child);
        }
    }

    private void visitAssign(AssignNode node) {
        IdNode id = (IdNode) node.getChildren().get(0);
        AstNode expr = node.getChildren().get(1);

        visit(id);
        visit(expr);

        if (id.getDataType() == null || expr.getDataType() == null)
            return;

        if (id.getDataType() == DataType.FLOAT && expr.getDataType() == DataType.INT) {
            // Coercion
            CastNode cast = new CastNode(DataType.FLOAT, expr);
            node.getChildren().set(1, cast);
            expr = cast;
        } else if (id.getDataType() != expr.getDataType()) {
            errors.add(new SemanticError("Type mismatch in assignment to " + id.getName() + ". Expected "
                    + id.getDataType() + ", got " + expr.getDataType(), 0));
        }
    }

    private void visitIf(IfNode node) {
        AstNode cond = node.getChildren().get(0);
        visit(cond);

        for (int i = 1; i < node.getChildren().size(); i++) {
            visit(node.getChildren().get(i));
        }
    }

    private void visitWhile(WhileNode node) {
        AstNode cond = node.getChildren().get(0);
        visit(cond);

        for (int i = 1; i < node.getChildren().size(); i++) {
            visit(node.getChildren().get(i));
        }
    }

    private void visitPrint(PrintNode node) {
        for (AstNode child : node.getChildren()) {
            visit(child);
        }
    }

    private void visitReturn(ReturnNode node) {
        AstNode expr = node.getChildren().get(0);
        visit(expr);

        DataType retType = currentTable.getReturnType();
        if (retType == DataType.FLOAT && expr.getDataType() == DataType.INT) {
            CastNode cast = new CastNode(DataType.FLOAT, expr);
            node.getChildren().set(0, cast);
            expr = cast;
        } else if (retType != expr.getDataType()) {
            errors.add(
                    new SemanticError("Return type mismatch. Expected " + retType + ", got " + expr.getDataType(), 0));
        }
    }

    private void visitCall(CallNode node) {
        SymbolTable funcTable = symbolManager.getTable(node.getFunctionName());
        if (funcTable == null) {
            errors.add(new SemanticError("Function " + node.getFunctionName() + " not defined.", 0));
            node.setDataType(DataType.ERROR);
            return;
        }

        node.setDataType(funcTable.getReturnType());

        List<AstNode> args = node.getChildren();
        List<TableEntry> params = new ArrayList<>();
        for (TableEntry entry : funcTable.getAllSymbols()) {
            if (entry.isParam()) {
                params.add(entry);
            }
        }

        params.sort((p1, p2) -> Integer.compare(p1.getPosParam(), p2.getPosParam()));

        if (args.size() != params.size()) {
            errors.add(new SemanticError("Incorrect number of arguments for function " + node.getFunctionName()
                    + ". Expected " + params.size() + ", got " + args.size(), 0));
            return;
        }

        for (int i = 0; i < args.size(); i++) {
            AstNode arg = args.get(i);
            TableEntry param = params.get(i);

            visit(arg);

            if (arg.getDataType() == DataType.INT && param.getDatatype() == DataType.FLOAT) {
                CastNode cast = new CastNode(DataType.FLOAT, arg);
                node.getChildren().set(i, cast);
            } else if (arg.getDataType() != param.getDatatype()) {
                errors.add(new SemanticError("Type mismatch in argument " + (i + 1) + " of function "
                        + node.getFunctionName() + ". Expected " + param.getDatatype() + ", got " + arg.getDataType(),
                        0));
            }
        }
    }

    private void visitRelOp(RelOpNode node) {
        AstNode left = node.getChildren().get(0);
        AstNode right = node.getChildren().get(1);
        visit(left);
        visit(right);

        if (left.getDataType() == DataType.INT && right.getDataType() == DataType.FLOAT) {
            CastNode cast = new CastNode(DataType.FLOAT, left);
            node.getChildren().set(0, cast);
            left = cast;
        } else if (left.getDataType() == DataType.FLOAT && right.getDataType() == DataType.INT) {
            CastNode cast = new CastNode(DataType.FLOAT, right);
            node.getChildren().set(1, cast);
            right = cast;
        }

        node.setDataType(DataType.INT);
    }

    private void visitAritOp(AritOpNode node) {
        AstNode left = node.getChildren().get(0);
        AstNode right = node.getChildren().get(1);
        visit(left);
        visit(right);

        if (left.getDataType() == DataType.FLOAT || right.getDataType() == DataType.FLOAT) {
            node.setDataType(DataType.FLOAT);
            if (left.getDataType() == DataType.INT) {
                CastNode cast = new CastNode(DataType.FLOAT, left);
                node.getChildren().set(0, cast);
            }
            if (right.getDataType() == DataType.INT) {
                CastNode cast = new CastNode(DataType.FLOAT, right);
                node.getChildren().set(1, cast);
            }
        } else {
            node.setDataType(DataType.INT);
        }
    }

    private void visitId(IdNode node) {
        TableEntry entry = currentTable.lookup(node.getName());
        if (entry != null) {
            node.setDataType(entry.getDatatype());
        } else {
            node.setDataType(DataType.ERROR);
            // Error already reported in Syntactic phase
            // Syntactic phase checks undeclared variables.
        }
    }
}
