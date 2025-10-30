package org.symbol_table;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTableManager {

    private Map<String, SymbolTable> tables;
    private SymbolTable currentTable;
    private int paramPos = 0;

    public SymbolTableManager() {
        tables = new HashMap<>();
    }

    public SymbolTable getTable(String functionName) {
        return tables.get(functionName);
    }

    public void printAllTables() {
        for (SymbolTable t : tables.values()) {
            t.printTable();
        }
    }

    public void createFunctionTable(String name, DataType retType) {
        currentTable = new SymbolTable(name, retType);
        tables.put(name, currentTable);
        paramPos = 0;
    }

    public SymbolTable getCurrentTable() {
        return currentTable;
    }

    public void closeCurrentTable() {
        currentTable = null;
    }

    public void addParam(String name, DataType type) {
        if (currentTable != null) {
            currentTable.addSymbol(
                new TableEntry(name, type, true, paramPos++)
            );
        }
    }

    public void addVariable(String name, DataType type) {
        if (currentTable != null) {
            currentTable.addSymbol(new TableEntry(name, type, false, -1));
        }
    }

    public void addFunctionCall(String funcName, List<String> args) {
        if (currentTable != null) {
            FunctionRegister call = new FunctionRegister(funcName);
            for (String arg : args) call.addArg(arg);
            TableEntry record = currentTable.lookup(funcName);
            if (record == null) {
                record = new TableEntry(funcName, DataType.VOID, false, -1);
                currentTable.addSymbol(record);
            }
            record.addCall(call);
        }
    }

    public void exportAllTablesToFile(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (SymbolTable table : tables.values()) {
                out.println(
                    "Tabela de símbolos para a função: " +
                        table.getFunctionName()
                );
                out.printf(
                    "%-10s %-10s %-10s %-10s %-10s%n",
                    "chave",
                    "name",
                    "datatype",
                    "is_param",
                    "pos_param"
                );

                for (TableEntry record : table.getAllSymbols()) {
                    out.println(record);
                }

                out.println(
                    "ret_type(" +
                        table.getFunctionName() +
                        "): " +
                        table.getReturnType()
                );
                out.println();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar as tabelas: " + e.getMessage());
        }
    }
}
