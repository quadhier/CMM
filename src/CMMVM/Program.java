package CMMVM;

import Lexer.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class Program implements Serializable {

    Hashtable<String, Integer> constantTable; // record the constant pool
    ArrayList<Object> constantPool;
    ArrayList<Bytecode> codes;
    int currentOpdIdx; // only used in code generation
    int slotSize;

    // prepared for while statement
    Stack<Integer> iterAddr;
    Stack<ArrayList<Integer>> breakCollection;

    public Program() {
        constantTable = new Hashtable<>();
        constantPool = new ArrayList<>();
        codes = new ArrayList<Bytecode>();
        currentOpdIdx = 0;
        slotSize = 0;
        iterAddr = new Stack<Integer>();
        breakCollection = new Stack<>();
    }

    public ArrayList<Object> getConstantPool() {
        return constantPool;
    }

    public ArrayList<Bytecode> getCodes() {
        return codes;
    }

    public Bytecode getCode(int i) {
        if(i < codes.size())
            return codes.get(i);
        return null;
    }

    public int getCodeNum() {
        return this.codes.size();
    }

    public int getCurrentOpdInx() {
        return currentOpdIdx;
    }

    public int getSlotSize() {
        return slotSize;
    }

    public void addCode(byte opt, int opd) {
        int offset = codes.size();
        codes.add(new Bytecode(offset, opt, opd));
    }

    public void addCode(byte opt) {
        int offset = codes.size();
        codes.add(new Bytecode(offset, opt));
    }

    public void createVal() {
        currentOpdIdx++;
        if(currentOpdIdx > slotSize) {
            slotSize = currentOpdIdx;
        }
    }

    public void removeVal() {
        currentOpdIdx--;
    }

    public void addConstant(String literal, int dataType) {
        Integer index = constantTable.get(literal);

        if(index == null) {
            index = constantPool.size();
            if(dataType == Tag.DOUBLE) {
                constantPool.add(Double.valueOf(literal));
                constantTable.put(literal, index);
                addCode(Opcode.dldc, index);
            } else if(dataType == Tag.INT) {
                constantPool.add(Integer.valueOf(literal));
                constantTable.put(literal, index);
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

    public void backpatch(int codeAddr, int targetAddr) {
        switch (codes.get(codeAddr).getOpt()) {
            case Opcode.beo:
            case Opcode.bez:
            case Opcode.jmp:
                codes.get(codeAddr).setOpd(targetAddr);
                break;
            default:
                break;
        }
    }

    // backpatch for break-statement
    public void backpatch(int targetAddr) {
        ArrayList<Integer> breakList = breakCollection.peek();
        for(Integer breakAddr : breakList) {
            codes.get(breakAddr).setOpd(targetAddr);
        }
    }

    public void storeIterStart(int iterStart) {
        iterAddr.push(iterStart);
    }

    public int getIterStart() {
        return iterAddr.peek();
    }

    public void removreIterStart() {
        iterAddr.pop();
    }

    public void createBreakList() {
        breakCollection.push(new ArrayList<Integer>());
    }

    public void addBreakAddr(int breakAddr) {
        breakCollection.peek().add(breakAddr);
    }

    public void removreBreakList() {
        breakCollection.pop();
    }

    @Override
    public String toString() {
        StringBuilder prog = new StringBuilder();
        prog.append("Constant Pool:\n");
        for (int i = 0; i < constantPool.size(); i++) {
            prog.append("position: " + i + "\tliteral: ");
            prog.append(constantPool.get(i).toString() + "\n");
        }
        prog.append("\n");
        prog.append("Slot Size: " + slotSize + "\n\n");
        prog.append("Codes:\n");
        for(int i = 0; i < codes.size(); i++) {
            Bytecode bytecode = codes.get(i);
            byte opt = bytecode.getOpt();
            if(opt == Opcode.ildc || opt == Opcode.dldc || opt == Opcode.ipush
                    || opt == Opcode.bez || opt == Opcode.beo || opt == Opcode.jmp) {
                prog.append(i + ": " + Opcode.opcodeLex.get(opt) + " " + bytecode.getOpd() + "\n");
            } else if(opt == Opcode.newarray) {
                prog.append(i + ": newarray ");
                switch (bytecode.getOpd()) {
                    case Tag.DOUBLE:
                        prog.append("double" + "\n");
                        break;
                    default:
                        prog.append("int" + "\n");
                        break;
                }
            } else {
                prog.append(i + ": " + Opcode.opcodeLex.get(opt) + "\n");
            }

        }
        return prog.toString();
    }


}

