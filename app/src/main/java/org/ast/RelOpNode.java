package org.ast;

public class RelOpNode extends AstNode {
    public RelOpNode(String op) {
        super("RelOp");
        this.op = op;
    }
}
