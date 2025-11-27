package org.ast;

public class IdNode extends AstNode {
    private String name;

    public IdNode(String name) {
        super("IdNode");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
