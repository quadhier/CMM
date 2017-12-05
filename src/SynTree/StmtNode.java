package SynTree;

import CMMVM.Bytecode;
import Lexer.Tag;

import java.util.ArrayList;


// statement
public class StmtNode extends SNode {

    private SNode child;

    public StmtNode() {
        super(Tag.STMT);
    }

    public SNode getChild() {
        return child;
    }

    public void setChild(SNode child) {
        this.child = child;
    }

    @Override
    public void checkAndBuild() {
        child.setCurrentEnv(currentEnv);
        child.checkAndBuild();
    }

    @Override
    public void visit() {
        child.visit();
    }

    @Override
    public void traverse(int blank) {
        if(blank > 0)
            System.out.print(" |");
        for(int i = 0 ; i < blank; i++) {
            if(i == blank - 1)
                System.out.print("---");
            else
                System.out.print("    |");
        }
        System.out.println("Stmt");
        if(child != null)
            child.traverse(blank + 1);
    }


    @Override
    public void genBytecode(ArrayList<Bytecode> prog, int currentOpdIdx, ArrayList<Object> constantPool) {

    }

}
