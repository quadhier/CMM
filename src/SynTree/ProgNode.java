package SynTree;

import CMMVM.Bytecode;
import CMMVM.Program;
import Lexer.Tag;

import java.util.ArrayList;


// program
public class ProgNode extends SNode {

    private ArrayList<StmtNode> statements;

    public ProgNode() {
        super(Tag.PROG);
        statements = new ArrayList<>();
    }

    public void addStatement(StmtNode statement) {
        statements.add(statement);
    }

    @Override
    public void checkAndBuild() {
        for(StmtNode stmtNode : statements) {
            stmtNode.setCurrentEnv(currentEnv);
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
        System.out.println("Prog");

        for(StmtNode stmtNode : statements) {
            stmtNode.traverse(blank + 1);
        }
    }

    @Override
    public void genBytecode(Program program) {

    }

}
