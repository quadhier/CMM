package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Lexer.Tag;
import Lexer.Token;
import SymTable.Symbol;

import java.util.ArrayList;

public class ReadStmtNode extends SNode {


    private NnaryExprNode leftValueExpression;

    public ReadStmtNode() {
        super(Tag.READSTMT);
        leftValueExpression = null;
    }

    public void setLeftValueExpression(NnaryExprNode leftValueExpression) {
        this.leftValueExpression = leftValueExpression;
    }

    @Override
    public void checkAndBuild() {
        leftValueExpression.setCurrentEnv(currentEnv);
        leftValueExpression.checkAndBuild();
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
        System.out.println("ReadStmt");
        leftValueExpression.traverse(blank + 1);
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

            if(dataType == Tag.DOUBLE) {
                program.addCode(Opcode.dread);
                program.addCode(Opcode.dastore);
            } else {
                program.addCode(Opcode.iread);
                program.addCode(Opcode.iastore);
            }

        } else if(lValTag == Tag.VARLEXPR) {

            program.addCode(Opcode.ipush, opdIdx);

            if(dataType == Tag.DOUBLE) {
                program.addCode(Opcode.dread);
                program.addCode(Opcode.dstore);
            } else {
                program.addCode(Opcode.iread);
                program.addCode(Opcode.istore);
            }

        }

    }

}
