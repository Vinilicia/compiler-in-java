package org.ast;

import java.util.ArrayList;
import java.util.List;
import org.symbol_table.DataType;

public abstract class AstNode {
    protected String nodeType;
    protected List<AstNode> children;
    protected DataType dataType;
    protected String op;

    public AstNode(String nodeType) {
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
        this.dataType = null;
        this.op = null;
    }

    public void addChild(AstNode child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    public String getNodeType() {
        return nodeType;
    }

    public List<AstNode> getChildren() {
        return children;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String toString() {
        return nodeType;
    }
}
