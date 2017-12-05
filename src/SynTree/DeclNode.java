package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

// declaration
public class DeclNode extends SNode {

	private Token declarationSpecifer; //int, double, bool
	private ArrayList<NnaryExprNode> dimensionLengths; // additive-expression, e.g. [a+2][3]
	private ArrayList<InitlzrNode> initializers; // a = 1, b=3;


	public DeclNode(int tag) {
		super(tag);
		declarationSpecifer = null;
		dimensionLengths = null;
		initializers = new ArrayList<>();
	}

	public void setDeclarationSpecifer(Token declarationSpecifer) {
		this.declarationSpecifer = declarationSpecifer;
	}

	public void addDimensionLength(NnaryExprNode additiveExpression) {
		if (dimensionLengths == null) {
			dimensionLengths = new ArrayList<NnaryExprNode>();
		}
		dimensionLengths.add(additiveExpression);
	}

	public void addInitializer(InitlzrNode initializer) {
		initializers.add(initializer);
	}

	@Override
	public void checkAndBuild() {


		// if it is an array check the length of each dimension
		// to ensure they are all of type int
		if (tag == Tag.ARRDECL) {
			for (NnaryExprNode nnaryExprNode : dimensionLengths) {
			    nnaryExprNode.setCurrentEnv(currentEnv);
				nnaryExprNode.checkAndBuild();
				if (nnaryExprNode.getDataType() != Tag.INT) {
					Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "size of array has non-integer type");
				}
			}
		}


		int declDataType = declarationSpecifer.getTag();
		for (InitlzrNode initlzrNode : initializers) {

		    initlzrNode.setCurrentEnv(currentEnv);
		    initlzrNode.checkAndBuild();

			// check if it is redefinition or initialization of an array
			// put identifiers into symbols if there are no errors
			Identifer currIdt = initlzrNode.getIdentifer();
			Symbol symbol = currentEnv.getFromCurrentScope(currIdt.getLexeme());
			if (symbol != null) {
				Identifer prevIdt = symbol.getIdentifer();
				Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR,
						"redefinition of identifier '" + currIdt.getLexeme()
								+ "', previously defined at line " + prevIdt.getLine() + ", position " + prevIdt.getStartpos());
				continue;
			}

			if (tag == Tag.ARRDECL) {
				int size = dimensionLengths.size();
				symbol = new Symbol(currIdt, Tag.ARRDECL, declDataType, dimensionLengths);
				currentEnv.put(currIdt.getLexeme(), symbol);
				if (initlzrNode.getExpression() != null) {
					Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR, "array cannot be declared and initialized at the same time");
				}
			} else if (tag == Tag.VARDECL) {
				symbol = new Symbol(currIdt, Tag.VARDECL, declDataType);
				// put it into symbol table first, as long as it is not redefinition
				currentEnv.put(currIdt.getLexeme(), symbol);
				// check data type of initializer
				if (initlzrNode.getExpression() == null)
					return;
				initlzrNode.getExpression().checkAndBuild();
				int asgnDataType = initlzrNode.getExpression().getDataType();
				// double to int cast warning
				if (declDataType == Tag.INT && asgnDataType == Tag.DOUBLE) {
					Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.WARNING, "implicityly cast double to int");
				}
				// double or int to bool converting error, and vice versa
				// note that identifier in the expression may not be declared
				if (declDataType == Tag.BOOL && (asgnDataType == Tag.INT || asgnDataType == Tag.DOUBLE)) {
					Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR,
							"inconsistent type, cannot use " + (asgnDataType == Tag.INT ? "int" : "double") + " value to initialize bool variable");
				} else if (asgnDataType == Tag.BOOL && (declDataType == Tag.INT || declDataType == Tag.DOUBLE)) {
					Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR,
							"inconsistent type, cannot use bool value to initialize " + (declDataType == Tag.INT ? "int" : "double") + " variable");
				}
			}

		} // end of loop for initializers

	}

	@Override
	public void visit() {
		for (InitlzrNode initlzrNode : initializers) {
			Identifer currIdt = initlzrNode.getIdentifer();
			Symbol symbol = currentEnv.get(currIdt.getLexeme());

			if (tag == Tag.ARRDECL) {
				//define the one dimensional size of the multi-dimensional array
				int totalNum = 1;
				for (NnaryExprNode n : dimensionLengths) {
					totalNum *= (int) n.getValue();
				}
				//data type is already set in advance when doing checkAndBuild()
				switch (symbol.getDataType()){
					case Tag.INT:
						symbol.setValue(new int[totalNum]);
					case Tag.BOOL:
						symbol.setValue(new boolean[totalNum]);
					case Tag.DOUBLE:
						symbol.setValue(new double[totalNum]);
				}
			}else if (tag == Tag.VARDECL){
				initlzrNode.getExpression().visit();
				if (initlzrNode.getExpression() == null)
					return;
				symbol.setValue(initlzrNode.getExpression().getValue());
			}

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
		System.out.println("Decl");

		blank++;
		if (blank > 0)
			System.out.print(" |");
		for (int i = 0; i < blank; i++) {
			if (i == blank - 1)
				System.out.print("---");
			else
				System.out.print("    |");
		}
		System.out.println(declarationSpecifer.getLexeme());
		if (dimensionLengths != null) {
			for (NnaryExprNode adtvExprNode : dimensionLengths) {
				adtvExprNode.traverse(blank);
			}
		}
		for (InitlzrNode initlzrNode : initializers) {
			initlzrNode.traverse(blank);
		}
	}

    @Override
    public void genBytecode(Program program) {

        if(tag == Tag.ARRDECL) {

            for(InitlzrNode initlzrNode : initializers) {
                // store the operand index in the symbol table
                Symbol symbol = currentEnv.get(initlzrNode.getIdentifer().getLexeme());
                symbol.setOpdIdx(program.getCurrentOpdInx());
                // generate code to calculate the length of the array
                program.addCode(Opcode.iconst_1);
                for (NnaryExprNode nnaryExprNode : dimensionLengths) {
                    nnaryExprNode.genBytecode(program);
                    program.addCode(Opcode.mul);
                }
                // generate code to create an array
                if(declarationSpecifer.getTag() == Tag.DOUBLE) {
                    program.addCode(Opcode.newarray, Tag.DOUBLE);
                } else {
                    program.addCode(Opcode.newarray, Tag.INT);
                }

            }

        } else if(tag == Tag.VARDECL) {

            for(InitlzrNode initlzrNode : initializers) {
                // store the operand index in the symbol table
                // and generate code to push its index onto the stack
                Symbol symbol = currentEnv.get(initlzrNode.getIdentifer().getLexeme());
                int opdIdx = program.getCurrentOpdInx();
                symbol.setOpdIdx(opdIdx);
                program.addCode(Opcode.ipush, opdIdx);

                NnaryExprNode nnaryExprNode = initlzrNode.getExpression();
                if(nnaryExprNode != null) {
                    nnaryExprNode.genBytecode(program);
                } else {
                    program.addCode(Opcode.iconst_0);
                }

                // generate code to create a variable
                if(declarationSpecifer.getTag() == Tag.DOUBLE) {
                    program.addCode(Opcode.dstore);
                } else {
                    program.addCode(Opcode.istore);
                }



            }
        }

    }

}


