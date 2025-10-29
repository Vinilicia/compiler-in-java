package org.syntactic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableManager {

    private Map<String, SymbolTable> tables;

    public SymbolTableManager() {
        tables = new HashMap<>();
    }

    public void createFunctionTable(String functionName, DataType returnType) {
        tables.put(functionName, new SymbolTable(functionName, returnType));
    }

    public SymbolTable getTable(String functionName) {
        return tables.get(functionName);
    }

    public void printAllTables() {
        for (SymbolTable t : tables.values()) {
            t.printTable();
        }
    }
}
