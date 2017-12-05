package SynTree;

import CMMVM.Bytecode;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;

import java.util.ArrayList;

public class NnaryExprNode extends SNode {

    private Token opt;
    private ArrayList<NnaryExprNode> childExpressions;
    private Token constVal; // for constant value
    private Identifer identifier; // for left-value-expression
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

    public Identifer getIdentifier() {
        return identifier;
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

    public int getEndLine() {
        return endLine;
    }

    public int getEndPos() {
        return endPos;
    }

    public ArrayList<NnaryExprNode> getChildExpressions() {
        return childExpressions;
    }

    public void setOpt(Token opt) {
        this.opt = opt;
    }

    public void addChildExpression(NnaryExprNode childExpression) {
        childExpressions.add(childExpression);
    }

    public void setConstVal(Token constVal) {
        this.constVal = constVal;
    }

    public void setIdentifier(Identifer identifier) {
        this.identifier = identifier;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    @Override
    public void checkAndBuild() {

        if(tag == Tag.CONSTVAL) {
            // the attributes of leaf nodes are all synthesized attributes
            startLine = constVal.getLine();
            startPos = constVal.getStartpos();
            endLine = constVal.getLine();
            endPos = constVal.getEndpos();
            if(constVal.getTag() == Tag.ICONST) {
                dataType = Tag.INT;
            } else if(constVal.getTag() == Tag.DCONST) {
                dataType = Tag.DOUBLE;
            } else if(constVal.getTag() == Tag.TRUE || constVal.getTag() == Tag.FALSE) {
                dataType = Tag.BOOL;
            }
        } else if(tag == Tag.VARLEXPR || tag == Tag.ARRLEXPR) {
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
            if(tag == Tag.VARLEXPR && symbol.getTag() == Tag.ARRDECL) {
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

                // check if dimension is inconsistent
                if(childExpressions.size() != symbol.getDimension()) {
                    Failure.addFailure(SynTree.getFilepath(), identifier.getLine(), identifier.getStartpos(), Failure.ERROR,
                            "array '" + identifier.getLexeme() + "''s dimension is inconsistent with its declaration");
                }


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

        } else if(tag == Tag.UNARYEXPR) {
            startLine = opt.getLine();
            startPos = opt.getStartpos();
            NnaryExprNode childExpr = childExpressions.get(0);
            childExpr.checkAndBuild();
            endLine = childExpr.getEndLine();
            endPos = childExpr.getEndPos();
            dataType = childExpr.getDataType();
            // check data type after '!'
            if(opt.getTag() == '!' && dataType != Tag.BOOL) {
                Failure.addFailure(SynTree.getFilepath(), opt.getLine(), opt.getStartpos(), Failure.ERROR, "'!' cannot be applied to non-boolean type");
            }
            // check data type after '-'
            if(opt.getTag() == '-' && dataType == Tag.BOOL) {
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
            for (NnaryExprNode nnaryExprNode : childExpressions) {
                nnaryExprNode.setCurrentEnv(currentEnv);
                nnaryExprNode.checkAndBuild();

                // use undeclared variables
                if(nnaryExprNode.getDataType() == 0) {
                    hasUndefined = true;
                } else if (nnaryExprNode.getDataType() == Tag.DOUBLE) {
                    hasDouble = true;
                }

                // operators except '==', '!=', '||' and '&&' cannot be applied on the boolean type variables
                if (optTag != Tag.EQ && optTag != Tag.NE && optTag != Tag.OR && optTag != Tag.AND && nnaryExprNode.getDataType() == Tag.BOOL) {
                    Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "'" + sampleOpt.getLexeme() + "' cannot be applied to boolean type");
                }

                // '||' and '&&' cannot be applied on numeric types
                if((optTag == Tag.AND || optTag == Tag.OR) && nnaryExprNode.getDataType() != Tag.BOOL) {
                    Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getStartLine(), nnaryExprNode.getStartPos(), Failure.ERROR, "'" + sampleOpt.getLexeme() + "' cannot be applied to numeric type");
                }


                // '%' cannot be applied on double type
                Token tagetOpt = nnaryExprNode.getOpt();
                if(tagetOpt != null && tagetOpt.getTag() == '%'
                        && (prevDataType == Tag.DOUBLE || nnaryExprNode.getDataType() == Tag.DOUBLE)) {
                    Failure.addFailure(SynTree.getFilepath(), tagetOpt.getLine(), tagetOpt.getStartpos(), Failure.ERROR, "'" + tagetOpt.getLexeme() + "' cannot be applied to double type");
                }

                // equality operator cannot be used to compare boolean and numeric type
                if((optTag == Tag.EQ || optTag == Tag.NE)
                        && prevDataType != -1 // not the first one
                        && prevDataType != nnaryExprNode.getDataType() // data type of previous child expression is different from that of this one
                        && (prevDataType == Tag.BOOL || nnaryExprNode.getDataType() == Tag.BOOL)) { // previous one or this one is boolean type, as '==' is applicable to numeric types
                    Failure.addFailure(SynTree.getFilepath(), nnaryExprNode.getOpt().getLine(), nnaryExprNode.getOpt().getStartpos(), Failure.ERROR,
                            nnaryExprNode.getOpt().getLexeme() + " cannot be used to compare numeric and boolean types");
                }


                prevDataType = nnaryExprNode.getDataType();

            }
            startLine = childExpressions.get(0).getStartLine();
            startPos = childExpressions.get(0).getStartPos();
            endPos = childExpressions.get(childExpressions.size() - 1).getEndLine();
            endPos = childExpressions.get(childExpressions.size() - 1).getEndPos();
            // the data type of expression is boolean
            // only if some kinds of operators are applied
            if(hasUndefined)
                return;
            if(Tag.isResultBool(optTag)) {
                dataType = Tag.BOOL;
            } else if(hasDouble) {
                dataType = Tag.DOUBLE;
            } else {
                dataType = Tag.INT;
            }

        }

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
        System.out.print(opt == null ? "" : " " + opt.getLexeme());
        if(this.tag == Tag.CONSTVAL) {
            System.out.println(" Litrl " + constVal.getLexeme());
        } else if (this.tag == Tag.ARRLEXPR || this.tag == Tag.VARLEXPR) {
            System.out.println(" LftVal " + identifier.getLexeme());
        } else {
            System.out.println(" Expr");
        }
        for(NnaryExprNode childExpression : childExpressions) {
            childExpression.traverse(blank + 1);
        }
    }

    @Override
    public void genBytecode(ArrayList<Bytecode> prog, int currentOpdIdx, ArrayList<Object> constantPool) {

    }

}
