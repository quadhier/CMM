package SynTree;


import CMMVM.Bytecode;
import CMMVM.Program;
import Lexer.Tag;
import SymTable.Env;

import java.util.ArrayList;

// compound-statement
public class CompStmtNode extends SNode {

    private ArrayList<StmtNode> statements;

    private Env nextEnv;

    public CompStmtNode() {
        super(Tag.COMPSTMT);
        statements = null;
    }

    public void addStatement(StmtNode statement) {
        if(statements == null) {
            statements = new ArrayList<StmtNode>();
        }
        statements.add(statement);
    }

    public Env getNextEnv() {
        return nextEnv;
    }

    public void setContinueLoop(boolean continueLoop){
            nextEnv.setContinueLoop(continueLoop);
    }

    public void setBreakLoop(boolean breakLoop){
        nextEnv.setBreakLoop(breakLoop);
    }

    @Override
    public void checkAndBuild() {
        // inside a compound statement, a new environment should be created
        nextEnv = new Env(currentEnv);
        if (statements==null)
            return;
        for(StmtNode stmtNode : statements) {
            stmtNode.setCurrentEnv(nextEnv);
            stmtNode.checkAndBuild();
        }
    }

    @Override
    public void visit() {
        if (currentEnv.isContinueLoop()||currentEnv.isBreakLoop())
            return;
        for(StmtNode stmtNode: statements){
            stmtNode.visit();
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
        System.out.println("CompStmt");
        if(statements == null)
            return;
        for(StmtNode stmtNode : statements) {
            stmtNode.traverse(blank + 1);
        }

    }

    @Override
    public void genBytecode(Program program) {
        for (StmtNode stmtNode : statements) {
            stmtNode.genBytecode(program);
        }
    }

}
