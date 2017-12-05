package CMMVM;

import java.util.ArrayList;
import java.util.Stack;

public class ActRecord {
    
    Stack<Operand> opdStack;
    ArrayList<Operand> localVariables;

    public ActRecord() {
        this.opdStack = new Stack<Operand>();
        this.localVariables = new ArrayList<Operand>();
    }

    public void pushOpd(Operand opd) {
        opdStack.push(opd);
    }

    public Operand popOpd(Operand opd) {
        return opdStack.pop();
    }

    // get an operand from the local variable area
    public Operand getLocalVal(int i) {
        return localVariables.get(i);
    }

}
