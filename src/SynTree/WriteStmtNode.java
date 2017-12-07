package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
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
        if (currentEnv.isContinueLoop()||currentEnv.isBreakLoop())
            return;
        System.out.println(expression.getValue());
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
    public void genBytecode(Program program) {
        expression.genBytecode(program);
        if(expression.getDataType() == Tag.DOUBLE) {
            program.addCode(Opcode.dwrite);
        } else {
            program.addCode(Opcode.iwrite);
        }
    }

}
