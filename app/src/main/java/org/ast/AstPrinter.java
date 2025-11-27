package org.ast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AstPrinter {

    public void print(List<FunctionNode> functions, String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (FunctionNode func : functions) {
                printNode(func, "", out);
                out.println();
            }
        } catch (IOException e) {
            System.err.println("Error writing AST: " + e.getMessage());
        }
    }

    private void printNode(AstNode node, String indent, PrintWriter out) {
        if (node == null)
            return;

        out.print(indent + node.getNodeType());

        if (node instanceof FunctionNode) {
            out.print(" (Name: " + ((FunctionNode) node).getFunctionName() + ")");
        } else if (node instanceof IdNode) {
            out.print(" (Name: " + ((IdNode) node).getName() + ")");
        } else if (node instanceof IntConstNode) {
            out.print(" (Value: " + ((IntConstNode) node).getValue() + ")");
        } else if (node instanceof FloatConstNode) {
            out.print(" (Value: " + ((FloatConstNode) node).getValue() + ")");
        } else if (node instanceof CharConstNode) {
            out.print(" (Value: " + ((CharConstNode) node).getValue() + ")");
        } else if (node instanceof RelOpNode) {
            out.print(" (Op: " + node.getOp() + ")");
        } else if (node instanceof AritOpNode) {
            out.print(" (Op: " + node.getOp() + ")");
        } else if (node instanceof CallNode) {
            out.print(" (Func: " + ((CallNode) node).getFunctionName() + ")");
        } else if (node instanceof CastNode) {
            out.print(" (Target: " + ((CastNode) node).getTargetType() + ")");
        }

        if (node.getDataType() != null) {
            out.print(" [Type: " + node.getDataType() + "]");
        }

        out.println();

        for (AstNode child : node.getChildren()) {
            printNode(child, indent + "  ", out);
        }
    }
}
