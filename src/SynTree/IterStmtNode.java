package SynTree;

import CMMVM.Bytecode;
import Failure.Failure;
import Lexer.Tag;

import java.util.ArrayList;


// iteration-statement
public class IterStmtNode extends SNode {

    private NnaryExprNode expression;
    private StmtNode statement;

    public IterStmtNode() {
        super(Tag.ITERSTMT);
        expression = null;
        statement = null;
    }

    public void setExpression(NnaryExprNode expression) {
        this.expression = expression;
    }

    public void setStatement(StmtNode statement) {
        this.statement = statement;
    }

    @Override
    public void checkAndBuild() {

        expression.setCurrentEnv(currentEnv);
        expression.checkAndBuild();
        if(expression.getDataType() != Tag.BOOL) {
            Failure.addFailure(SynTree.getFilepath(), expression.getStartLine(), expression.getStartPos(), Failure.ERROR,
                    "expected bool type expression");
        }
        currentEnv.setInLoop();
        statement.setCurrentEnv(currentEnv);
        statement.checkAndBuild();

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
        System.out.println("IterStmt");
        expression.traverse(blank + 1);
        statement.traverse(blank + 1);
    }


    @Override
    public void genBytecode(ArrayList<Bytecode> prog, int currentOpdIdx, ArrayList<Object> constantPool) {

    }

}
