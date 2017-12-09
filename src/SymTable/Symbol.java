package SymTable;

import Lexer.Identifer;
import Lexer.Tag;
import SynTree.NnaryExprNode;

import java.util.ArrayList;


public class Symbol {

    private Identifer identifer;
    private int tag; // basic data type or array
    private int dataType; // data type: int, double, bool
    private ArrayList<NnaryExprNode> dimLengths; // length of each dimension
    private Object value; // reference to the array
    private int arrayLength;
    private int opdIdx;
    
    // for basic data type
    public Symbol(Identifer idt, int tag, int dataType) {
        this.identifer = idt;
        this.tag = tag;
        this.dataType = dataType;
        this.dimLengths = null;
        this.value = null;
        this.arrayLength = -1;
    }
    // for array
    public Symbol(Identifer idt, int tag, int dataType, ArrayList<NnaryExprNode> dimLengths) {
        this.identifer = idt;
        this.tag = tag;
        this.dataType = dataType;
        this.dimLengths = dimLengths;
        this.value = null;
        this.arrayLength = -1;
    }

    public Identifer getIdentifer() {
        return identifer;
    }

    public int getTag() {
        return tag;
    }

    public int getDataType() {
        return dataType;
    }

    public int getDimension() {
        if(tag == Tag.VARDECL) {
            return 0;
        } else {
            return dimLengths.size();
        }
    }

    public ArrayList<NnaryExprNode> getDimLengths() {
        return dimLengths;
    }

    public int getArrayLength() {
        return arrayLength;
    }

    public Object getValue() {
        return value;
    }

    public int getOpdIdx() {
        return opdIdx;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setOpdIdx(int opdIdx) {
        this.opdIdx = opdIdx;
    }

    public void setArrayLength(int arrayLength) {
        this.arrayLength = arrayLength;
    }
}
