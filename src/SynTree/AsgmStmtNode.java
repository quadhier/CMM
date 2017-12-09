package SynTree;

import CMMVM.Opcode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;

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

        // assignment operator except '=' cannot be applied to boolean type
        if(assignmentOperator.getTag() != '=' && (leftValueExpression.getDataType() == Tag.BOOL || expression.getDataType() == Tag.BOOL)) {
            Failure.addFailure(SynTree.getFilepath(), assignmentOperator.getLine(), assignmentOperator.getStartpos(), Failure.ERROR,
                    "cannot apply '" + assignmentOperator.getLexeme() + "' on bool variable");
        }

        // '%=' cannot be assigned to double
        if(assignmentOperator.getTag() == Tag.RDASN && (leftValueExpression.getDataType() == Tag.DOUBLE || expression.getDataType() == Tag.DOUBLE)) {
            Failure.addFailure(SynTree.getFilepath(), assignmentOperator.getLine(), assignmentOperator.getStartpos(), Failure.ERROR,
                    "cannot apply '" + assignmentOperator.getLexeme() + "' on double variable");
        }

        // unable to do boundary check in the compile time

    }

	@Override
	public void visit() {
    	if (currentEnv.isContinueLoop()||currentEnv.isBreakLoop())
    		return;
		switch (leftValueExpression.getDataType()) {
			case Tag.INT:
				switch (assignmentOperator.getTag()) {
					case '=':
					    // type should be decided here
                        if(expression.getDataType() == Tag.DOUBLE) {
                            leftValueExpression.setValue((int)(double)expression.getValue()); //synchronization in the symbol table is completed inside the setValue() function
                        } else if(expression.getDataType() == Tag.INT) {
                            leftValueExpression.setValue(expression.getValue());
                        } else {
                            leftValueExpression.setValue(expression.getValue());
                        }
						break;
					case Tag.PLASN:
						leftValueExpression.setValue((int) leftValueExpression.getValue() + (int) expression.getValue());
						break;
					case Tag.MIASN:
						leftValueExpression.setValue((int) leftValueExpression.getValue() - (int) expression.getValue());
						break;
					case Tag.MLASN:
						leftValueExpression.setValue((int) leftValueExpression.getValue() * (int) expression.getValue());
						break;
					case Tag.QTASN:
						if ((int) expression.getValue() == 0) {
							System.err.println("Runtime Error: divided by 0 on line " + expression.getStartLine() + ", position " + expression.getStartPos());
							System.exit(1);
						}
						leftValueExpression.setValue((int) leftValueExpression.getValue() / (int) expression.getValue());
						break;
					case Tag.RDASN:
						if ((int) expression.getValue() == 0) {
							System.err.println("Runtime Error: mod by 0 on line " + expression.getStartLine() + ", position " + expression.getStartPos());
							System.exit(1);
						}
						leftValueExpression.setValue((int) leftValueExpression.getValue() % (int) expression.getValue());
						break;
				}
				break;
			case Tag.DOUBLE:
				switch (assignmentOperator.getTag()) {
                    case '=':
                        // type should be decided here
                        if(expression.getDataType() == Tag.DOUBLE) {
                            leftValueExpression.setValue(expression.getValue()); //synchronization in the symbol table is completed inside the setValue() function
                        } else if(expression.getDataType() == Tag.INT) {
                            leftValueExpression.setValue((double)(int)expression.getValue());
                        } else {
                            leftValueExpression.setValue(expression.getValue());
                        }
                        break;
					case Tag.PLASN:
						leftValueExpression.setValue((double) (int) leftValueExpression.getValue() + (double) (int) expression.getValue());
						break;
					case Tag.MIASN:
						leftValueExpression.setValue((double) (int) leftValueExpression.getValue() - (double) (int) expression.getValue());
						break;
					case Tag.MLASN:
						leftValueExpression.setValue((double) (int) leftValueExpression.getValue() * (double) (int) expression.getValue());
						break;
					case Tag.QTASN:
//						if ((Integer) expression.getValue() == 0) {
//							System.err.println("Runtime Error: divided by 0 on line " + expression.getStartLine() + ", position " + expression.getStartPos());
//							System.exit(1);
//						}
						leftValueExpression.setValue((double) (int) leftValueExpression.getValue() / (double) (int) expression.getValue());
						break;

//					case '=':
//						leftValueExpression.setValue(expression.getValue()); //synchronization in the symbol table is completed inside the setValue() function
//						break;
//					case Tag.PLASN:
//						leftValueExpression.setValue((double) leftValueExpression.getValue() + (double) expression.getValue());
//						break;
//					case Tag.MIASN:
//						leftValueExpression.setValue((double) leftValueExpression.getValue() - (double) expression.getValue());
//						break;
//					case Tag.MLASN:
//						leftValueExpression.setValue((double) leftValueExpression.getValue() * (double) expression.getValue());
//						break;
//					case Tag.QTASN:
//						if ((Integer) expression.getValue() == 0) {
//							System.err.println("Runtime Error: divided by 0 on line " + expression.getStartLine() + ", position " + expression.getStartPos());
//							System.exit(1);
//						}
//						leftValueExpression.setValue((double) leftValueExpression.getValue() / (double) expression.getValue());
//						break;
				}
				break;
			case Tag.BOOL:
				leftValueExpression.setValue((boolean)expression.getValue());
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
        ArrayList<NnaryExprNode> elemIndexes = leftValueExpression.getChildExpressions();
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
                elemIndexes.get(elemIndexes.size() - 1).genBytecode(program);
                program.addCode(Opcode.ipush, dimIdx);
                program.addCode(Opcode.iconst_1);
                program.addCode(Opcode.istore);
                for(int i = elemIndexes.size() - 2; i >=0; i--) {
                    // load previously stored dimension length product
                    // multiply it by the current element index and store it back
                    program.addCode(Opcode.ipush, dimIdx); // for load again
                    program.addCode(Opcode.ipush, dimIdx); // for store
                    program.addCode(Opcode.ipush, dimIdx); // for load
                    program.addCode(Opcode.iload);
                    dimLengths.get(i + 1).genBytecode(program);
                    program.addCode(Opcode.imul); // calculate reused product
                    program.addCode(Opcode.istore);
                    program.addCode(Opcode.iload);
                    elemIndexes.get(i).genBytecode(program);
                    program.addCode(Opcode.imul); // element index * previously stored product
                    program.addCode(Opcode.iadd);
                }
                program.removeVal(); // remove dimIdx
                // end : now the stack top is the element index with below unchanged

                // store the array element index
                // in the local variable area at tmpIdx
                program.addCode(Opcode.istore);


                // store the array index and element index twice
                // for store
                program.addCode(Opcode.ipush, tmpIdx);
                program.addCode(Opcode.iload);
                program.addCode(Opcode.ipush, opdIdx);
                // for load
                program.addCode(Opcode.ipush, tmpIdx);
                program.addCode(Opcode.iload);
                program.addCode(Opcode.ipush, opdIdx);

                program.removeVal(); // remove tmpIdx

                // load the value of the left-value-expression onto the operand stack
                // and calculate
                if (dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.daload);
                    expression.genBytecode(program);

                    switch (assignmentOperator.getTag()) {
                        case Tag.PLASN:
                            program.addCode(Opcode.dadd);
                            break;
                        case Tag.MIASN:
                            program.addCode(Opcode.dsub);
                            break;
                        case Tag.MLASN:
                            program.addCode(Opcode.dmul);
                            break;
                        case Tag.QTASN:
                            program.addCode(Opcode.ddiv);
                            break;
//                        case Tag.RDASN:
//                            program.addCode(Opcode.rem);
//                            break;
                    }
                    program.addCode(Opcode.dastore);

                } else { // Tag.INT or Tag.DOUBLE
                    program.addCode(Opcode.iaload);
                    expression.genBytecode(program);

                    switch (assignmentOperator.getTag()) {
                        case Tag.PLASN:
                            program.addCode(Opcode.iadd);
                            break;
                        case Tag.MIASN:
                            program.addCode(Opcode.isub);
                            break;
                        case Tag.MLASN:
                            program.addCode(Opcode.imul);
                            break;
                        case Tag.QTASN:
                            program.addCode(Opcode.idiv);
                            break;
//                        case Tag.RDASN:
//                            program.addCode(Opcode.rem);
//                            break;
                    }
                    program.addCode(Opcode.iastore);

                }

            } else { // Tag.VARLEXPR

                // for load
                program.addCode(Opcode.ipush, opdIdx);
                // for store
                program.addCode(Opcode.ipush, opdIdx);

                if (dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dload);
                    expression.genBytecode(program);

                    switch (assignmentOperator.getTag()) {
                        case Tag.PLASN:
                            program.addCode(Opcode.dadd);
                            break;
                        case Tag.MIASN:
                            program.addCode(Opcode.dsub);
                            break;
                        case Tag.MLASN:
                            program.addCode(Opcode.dmul);
                            break;
                        case Tag.QTASN:
                            program.addCode(Opcode.ddiv);
                            break;
//                        case Tag.RDASN:
//                            program.addCode(Opcode.rem);
//                            break;
                    }
                    program.addCode(Opcode.dstore);

                } else { // Tag.INT or Tag.BOOL
                    program.addCode(Opcode.iload);
                    expression.genBytecode(program);

                    switch (assignmentOperator.getTag()) {
                        case Tag.PLASN:
                            program.addCode(Opcode.iadd);
                            break;
                        case Tag.MIASN:
                            program.addCode(Opcode.isub);
                            break;
                        case Tag.MLASN:
                            program.addCode(Opcode.imul);
                            break;
                        case Tag.QTASN:
                            program.addCode(Opcode.idiv);
                            break;
                        case Tag.RDASN:
                            program.addCode(Opcode.rem);
                            break;
                    }
                    program.addCode(Opcode.istore);

                }

            }

        } else { // '='

            if(lValTag == Tag.ARRLEXPR) {

                // start : generate code to calculate element index
                int dimIdx = program.getCurrentOpdInx();
                program.createVal();
                elemIndexes.get(elemIndexes.size() - 1).genBytecode(program);
                program.addCode(Opcode.ipush, dimIdx);
                program.addCode(Opcode.iconst_1);
                program.addCode(Opcode.istore);
                for(int i = elemIndexes.size() - 2; i >=0; i--) {
                    // load previously stored dimension length product
                    // multiply it by the current element index and store it back
                    program.addCode(Opcode.ipush, dimIdx); // for load again
                    program.addCode(Opcode.ipush, dimIdx); // for store
                    program.addCode(Opcode.ipush, dimIdx); // for load
                    program.addCode(Opcode.iload);
                    dimLengths.get(i + 1).genBytecode(program);
                    program.addCode(Opcode.imul); // calculate reused product
                    program.addCode(Opcode.istore);
                    program.addCode(Opcode.iload);
                    elemIndexes.get(i).genBytecode(program);
                    program.addCode(Opcode.imul); // element index * previously stored product
                    program.addCode(Opcode.iadd);
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
