package SynTree;


import CMMVM.Bytecode;
import CMMVM.Program;
import Lexer.Tag;
import SymTable.Env;

import java.util.ArrayList;

// compound-statement
public class CompStmtNode extends SNode {

    private ArrayList<StmtNode> statements;


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

    @Override
    public void checkAndBuild() {
        // inside a compound statement, a new environment should be created
        Env newEnv = new Env(currentEnv);
        for(StmtNode stmtNode : statements) {
            stmtNode.setCurrentEnv(newEnv);
            stmtNode.checkAndBuild();
        }
    }

    @Override
    public void visit() {
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
