package org.syntactic;

import java.util.ArrayList;
import java.util.List;

public class FunctionRegister {

    private String name;
    private int numArgs;
    private List<String> args;

    public FunctionRegister(String name) {
        this.name = name;
        this.args = new ArrayList<>();
        this.numArgs = 0;
    }

    public void addArg(String arg) {
        args.add(arg);
        numArgs = args.size();
    }

    public String getName() {
        return name;
    }

    public int getNumArgs() {
        return numArgs;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return String.format(
            "FunctionRegister{name='%s', numArgs=%d, args=%s}",
            name,
            numArgs,
            args
        );
    }
}
