package org.ast;

public class IntConstNode extends AstNode {
    private int value;

    public IntConstNode(int value) {
        super("IntConstNode");
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
