package SynTree;

import CMMVM.Bytecode;
import Lexer.Tag;

import java.util.ArrayList;

public class WriteStmtNode extends SNode {

    private NnaryExprNode expression;

    public WriteStmtNode() {
        super(Tag.WRITESTMT);
        expression = null;
    }

    public void setExpression(NnaryExprNode expression) {
        this.expression = expression;
    }

    @Override
    public void checkAndBuild() {
        expression.setCurrentEnv(currentEnv);
        expression.checkAndBuild();
    }

    @Override
    public void visit() {

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
        System.out.println("WriteStmt");
        expression.traverse(blank + 1);
    }

    @Override
    public void genBytecode(ArrayList<Bytecode> prog) {

    }

}
