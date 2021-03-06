package SynTree;

import CMMVM.Opcode;
import CMMVM.Program;
import Lexer.Tag;


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
        } else if(expression.getDataType() == Tag.INT) {
            program.addCode(Opcode.iwrite);
        } else {
            program.addCode(Opcode.bwrite);
        }
    }

}
