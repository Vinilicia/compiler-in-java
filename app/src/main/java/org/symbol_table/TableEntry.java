package org.symbol_table;

import java.util.ArrayList;
import java.util.List;

public class TableEntry {

    String name;
    DataType datatype;
    boolean isParam;
    int posParam;
    List<FunctionRegister> callRefs;

    public TableEntry(
        String name,
        DataType datatype,
        boolean isParam,
        int posParam
    ) {
        this.name = name;
        this.datatype = datatype;
        this.isParam = isParam;
        this.posParam = posParam;
        this.callRefs = new ArrayList<>();
    }

    public void addCall(FunctionRegister call) {
        callRefs.add(call);
    }

    @Override
    public String toString() {
        return String.format(
            "%-10s %-10s %-10s %-10d %s",
            name,
            datatype,
            isParam,
            posParam,
            callRefs.isEmpty() ? "NULL" : callRefs.toString()
        );
    }
}
