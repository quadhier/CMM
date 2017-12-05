package CMMVM;

import Lexer.Tag;

import java.util.ArrayList;
import java.util.Hashtable;

public class Program {

    Hashtable<String, Integer> constantTable; // record the constant pool
    ArrayList<Object> constantPool;
    ArrayList<Bytecode> codes;
    int currentOpdInx; // only used in code generation

    public Program() {
        constantTable = new Hashtable<>();
        constantPool = new ArrayList<>();
        codes = new ArrayList<Bytecode>();
        currentOpdInx = 0;
    }

    private boolean isLocalValInc(byte Operand) {
        switch (Operand) {
            case Opcode.istore:
            case Opcode.dstore:
            case Opcode.iastore:
            case Opcode.dastore:
                return true;
            default:
                return false;
        }
    }

    public ArrayList<Object> getConstantPool() {
        return constantPool;
    }

    public ArrayList<Bytecode> getCodes() {
        return codes;
    }

    public int getCurrentOpdInx() {
        return currentOpdInx;
    }

    public void addCode(byte opt, int opd) {
        int offset = codes.size();
        codes.add(new Bytecode(offset, opt, opd));
        if(isLocalValInc(opt)) {
            currentOpdInx++;
        }
    }

    public void addCode(byte opt) {
        int offset = codes.size();
        codes.add(new Bytecode(offset, opt));
        if(isLocalValInc(opt)) {
            currentOpdInx++;
        }
    }

    public void addConstant(String literal, int dataType) {
        Integer index = constantTable.get(literal);

        if(index == null) {
            index = constantPool.size();
            if(dataType == Tag.DOUBLE) {
                constantPool.add(Integer.valueOf(literal));
                addCode(Opcode.dldc, index);
            } else if(dataType == Tag.INT) {
                constantPool.add(Double.valueOf(literal));
                addCode(Opcode.ildc, index);
            } else {
                if(literal.equals("true")) {
                    addCode(Opcode.iconst_1);
                } else {
                    addCode(Opcode.iconst_0);
                }
            }
        } else {

            if(dataType == Tag.DOUBLE) {
                addCode(Opcode.dldc, index);
            } else if(dataType == Tag.INT) {
                addCode(Opcode.ildc, index);
            } else {
                if(literal.equals("true")) {
                    addCode(Opcode.iconst_1);
                } else {
                    addCode(Opcode.iconst_0);
                }
            }

        }

    }

}

