package SynTree;

import CMMVM.Bytecode;
import CMMVM.Operand;
import SymTable.Env;

import java.util.ArrayList;

public abstract class SNode {

    protected int tag;
    protected Env currentEnv;

    public SNode(int tag) {
        this.tag = tag;
    }

    int getTag() {
        return tag;
    }

    public void setCurrentEnv(Env currentEnv) {
        this.currentEnv = currentEnv;
    }

    public abstract void visit();

    public abstract void checkAndBuild();

    public abstract void traverse(int blank);

    public abstract void genBytecode(ArrayList<Bytecode> prog, int currentOpdIdx, ArrayList<Object> constantPool);

}
