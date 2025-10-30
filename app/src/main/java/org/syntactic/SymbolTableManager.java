package org.syntactic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

            System.out.println(
                "Tabelas exportadas para o arquivo: " + filename
            );
        } catch (IOException e) {
            System.err.println("Erro ao salvar as tabelas: " + e.getMessage());
        }
    }
}
