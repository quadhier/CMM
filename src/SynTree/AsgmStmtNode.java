package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;
import sun.dc.pr.PRError;

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
    public void genBytecode(Program program) {

        // generate bytecode to calculate the operand index and array element index if any
        // and push them onto the stack
        int lValTag = leftValueExpression.getTag();
        Token lValIdt = leftValueExpression.getIdentifier();
        Symbol symbol = currentEnv.get(lValIdt.getLexeme());
        int dataType = symbol.getDataType();
        int opdIdx = symbol.getOpdIdx();
        ArrayList<NnaryExprNode> elemIndex = leftValueExpression.getChildExpressions();
        ArrayList<NnaryExprNode> dimLengths = symbol.getDimLengths();

        if(assignmentOperator.getTag() != '=') {

            if(lValTag == Tag.ARRLEXPR) {
                // create a tmp variable to store the array element index
                int tmpIdx = program.getCurrentOpdInx();
                program.createVal();
                program.addCode(Opcode.ipush, tmpIdx);

                // start : generate code to calculate element index
                int dimIdx = program.getCurrentOpdInx();
                program.createVal();
                elemIndex.get(elemIndex.size() - 1).genBytecode(program);
                program.addCode(Opcode.ipush, dimIdx);
                program.addCode(Opcode.iconst_1);
                program.addCode(Opcode.istore);
                for(int i = elemIndex.size() - 2; i >=0; i--) {
                    // load previously stored dimension length product
                    // multiply it by the current element index and store it back
                    program.addCode(Opcode.ipush, dimIdx); // for load again
                    program.addCode(Opcode.ipush, dimIdx); // for store
                    program.addCode(Opcode.ipush, dimIdx); // for load
                    program.addCode(Opcode.iload);
                    dimLengths.get(i + 1).genBytecode(program);
                    program.addCode(Opcode.mul); // calculate reused product
                    program.addCode(Opcode.istore);
                    program.addCode(Opcode.iload);
                    elemIndex.get(i).genBytecode(program);
                    program.addCode(Opcode.mul); // element index * previously stored product
                    program.addCode(Opcode.add);
                }
                program.removeVal(); // remove dimIdx
                // end : now the stack top is the element index with below unchanged

                // store the array element index
                // in the local variable area at tmpIdx
                program.addCode(Opcode.istore);


                // store the array index and element index twice
                // for load
                program.addCode(Opcode.ipush, tmpIdx);
                program.addCode(Opcode.iload);
                program.addCode(Opcode.ipush, opdIdx);
                // for store
                program.addCode(Opcode.ipush, tmpIdx);
                program.addCode(Opcode.iload);
                program.addCode(Opcode.ipush, opdIdx);

                program.removeVal(); // remove tmpIdx

                // load the value of the left-value-expression onto the operand stack
                if (dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.daload);
                } else {
                    program.addCode(Opcode.iaload);
                }

                expression.genBytecode(program);

                switch (assignmentOperator.getTag()) {
                    case Tag.PLASN:
                        program.addCode(Opcode.add);
                        break;
                    case Tag.MIASN:
                        program.addCode(Opcode.sub);
                        break;
                    case Tag.MLASN:
                        program.addCode(Opcode.mul);
                        break;
                    case Tag.QTASN:
                        program.addCode(Opcode.div);
                        break;
                    case Tag.RDASN:
                        program.addCode(Opcode.rem);
                        break;
                }

                if (dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dastore);
                } else {
                    program.addCode(Opcode.iastore);
                }

            } else { // Tag.VARLEXPR

                // for load
                program.addCode(Opcode.ipush, opdIdx);
                // for store
                program.addCode(Opcode.ipush, opdIdx);

                if (dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dload);
                } else {
                    program.addCode(Opcode.iload);
                }

                expression.genBytecode(program);

                switch (assignmentOperator.getTag()) {
                    case Tag.PLASN:
                        program.addCode(Opcode.add);
                        break;
                    case Tag.MIASN:
                        program.addCode(Opcode.sub);
                        break;
                    case Tag.MLASN:
                        program.addCode(Opcode.mul);
                        break;
                    case Tag.QTASN:
                        program.addCode(Opcode.div);
                        break;
                    case Tag.RDASN:
                        program.addCode(Opcode.rem);
                        break;
                }

                if (dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dstore);
                } else {
                    program.addCode(Opcode.istore);
                }
            }

        } else { // '='

            if(lValTag == Tag.ARRLEXPR) {

                // start : generate code to calculate element index
                int dimIdx = program.getCurrentOpdInx();
                program.createVal();
                elemIndex.get(elemIndex.size() - 1).genBytecode(program);
                program.addCode(Opcode.ipush, dimIdx);
                program.addCode(Opcode.iconst_1);
                program.addCode(Opcode.istore);
                for(int i = elemIndex.size() - 2; i >=0; i--) {
                    // load previously stored dimension length product
                    // multiply it by the current element index and store it back
                    program.addCode(Opcode.ipush, dimIdx); // for load again
                    program.addCode(Opcode.ipush, dimIdx); // for store
                    program.addCode(Opcode.ipush, dimIdx); // for load
                    program.addCode(Opcode.iload);
                    dimLengths.get(i + 1).genBytecode(program);
                    program.addCode(Opcode.mul); // calculate reused product
                    program.addCode(Opcode.istore);
                    program.addCode(Opcode.iload);
                    elemIndex.get(i).genBytecode(program);
                    program.addCode(Opcode.mul); // element index * previously stored product
                    program.addCode(Opcode.add);
                }
                program.removeVal();
                // end : now the stack top is the element index with below unchanged

                program.addCode(Opcode.ipush, opdIdx);
                expression.genBytecode(program);

                if(dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dastore);
                } else {
                    program.addCode(Opcode.iastore);
                }
            } else {

                program.addCode(Opcode.ipush, opdIdx);
                expression.genBytecode(program);

                if(dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dstore);
                } else {
                    program.addCode(Opcode.istore);
                }
            }


        }

    }
}
