package org.ast;

import org.symbol_table.DataType;

public class CastNode extends AstNode {
    private DataType targetType;

    public CastNode(DataType targetType, AstNode child) {
        super("CastNode");
        this.targetType = targetType;
        this.addChild(child);
        this.setDataType(targetType);
    }

    public DataType getTargetType() {
        return targetType;
    }
}
