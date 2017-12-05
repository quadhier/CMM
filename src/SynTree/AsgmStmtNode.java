package SynTree;

import CMMVM.Bytecode;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;

import java.util.ArrayList;


// assignment-expression
public class AsgmStmtNode extends SNode {

    private NnaryExprNode leftValueExpression;
    private Token assignmentOperator;
    private NnaryExprNode expression;

    public AsgmStmtNode() {
        super(Tag.ASGMSTMT);
        leftValueExpression = null;
        assignmentOperator = null;
        expression = null;
    }

    public void setLeftValueExpression(NnaryExprNode leftValueExpression) {
        this.leftValueExpression = leftValueExpression;
    }

    public void setAssignmentOperator(Token assignmentOperator) {
        this.assignmentOperator = assignmentOperator;
    }

    public void setExpression(NnaryExprNode expression) {
        this.expression = expression;
    }

    @Override
    public void checkAndBuild() {
        leftValueExpression.setCurrentEnv(currentEnv);
        expression.setCurrentEnv(currentEnv);

        leftValueExpression.checkAndBuild();
        expression.checkAndBuild();

        // check if the dataType of the left-value-expression is
        // consistent with the dataType of the RHS expression
        Identifer idt = leftValueExpression.getIdentifier();
        int lDataType = leftValueExpression.getDataType();
        int rDataType = expression.getDataType();//
        // double to int cast warning
        if(lDataType == Tag.INT && rDataType == Tag.DOUBLE) {
            Failure.addFailure(SynTree.getFilepath(), idt.getLine(), idt.getStartpos(),Failure.WARNING, "implicityly cast double to int");
        }
        // double or int to bool converting error, and vice versa
        // note that identifier in the expression may not be declared
        if(lDataType == Tag.BOOL && (rDataType == Tag.INT || rDataType == Tag.DOUBLE)) {
            Failure.addFailure(SynTree.getFilepath(), idt.getLine(), idt.getStartpos(), Failure.ERROR,
                    "inconsistent type, cannot assign " + (rDataType == Tag.INT ? "int" : "double") + " value to bool variable");
        } else if(rDataType == Tag.BOOL && (lDataType == Tag.INT || lDataType == Tag.DOUBLE)) {
            Failure.addFailure(SynTree.getFilepath(), idt.getLine(), idt.getStartpos(), Failure.ERROR,
                    "inconsistent type, cannot assign bool value to " + (lDataType == Tag.INT ? "int" : "double") + " variable");
        }

        // unable to do boundary check in the compile time

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
        System.out.println("AsgmStmt " + assignmentOperator.getLexeme());
        leftValueExpression.traverse(blank + 1);
        expression.traverse(blank + 1);
    }

    @Override
    public void genBytecode(ArrayList<Bytecode> prog) {



    }
}
