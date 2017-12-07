package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;

import java.util.ArrayList;

public class NnaryExprNode extends SNode {

	private Token opt;
	private ArrayList<NnaryExprNode> childExpressions;
	private Token constVal; // for constant value
	private Identifer identifier; // for left-value-expression
	private Object value;
	// the following values are calculated and used in checkAndBuild()
	private int startLine;
	private int startPos; // record the start position of this expression
	private int endLine;
	private int endPos; // record the end position of this expression
	private int dataType; // indicate the data type of the expression


	public NnaryExprNode(int tag) {
		super(tag);
		opt = null;
		childExpressions = new ArrayList<NnaryExprNode>();
		constVal = null;
		identifier = null;
	}

	public Token getOpt() {
		return opt;
	}

	public void setOpt(Token opt) {
		this.opt = opt;
	}

	public Identifer getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifer identifier) {
		this.identifier = identifier;
	}

	public int getDataType() {
		return dataType;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndLine() {
		return endLine;
	}

	public int getEndPos() {
		return endPos;
	}


	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public ArrayList<NnaryExprNode> getChildExpressions() {
		return childExpressions;
	}

	public Object getValue() {
		visit();
		return value;
	}

	public void addChildExpression(NnaryExprNode childExpression) {
		childExpressions.add(childExpression);
	}

	public void setConstVal(Token constVal) {
		this.constVal = constVal;
	}

	@Override
	public void checkAndBuild() {

		if (tag == Tag.CONSTVAL) {
			// the attributes of leaf nodes are all synthesized attributes
			startLine = constVal.getLine();
			startPos = constVal.getStartpos();
			endLine = constVal.getLine();
			endPos = constVal.getEndpos();
			if (constVal.getTag() == Tag.ICONST) {
				dataType = Tag.INT;
			} else if (constVal.getTag() == Tag.DCONST) {
				dataType = Tag.DOUBLE;
			} else if (constVal.getTag() == Tag.TRUE || constVal.getTag() == Tag.FALSE) {
				dataType = Tag.BOOL;
			}
		} else if (tag == Tag.VARLEXPR || tag == Tag.ARRLEXPR) {
			startLine = identifier.getLine();
			startPos = identifier.getStartpos();
			// check if the identifier is declared
			Symbol symbol = currentEnv.get(identifier.getLexeme());
			if (symbol == null) {
				Failure.addFailure(SynTree.getFilepath(), identifier.getLine(), identifier.getStartpos(), Failure.ERROR, "use of undeclared identifier '" + identifier.getLexeme() + "'");
				return;
			}
			dataType = symbol.getDataType();


			// check if the tag of the left-value-expression is
			// consistent with the declaration
			// declared as basic variable, accessed as array
			if (tag == Tag.VARLEXPR && symbol.getTag() == Tag.ARRDECL) {
				Failure.addFailure(SynTree.getFilepath(), identifier.getLine(), identifier.getStartpos(), Failure.ERROR,
						identifier.getLexeme() + " is declared as an array, cannot be accessed as a basic type variable");
				// declared as array, accessed as basic variable
			} else if (tag == Tag.ARRLEXPR && symbol.getTag() == Tag.VARDECL) {
				Failure.addFailure(SynTree.getFilepath(), identifier.getLine(), identifier.getStartpos(), Failure.ERROR,
						identifier.getLexeme() + " is declared as a basic type variable, cannot be accessed as array");
			}


			if (tag == Tag.VARLEXPR) {
				endLine = identifier.getLine();
				endPos = identifier.getEndpos();
			} else {
				for (NnaryExprNode nnaryExprNode : childExpressions) {
					nnaryExprNode.setCurrentEnv(currentEnv);
					nnaryExprNode.checkAndBuild();
					// check data type of the lengths of dimensions
					// ensure that they are all of type int
					if (nnaryExprNode.getDataType() != Tag.INT) {
						Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "size of array has non-integer type");
					}
				}
				endLine = childExpressions.get(childExpressions.size() - 1).getEndLine();
				endPos = childExpressions.get(childExpressions.size() - 1).getEndPos() + 1;
			}

		} else if (tag == Tag.UNARYEXPR) {
			startLine = opt.getLine();
			startPos = opt.getStartpos();
			NnaryExprNode childExpr = childExpressions.get(0);
			childExpr.checkAndBuild();
			endLine = childExpr.getEndLine();
			endPos = childExpr.getEndPos();
			dataType = childExpr.getDataType();
			// check data type after '!'
			if (opt.getTag() == '!' && dataType != Tag.BOOL) {
				Failure.addFailure(SynTree.getFilepath(), opt.getLine(), opt.getStartpos(), Failure.ERROR, "'!' cannot be applied to non-boolean type");
			}
			// check data type after '-'
			if (opt.getTag() == '-' && dataType == Tag.BOOL) {
				Failure.addFailure(SynTree.getFilepath(), opt.getLine(), opt.getStartpos(), Failure.ERROR, "'-' cannot be applied to boolean type");
			}

		} else {
			// check data type
			// as at the same level, operator types are the same
			// select one and start checking
			Token sampleOpt = childExpressions.get(1).getOpt();
			int optTag = sampleOpt.getTag();
			int prevDataType = -1;
			boolean hasUndefined = false;
			boolean hasDouble = false;
			for (int i = 0; i < childExpressions.size(); i++) {

                NnaryExprNode nnaryExprNode = childExpressions.get(i);
				nnaryExprNode.setCurrentEnv(currentEnv);
				nnaryExprNode.checkAndBuild();

				// use undeclared variables
				if (nnaryExprNode.getDataType() == 0) {
					hasUndefined = true;
				} else if (nnaryExprNode.getDataType() == Tag.DOUBLE) {
					hasDouble = true;
				}

				sampleOpt = nnaryExprNode.getOpt() == null ? sampleOpt : nnaryExprNode.getOpt();
				optTag = sampleOpt.getTag();

				// operators except '==', '!=', '||' and '&&' cannot be applied on the boolean type variables
				if (optTag != Tag.EQ && optTag != Tag.NE && optTag != Tag.OR && optTag != Tag.AND && nnaryExprNode.getDataType() == Tag.BOOL) {
					Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "'" + sampleOpt.getLexeme() + "' cannot be applied to boolean type");
				}

				// '||' and '&&' cannot be applied on numeric types
				if ((optTag == Tag.AND || optTag == Tag.OR) && nnaryExprNode.getDataType() != Tag.BOOL) {
					Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "'" + sampleOpt.getLexeme() + "' cannot be applied to numeric type");
				}


				// '%' cannot be applied on double type
				if (sampleOpt.getTag() == '%'&& nnaryExprNode.getDataType() == Tag.DOUBLE) {
					Failure.addFailure(SynTree.getFilepath(), sampleOpt.getLine(), sampleOpt.getStartpos(), Failure.ERROR, "'" + sampleOpt.getLexeme() + "' cannot be applied to double type");
				}


				// equality operator cannot be used to compare boolean and numeric type
				if ((optTag == Tag.EQ || optTag == Tag.NE)
						&& i == 1
						&& childExpressions.get(0).getDataType() != nnaryExprNode.getDataType() // data type of previous child expression is different from that of this one
						&& (childExpressions.get(0).getDataType() == Tag.BOOL || nnaryExprNode.getDataType() == Tag.BOOL)) { // previous one or this one is boolean type, as '==' is applicable to numeric types
					Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getOpt().getLine(), nnaryExprNode.getOpt().getStartpos(), Failure.ERROR,
							 "'" + nnaryExprNode.getOpt().getLexeme() + "' cannot be used to compare numeric and boolean types");
				}
                if((optTag == Tag.EQ || optTag == Tag.NE)
                        && i > 1
                        && nnaryExprNode.getDataType() != Tag.BOOL) {
                    Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getOpt().getLine(), nnaryExprNode.getOpt().getStartpos(), Failure.ERROR,
                            "'" + nnaryExprNode.getOpt().getLexeme() + "' cannot be used to compare numeric and boolean types");
                }


				prevDataType = nnaryExprNode.getDataType();

			}
			startLine = childExpressions.get(0).getStartLine();
			startPos = childExpressions.get(0).getStartPos();
			endPos = childExpressions.get(childExpressions.size() - 1).getEndLine();
			endPos = childExpressions.get(childExpressions.size() - 1).getEndPos();
			// the data type of expression is boolean
			// only if some kinds of operators are applied
			if (hasUndefined)
				return;
			if (Tag.isResultBool(optTag)) {
				dataType = Tag.BOOL;
			} else if (hasDouble) {
				dataType = Tag.DOUBLE;
			} else {
				dataType = Tag.INT;
			}

		}

	}

	@Override
	public void visit() {
		value = null;
		if (tag == Tag.CONSTVAL) {
			String s = constVal.getLexeme();
			switch (dataType) {
				case Tag.INT:
					value = Integer.valueOf(s);
					break;
				case Tag.DCONST:
					value = Double.valueOf(s);
					break;
				case Tag.BOOL:
					value = Boolean.valueOf(s);
					break;
			}
		} else if (tag == Tag.VARLEXPR) {
			Symbol symbol = currentEnv.get(identifier.getLexeme());
			value = symbol.getValue();
		} else if (tag == Tag.ARRLEXPR) {
			Symbol symbol = currentEnv.get(identifier.getLexeme());
			int dimension = symbol.getDimension();
			ArrayList<NnaryExprNode> dimLengths = symbol.getDimLengths();
			Object array[] = (Object[]) symbol.getValue();

			// to calculate the location of the target value in one-dimensional array, which we convert multi-dimensional array to
			int location = 0;
			// e.g. int[2][3][4] a;  a[1][2][3] -> 1*3*4 + 2 * 4 + 3
			// dimension is 3 here
			for (int i = 0; i < dimension; i++) {
				int tmpDimLength = (int) childExpressions.get(i).getValue(); // tmpDimLength is 1,2,3 respectively
				for (int j = i + 1; j < dimension; j++)
					tmpDimLength *= (int) dimLengths.get(j).getValue();
				location += tmpDimLength;
			}
			value = array[location];
		} else if (tag == Tag.ADTVEXPR || tag == Tag.MLTVEXPR || tag == Tag.UNARYEXPR) { //when is not left value, calculate the value.
			int i = 0;
			double d = 0;
			boolean b = false;
			if (tag == Tag.UNARYEXPR) {
				switch (dataType) {
					case Tag.INT:
						i = (int) childExpressions.get(0).getValue();
						value = -i;
						break;
					case Tag.DOUBLE:
						d = (double) childExpressions.get(0).getValue();
						value = -d;
						break;
					case Tag.BOOL:
						b = (boolean) childExpressions.get(0).getValue();
						value = !b;
						break;
				}
			} else if (tag == Tag.ADTVEXPR) {
				switch (dataType) {
					case Tag.INT:
						i = (int) childExpressions.get(0).getValue();
						for (int m = 1; m < childExpressions.size(); m++) {
							NnaryExprNode tmp = childExpressions.get(m);
							int tmpValue = (int) tmp.getValue();
							switch (tmp.getOpt().getTag()) {
								case '+':
									i += tmpValue;
									break;
								case '-':
									i -= tmpValue;
									break;
							}
						}
						value = i;
						break;
					case Tag.DOUBLE:
						d = (double) childExpressions.get(0).getValue();
						for (int m = 1; m < childExpressions.size(); m++) {
							NnaryExprNode tmp = childExpressions.get(m);
							double tmpValue = (double) tmp.getValue();
							switch (tmp.getOpt().getTag()) {
								case '+':
									d += tmpValue;
									break;
								case '-':
									d -= tmpValue;
									break;
							}
						}
						value = d;
						break;
				}
			} else if (tag == Tag.MLTVEXPR) {
				switch (dataType) {
					case Tag.INT:
						i = (int) childExpressions.get(0).getValue();
						for (int m = 1; m < childExpressions.size(); m++) {
							NnaryExprNode tmp = childExpressions.get(m);
							int tmpValue = (int) tmp.getValue();
							switch (tmp.getOpt().getTag()) {
								case '*':
									i *= tmpValue;
									break;
								case '/':
									if (tmpValue == 0) {
										System.err.println("Runtime Error: divided by 0 on line " + startLine + ", position " + startPos);
										System.exit(1);
									}
									i /= tmpValue;
									break;
								case '%':
									if (tmpValue == 0) {
										System.err.println("Runtime Error: mod by 0 on line " + startLine + ", position " + startPos);
										System.exit(1);
									}
									i %= tmpValue;
									break;
							}
						}
						value = i;
						break;
					case Tag.DOUBLE:
						d = (double) childExpressions.get(0).getValue();
						for (int m = 1; m < childExpressions.size(); m++) {
							NnaryExprNode tmp = childExpressions.get(m);
							double tmpValue = (double) tmp.getValue();
							switch (tmp.getOpt().getTag()) {
								case '*':
									d *= tmpValue;
									break;
								case '/':
									if (tmpValue == 0) {
										System.err.println("Runtime Error: divided by 0 on line " + startLine + ", position " + startPos);
										System.exit(1);
									}
									d /= tmpValue;
									break;
							}
						}
						value = d;
						break;
				}
			}
		} else if (tag == Tag.RELAEXPR) {
			boolean boolValue = false;
			int i;
			double d;
			switch (childExpressions.get(0).getDataType()){
				case Tag.INT:
					i = (int) childExpressions.get(0).getValue();
					for (int m = 1; m < childExpressions.size(); m++){
						NnaryExprNode tmp = childExpressions.get(m);
						int tmpValue = (int) tmp.getValue();
						switch (tmp.getOpt().getTag()) {
							case '>':
								boolValue = i > tmpValue;
								break;
							case '<':
								boolValue = i < tmpValue;
								break;
							case Tag.LE:
								boolValue = i <= tmpValue;
								break;
							case Tag.GE:
								boolValue = i >= tmpValue;
								break;
						}
					}
					break;
				case Tag.DOUBLE:
					d = (double) childExpressions.get(0).getValue();
					for (int m = 1; m < childExpressions.size(); m++) {
						NnaryExprNode tmp = childExpressions.get(m);
						double tmpValue = (double) tmp.getValue();
						switch (tmp.getOpt().getTag()) {
							case '>':
								boolValue = d > tmpValue;
								break;
							case '<':
								boolValue = d < tmpValue;
								break;
							case Tag.LE:
								boolValue = d <= tmpValue;
								break;
							case Tag.GE:
								boolValue = d >= tmpValue;
								break;
						}
					}
					break;
			}
			value = boolValue;
			//TODO assignment-operator，%=，,+=，boolean运算，2==3!=true
			//==,!=,
		} else if (tag == Tag.EQEXPR) {
			boolean boolValue = false, bool2;
			int i, i1;
			double d, d1;
			//in case that the first expression is 2==3 == true, in this case the grammar is correct. Otherwise, the operands will all by type of bool
			// 2==3 == true correct
			// true != 2==3 incorrect

			for (int m = 0; m < childExpressions.size(); m++) {
				if (m == 0) {
					switch (childExpressions.get(0).getDataType()) {
						case Tag.INT:
							i = (int) childExpressions.get(0).getValue();
							i1 = (int) childExpressions.get(1).getValue();
							switch (childExpressions.get(1).getOpt().getTag()) {
								case Tag.EQ:
									boolValue = i == i1;
									break;
								case Tag.NE:
									boolValue = i != i1;
									break;
							}
							break;
						case Tag.DOUBLE:
							d = (double) childExpressions.get(0).getValue();
							d1 = (double) childExpressions.get(1).getValue();
							switch (childExpressions.get(1).getOpt().getTag()) {
								case Tag.EQ:
									boolValue = d == d1;
									break;
								case Tag.NE:
									boolValue = d != d1;
									break;
							}
							break;
						case Tag.BOOL:
							boolValue = (Boolean) childExpressions.get(0).getValue();
							bool2 = (Boolean) childExpressions.get(1).getValue();
							switch (childExpressions.get(1).getOpt().getTag()) {
								case Tag.EQ:
									boolValue = boolValue == bool2;
									break;
								case Tag.NE:
									boolValue = boolValue != bool2;
									break;
							}
							break;
					}
					m++;
				} else {
					bool2 = (Boolean) childExpressions.get(m).getValue();
					switch (childExpressions.get(m).getOpt().getTag()) {
						case Tag.EQ:
							boolValue = boolValue == bool2;
							break;
						case Tag.NE:
							boolValue = boolValue != bool2;
							break;
					}
				}
			}
			value = boolValue;
		} else if (tag == Tag.LGANDEXPR) {
			boolean boolValue;
			boolValue = (Boolean) childExpressions.get(0).getValue();
			for (int m = 1; m < childExpressions.size(); m++) {
				NnaryExprNode tmp = childExpressions.get(m);
				Boolean tmpValue = (Boolean) tmp.getValue();
				boolValue = boolValue && tmpValue;
			}
			value = boolValue;
		} else if (tag == Tag.LGOREXPR) {
			boolean boolValue;
			boolValue = (Boolean) childExpressions.get(0).getValue();
			for (int m = 1; m < childExpressions.size(); m++) {
				NnaryExprNode tmp = childExpressions.get(m);
				Boolean tmpValue = (Boolean) tmp.getValue();
				boolValue = boolValue || tmpValue;
			}
			value = boolValue;
		}
	}


	@Override
	public void traverse(int blank) {
		if (blank > 0)
			System.out.print(" |");
		for (int i = 0; i < blank; i++) {
			if (i == blank - 1)
				System.out.print("---");
			else
				System.out.print("    |");
		}
		System.out.print(opt == null ? "" : " " + opt.getLexeme());
		if (this.tag == Tag.CONSTVAL) {
			System.out.println(" Litrl " + constVal.getLexeme());
		} else if (this.tag == Tag.ARRLEXPR || this.tag == Tag.VARLEXPR) {
			System.out.println(" LftVal " + identifier.getLexeme());
		} else {
			System.out.println(" Expr");
		}
		for (NnaryExprNode childExpression : childExpressions) {
			childExpression.traverse(blank + 1);
		}
	}

	@Override
	public void genBytecode(Program program) {

		if (tag == Tag.CONSTVAL) {
			program.addConstant(constVal.getLexeme(), dataType);
		} else if (tag == Tag.VARLEXPR) {

			// get the index of the operand in the local variable area
			// generate code to push the index onto the operand stack
			Symbol symbol = currentEnv.get(identifier.getLexeme());
			int opdIdx = symbol.getOpdIdx();
			program.addCode(Opcode.ipush, opdIdx);
            if(dataType == Tag.DOUBLE) {
                program.addCode(Opcode.dload);
            } else {
                program.addCode(Opcode.iload);
            }
        // basic variable left-value-expression end
		} else if (tag == Tag.ARRLEXPR) {

            Symbol symbol = currentEnv.get(identifier.getLexeme());
            int opdIdx = symbol.getOpdIdx();
            ArrayList<NnaryExprNode> elemIndexes = childExpressions;
            ArrayList<NnaryExprNode> dimLengths = symbol.getDimLengths();

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

            program.addCode(Opcode.ipush, opdIdx);
            if(dataType == Tag.DOUBLE) {
                program.addCode(Opcode.daload);
            } else {
                program.addCode(Opcode.iaload);
            }
        // array left-value-expression end
		} else if(tag == Tag.UNARYEXPR) {
            if(opt.getTag() == '-') {
                program.addCode(Opcode.iconst_0);
                childExpressions.get(0).genBytecode(program);
                if(dataType == Tag.DOUBLE) {
                    program.addCode(Opcode.dsub);
                } else {
                    program.addCode(Opcode.isub);
                }
            } else if(opt.getTag() == '!') {
                program.addCode(Opcode.iconst_1);
                childExpressions.get(0).genBytecode(program);
                program.addCode(Opcode.isub); // 1 - 1, 1 - 0
            }
        // unary-expression end
        } else if(tag == Tag.MLTVEXPR) {
		    if(dataType == Tag.DOUBLE) {
		        for(NnaryExprNode nnaryExprNode : childExpressions) {
		            Token currOpt = nnaryExprNode.getOpt();
		            nnaryExprNode.genBytecode(program);
		            // generate calculation byte code
		            if(currOpt != null) {
		                if(currOpt.getTag() == '*') {
                            program.addCode(Opcode.dmul);
                        } else if(currOpt.getTag() == '/') {
		                    program.addCode(Opcode.ddiv);
                        }
                    }
                }
            } else {
                for(NnaryExprNode nnaryExprNode : childExpressions) {
                    Token currOpt = nnaryExprNode.getOpt();
                    nnaryExprNode.genBytecode(program);
                    // generate calculation byte code
                    if(currOpt != null) {
                        if(currOpt.getTag() == '*') {
                            program.addCode(Opcode.imul);
                        } else if(currOpt.getTag() == '/') {
                            program.addCode(Opcode.idiv);
                        } else if(currOpt.getTag() == '%') {
                            program.addCode(Opcode.rem);
                        }
                    }
                }
            }
        // multiplicative-expression end
        } else if (tag == Tag.ADTVEXPR) {
            if(dataType == Tag.DOUBLE) {
                for(NnaryExprNode nnaryExprNode : childExpressions) {
                    Token currOpt = nnaryExprNode.getOpt();
                    nnaryExprNode.genBytecode(program);

                    // generate calculation byte code
                    if(currOpt != null) {
                        if(currOpt.getTag() == '+') {
                            program.addCode(Opcode.dadd);
                        } else if(currOpt.getTag() == '-') {
                            program.addCode(Opcode.dsub);
                        }
                    }

                }
            } else {
                for(NnaryExprNode nnaryExprNode : childExpressions) {
                    Token currOpt = nnaryExprNode.getOpt();
                    nnaryExprNode.genBytecode(program);
                    // generate calculation byte code
                    if(currOpt != null) {
                        if(currOpt.getTag() == '+') {
                            program.addCode(Opcode.iadd);
                        } else if(currOpt.getTag() == '-') {
                            program.addCode(Opcode.isub);
                        }
                    }
                }
            }
        // addictive-expression end
        } else { // Tag.RELAEXPR, Tag.EQEXPR, Tag.LGANDEXPR, Tag.LGOREXPR
		    childExpressions.get(0).genBytecode(program);
		    childExpressions.get(1).genBytecode(program);
            Token currOpt = childExpressions.get(1).getOpt();
            switch (currOpt.getTag()) {
                    case '<':
                        program.addCode(Opcode.tlt);
                        break;
                    case '>':
                        program.addCode(Opcode.tgt);
                        break;
                    case Tag.LE:
                        program.addCode(Opcode.tle);
                        break;
                    case Tag.GE:
                        program.addCode(Opcode.tge);
                        break;
                    case Tag.EQ:
                        program.addCode(Opcode.teq);
                        break;
                    case Tag.NE:
                        program.addCode(Opcode.tne);
                        break;
                    case Tag.AND:
                        program.addCode(Opcode.and);
                        break;
                    case Tag.OR:
                        program.addCode(Opcode.or);
                        break;
                }
        }
	}

}
