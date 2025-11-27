package org.ast;

public class CallNode extends AstNode {
    private String functionName;

    public CallNode(String functionName) {
        super("CallNode");
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}
