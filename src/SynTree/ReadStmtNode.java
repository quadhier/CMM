package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Lexer.Tag;

import java.util.ArrayList;

public class ReadStmtNode extends SNode {


    private NnaryExprNode leftValueExpression;

    public ReadStmtNode() {
        super(Tag.READSTMT);
        leftValueExpression = null;
    }

    public void setLeftValueExpression(NnaryExprNode leftValueExpression) {
        this.leftValueExpression = leftValueExpression;
    }

    @Override
    public void checkAndBuild() {
        leftValueExpression.setCurrentEnv(currentEnv);
        leftValueExpression.checkAndBuild();
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
        System.out.println("ReadStmt");
        leftValueExpression.traverse(blank + 1);
    }


    @Override
    public void genBytecode(Program program) {

        leftValueExpression.genBytecode(program);

        if(leftValueExpression.getTag() == Tag.ARRLEXPR) {

            if(leftValueExpression.getDataType() == Tag.DOUBLE) {
                program.addCode(Opcode.dread);
                program.addCode(Opcode.dastore);
            } else {
                program.addCode(Opcode.iread);
                program.addCode(Opcode.iastore);
            }

        } else if(leftValueExpression.getTag() == Tag.VARLEXPR) {

            if(leftValueExpression.getDataType() == Tag.DOUBLE) {
                program.addCode(Opcode.dread);
                program.addCode(Opcode.dstore);
            } else {
                program.addCode(Opcode.iread);
                program.addCode(Opcode.istore);
            }

        }

    }

}
