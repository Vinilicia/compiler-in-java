package org.ast;

public class AritOpNode extends AstNode {
    public AritOpNode(String op) {
        super("AritOp");
        this.op = op;
    }
}
