package SynTree;


import CMMVM.Bytecode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Env;

import java.util.ArrayList;

// jump-statement
public class JumpStmtNode extends SNode {

    private Token jumpType;

    public JumpStmtNode() {
        super(Tag.JUMPSTMT);
        jumpType = null;
    }

    public void setJumpType(Token jumpType) {
        this.jumpType = jumpType;
    }

    @Override
    public void checkAndBuild() {

        if(! currentEnv.isInLoop()) {
            Failure.addFailure(SynTree.getFilepath(), jumpType.getLine(), jumpType.getStartpos(), Failure.ERROR,
                    "'" + jumpType.getLexeme() + "' statement not in loop");
        }

    }

    @Override
    public void visit() {
        if (currentEnv.isContinueLoop()||currentEnv.isBreakLoop())
            return;
        Env t;
        switch (jumpType.getTag()){
            case Tag.CONTINUE:
                t =currentEnv;
                while (t.isInLoop()){
                    t.setContinueLoop(true);
                    if (t.getPrev()!=null)
                        t = t.getPrev();
                    else
                        break;
                }
                break;
            case Tag.BREAK:
                t =currentEnv;
                while (t.isInLoop()){
                    t.setBreakLoop(true);
                    if (t.getPrev()!=null)
                        t = t.getPrev();
                    else
                        break;
                }
                break;
        }
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
        System.out.println("JumStmt " + jumpType.getLexeme());
    }


    @Override
    public void genBytecode(Program program) {

    }

}
