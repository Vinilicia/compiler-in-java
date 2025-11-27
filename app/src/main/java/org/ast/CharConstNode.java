package org.ast;

public class CharConstNode extends AstNode {
    private char value;

    public CharConstNode(char value) {
        super("CharConstNode");
        this.value = value;
    }

    public char getValue() {
        return value;
    }
}
