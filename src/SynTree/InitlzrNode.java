package SynTree;

import CMMVM.Bytecode;
import CMMVM.Program;
import Lexer.Identifer;
import Lexer.Tag;

import java.util.ArrayList;

public class InitlzrNode extends SNode {

    private Identifer identifer;
    private NnaryExprNode expression;


    public InitlzrNode() {
        super(Tag.INITLZR);
        identifer = null;
        expression = null;
    }

    public Identifer getIdentifer() {
        return identifer;
    }

    public NnaryExprNode getExpression() {
        return expression;
    }

    public void setIdentifer(Identifer identifer) {
        this.identifer = identifer;
    }

    public void setExpression(NnaryExprNode expression) {
        this.expression = expression;
    }

    @Override
    public void checkAndBuild() {
        if(expression != null) {
            expression.setCurrentEnv(currentEnv);
        }
    }

    @Override
    public void visit() {
        // do nothing
        // as visit() has been finished in the DeclNode. with enough context
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
        System.out.println("Initlzr " + identifer.getLexeme());
        if(expression != null) {
            expression.traverse(blank + 1);
        }
    }

    @Override
    public void genBytecode(Program program) {
        // finished in declaration
    }

}
