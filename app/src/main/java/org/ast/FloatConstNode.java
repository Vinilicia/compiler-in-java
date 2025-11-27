package org.ast;

public class FloatConstNode extends AstNode {
    private float value;

    public FloatConstNode(float value) {
        super("FloatConstNode");
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
