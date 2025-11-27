package org.ast;

public class FunctionNode extends AstNode {
    private String functionName;
    private String returnTypeStr;

    public FunctionNode(String functionName, String returnTypeStr) {
        super("FunctionNode");
        this.functionName = functionName;
        this.returnTypeStr = returnTypeStr;
    }

    public String getFunctionName() {
        return functionName;
    }
}
