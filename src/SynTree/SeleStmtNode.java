package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Tag;

import java.util.ArrayList;


// selection-statement
public class SeleStmtNode extends SNode {

    private NnaryExprNode expression;
    private StmtNode ifStatement;
    private StmtNode elseStatement;

    public SeleStmtNode() {
        super(Tag.SELESTMT);
        expression = null;
        ifStatement = null;
        elseStatement = null;
    }

    public void setExpression(NnaryExprNode expression) {
        this.expression = expression;
    }

    public void setIfStatement(StmtNode ifStatement) {
        this.ifStatement = ifStatement;
    }

    public void setElseStatement(StmtNode elseStatement) {
        this.elseStatement = elseStatement;
    }

    @Override
    public void checkAndBuild() {

        expression.setCurrentEnv(currentEnv);
        expression.checkAndBuild();
        if(expression.getDataType() != Tag.BOOL) {
            Failure.addFailure(SynTree.getFilepath(), expression.getStartLine(), expression.getStartPos(), Failure.ERROR,
                    "expected bool type expression");
        }
        ifStatement.setCurrentEnv(currentEnv);
        ifStatement.checkAndBuild();
        if(elseStatement != null) {
            elseStatement.setCurrentEnv(currentEnv);
            elseStatement.checkAndBuild();
        }
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
        System.out.println("SeleStmt");
        expression.traverse(blank + 1);
        ifStatement.traverse(blank + 1);
        if(elseStatement != null) {
            elseStatement.traverse(blank + 1);
        }
    }

    @Override
    public void genBytecode(Program program) {
        expression.genBytecode(program);
        int codeAddr1 = program.getCodeNum();
        program.addCode(Opcode.bez);
        ifStatement.genBytecode(program);
        int codeAddr2 = program.getCodeNum();
        program.addCode(Opcode.jmp);
        int targetAddr1 = program.getCodeNum();
        if(elseStatement != null) {
            elseStatement.genBytecode(program);
        }
        int targetAddr2 = program.getCodeNum();
        program.backpatch(codeAddr1, targetAddr1);
        program.backpatch(codeAddr2, targetAddr2);
    }

}
