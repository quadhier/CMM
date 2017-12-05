package SynTree;

import CMMVM.Bytecode;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;

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
        if(dimensionLengths == null) {
            dimensionLengths = new ArrayList<NnaryExprNode>();
        }
        dimensionLengths.add(additiveExpression);
    }

    public void addInitializer(InitlzrNode initializer) {
        initializers.add(initializer);
    }

    @Override
    public void checkAndBuild() {

        // set environment of child nodes
        if(dimensionLengths != null) {
            for (NnaryExprNode adtvExprNode : dimensionLengths) {
                adtvExprNode.setCurrentEnv(currentEnv);
            }
        }
        for(InitlzrNode initlzrNode  : initializers) {
            initlzrNode.setCurrentEnv(currentEnv);
        }

        // if it is an array check the length of each dimension
        // to ensure they are all of type int
        if(tag == Tag.ARRDECL) {
            for(NnaryExprNode nnaryExprNode : dimensionLengths) {
                nnaryExprNode.checkAndBuild();
                if(nnaryExprNode.getDataType() != Tag.INT) {
                    Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "size of array has non-integer type");
                }
            }
        }

        int declDataType = declarationSpecifer.getTag();
        for (InitlzrNode initlzrNode : initializers) {

            // check if it is redefinition or initialization of an array
            // put identifiers into symbols if there are no errors
            Identifer currIdt = initlzrNode.getIdentifer();
            Symbol symbol = currentEnv.getFromCurrentScope(currIdt.getLexeme());
            if(symbol != null) {
                Identifer prevIdt =  symbol.getIdentifer();
                Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR,
                        "redefinition of identifier '" + currIdt.getLexeme()
                                + "', previously defined at line " + prevIdt.getLine() + ", position " + prevIdt.getStartpos());
                continue;
            }

            if(tag == Tag.ARRDECL) {
                int size = dimensionLengths.size();
                symbol = new Symbol(currIdt, Tag.ARRDECL, declDataType, dimensionLengths);
                currentEnv.put(currIdt.getLexeme(), symbol);
                if(initlzrNode.getExpression() != null) {
                    Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR, "array cannot be declared and initialized at the same time");
                }
            } else if(tag == Tag.VARDECL) {
                symbol = new Symbol(currIdt, Tag.VARDECL, declDataType);
                // put it into symbol table first, as long as it is not redefinition
                currentEnv.put(currIdt.getLexeme(), symbol);
                // check data type of initializer
                if(initlzrNode.getExpression() == null)
                    return;
                initlzrNode.getExpression().checkAndBuild();
                int asgnDataType = initlzrNode.getExpression().getDataType();
                // double to int cast warning
                if(declDataType == Tag.INT && asgnDataType == Tag.DOUBLE) {
                    Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(),Failure.WARNING, "implicityly cast double to int");
                }
                // double or int to bool converting error, and vice versa
                // note that identifier in the expression may not be declared
                if(declDataType == Tag.BOOL && (asgnDataType == Tag.INT || asgnDataType == Tag.DOUBLE)) {
                    Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR,
                            "inconsistent type, cannot use " + (asgnDataType == Tag.INT ? "int" : "double") + " value to initialize bool variable");
                } else if(asgnDataType == Tag.BOOL && (declDataType == Tag.INT || declDataType == Tag.DOUBLE)) {
                    Failure.addFailure(SynTree.getFilepath(), currIdt.getLine(), currIdt.getStartpos(), Failure.ERROR,
                            "inconsistent type, cannot use bool value to initialize " + (declDataType == Tag.INT ? "int" : "double") + " variable");
                }
            }

        } // end of loop for initializers

    }

    @Override
    public void visit() {

        int declDataType = declarationSpecifer.getTag();
        for (InitlzrNode initlzrNode : initializers){
            Identifer currIdt = initlzrNode.getIdentifer();
            if (tag==Tag.ARRDECL){
                Symbol symbol = new Symbol(currIdt, Tag.ARRDECL, declDataType, dimensionLengths);
                currentEnv.put(currIdt.getLexeme(), symbol);
                if (declDataType == Tag.INT) {
                    //define the one dimensional size of the multi-dimensional array
                    int totalNum = 1;
                    for (NnaryExprNode n:dimensionLengths){

                    }
                    //symbol.setValue(new int[]);
                }
            }
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
        System.out.println("Decl");

        blank++;
        if(blank > 0)
            System.out.print(" |");
        for(int i = 0 ; i < blank; i++) {
            if(i == blank - 1)
                System.out.print("---");
            else
                System.out.print("    |");
        }
        System.out.println(declarationSpecifer.getLexeme());
        if(dimensionLengths != null) {
            for(NnaryExprNode adtvExprNode : dimensionLengths) {
                adtvExprNode.traverse(blank);
            }
        }
        for(InitlzrNode initlzrNode : initializers) {
            initlzrNode.traverse(blank);
        }
    }

    @Override
    public void genBytecode(ArrayList<Bytecode> prog, int currentOpdIdx, ArrayList<Object> constantPool) {

    }

}


