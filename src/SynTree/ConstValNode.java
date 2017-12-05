package SynTree;

import CMMVM.Bytecode;
import Lexer.Tag;
import Lexer.Token;

import java.util.ArrayList;

// constant
public class ConstValNode extends SNode {


    private Token constant;

    public ConstValNode() {
        super(Tag.CONSTVAL);
        constant = null;
    }

    public void setConstant(Token constant) {
        this.constant = constant;
    }

    @Override
    public void checkAndBuild() {

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
        System.out.println("ConstVal " + constant.getLexeme());
    }

    @Override
    public void genBytecode(ArrayList<Bytecode> prog) {

    }

}
