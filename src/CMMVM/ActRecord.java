package CMMVM;

import java.util.ArrayList;
import java.util.Stack;

public class ActRecord {
    
    Stack<Operand> opdStack;
    Operand[] localVariables;

    public ActRecord(int slotSize) {
        this.opdStack = new Stack<Operand>();
        this.localVariables = new Operand[slotSize];
    }

    public void pushOpd(Operand opd) {
        opdStack.push(opd);
    }

    public Operand popOpd() {
        return opdStack.pop();
    }

    // get an operand from the local variable area
    public Operand getLocalVal(int idx) {
        return localVariables[idx];
    }

    public void storeLocalVal(int idx, Operand opd) {
        localVariables[idx] = opd;
    }

    public int storeLocalArrEle(int arrIdx, int eleIdx, int val) {
        int[] iarr = localVariables[arrIdx].getIntArr();
        if(eleIdx < iarr.length) {
            iarr[eleIdx] = val;
            return 0;
        } else {
            return -1;
        }
    }

    public int storeLocalArrEle(int arrIdx, int eleIdx, double val) {
        double[] darr = localVariables[arrIdx].getDoubleArr();
        if(eleIdx < darr.length) {
            darr[eleIdx] = val;
            return 0;
        } else {
            return -1;
        }
    }

}
