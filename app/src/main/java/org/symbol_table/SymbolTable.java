package org.symbol_table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private String functionName;
    private DataType returnType;
    private Map<String, TableEntry> table;

    public SymbolTable(String functionName, DataType returnType) {
        this.functionName = functionName;
        this.returnType = returnType;
        this.table = new HashMap<>();
    }

    public void addSymbol(TableEntry symbol) {
        table.put(symbol.name, symbol);
    }

    public TableEntry lookup(String name) {
        return table.get(name);
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctioName(String name) {
        this.functionName = name;
    }

    public DataType getReturnType() {
        return returnType;
    }

    public void setReturnType(DataType type) {
        this.returnType = type;
    }

    public Collection<TableEntry> getAllSymbols() {
        return table.values();
    }

    public void printTable() {
        System.out.println("Tabela de símbolos para a função: " + functionName);
        System.out.printf(
            "%-10s %-10s %-10s %-10s %-10s%n",
            "chave",
            "name",
            "datatype",
            "is_param",
            "pos_param"
        );
        for (TableEntry r : table.values()) {
            System.out.println(r);
        }
        System.out.println("ret_type(" + functionName + "): " + returnType);
        System.out.println();
    }
}
