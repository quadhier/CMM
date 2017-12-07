package CMMVM;

import Lexer.Tag;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class CMMVM {

    // all the instructions
    Program program;
    // runtime stack,
    // as there is no function definition temporarily
    // it has only one frame
    Stack<ActRecord> rtStack;
    // current stack
    ActRecord currRecd;

    //ConstPool constantPool;
    Object[] constantPool;

    public CMMVM(Program program) {
        this.program = program;
        rtStack = new Stack<ActRecord>();
        currRecd = new ActRecord(program.getSlotSize());
        rtStack.push(currRecd);
        constantPool = program.getConstantPool().toArray();
    }

    public void execute() {

        int ip = 0;
        boolean jmp = true;
        int target = 0;

        while(ip < program.getCodeNum()) {

            Bytecode inst = program.getCode(ip);

            int iopd1;
            int iopd2;
            double dopd1;
            double dopd2;
            int slotIdx;
            int[] iarr;
            double[] darr;
            int eleIdx;
            int arrSize;
            Operand op1;
            Operand op2;
            Scanner sc;
            switch (inst.getOpt()) {

                // arithmetic
                case Opcode.iadd:
                    iopd2 = currRecd.popOpd().getIntVal();
                    iopd1 = currRecd.popOpd().getIntVal();
                    currRecd.pushOpd(new Operand(iopd1 + iopd2));
                    break;
                case Opcode.dadd:
                    dopd2 = currRecd.popOpd().getDoubleVal();
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    currRecd.pushOpd(new Operand(dopd1 + dopd2));
                    break;
                case Opcode.isub:
                    iopd2 = currRecd.popOpd().getIntVal();
                    iopd1 = currRecd.popOpd().getIntVal();
                    currRecd.pushOpd(new Operand(iopd1 - iopd2));
                    break;
                case Opcode.dsub:
                    dopd2 = currRecd.popOpd().getDoubleVal();
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    currRecd.pushOpd(new Operand(dopd1 - dopd2));
                    break;
                case Opcode.imul:
                    iopd2 = currRecd.popOpd().getIntVal();
                    iopd1 = currRecd.popOpd().getIntVal();
                    currRecd.pushOpd(new Operand(iopd1 * iopd2));
                    break;
                case Opcode.dmul:
                    dopd2 = currRecd.popOpd().getDoubleVal();
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    currRecd.pushOpd(new Operand(dopd1 * dopd2));
                    break;
                case Opcode.ddiv:
                    dopd2 = currRecd.popOpd().getDoubleVal();
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    currRecd.pushOpd(new Operand(dopd1 / dopd2));
                    break;
                case Opcode.idiv:
                    iopd2 = currRecd.popOpd().getIntVal();
                    iopd1 = currRecd.popOpd().getIntVal();
                    // divided by zero
                    if(iopd2 == 0) {
                        System.err.println("Divided By Zero");
                        System.exit(-1);
                    }
                    currRecd.pushOpd(new Operand(iopd1 / iopd2));
                    break;
                case Opcode.rem:
                    iopd2 = currRecd.popOpd().getIntVal();
                    iopd1 = currRecd.popOpd().getIntVal();
                    if(iopd2 == 0) {
                        System.err.println("Divided By Zero");
                        System.exit(-1);
                    }
                    currRecd.pushOpd(new Operand(iopd1 % iopd2));
                    break;

                // stack operation
                case Opcode.iconst_0:
                    currRecd.pushOpd(new Operand(0));
                    break;
                case Opcode.iconst_1:
                    currRecd.pushOpd(new Operand(1));
                    break;
                case Opcode.ildc: // unary
                    currRecd.pushOpd(new Operand((Integer) constantPool[inst.getOpd()]));
                    break;
                case Opcode.dldc: // unary
                    currRecd.pushOpd(new Operand((Double) constantPool[inst.getOpd()]));
                    break;
                case Opcode.ipush: // unary
                    currRecd.pushOpd(new Operand(inst.getOpd()));
                    break;

                // iload and dload have no difference
                case Opcode.iload:
                    slotIdx = currRecd.popOpd().getIntVal();
                    currRecd.pushOpd(currRecd.getLocalVal(slotIdx));
                    break;
                case Opcode.dload:
                    slotIdx = currRecd.popOpd().getIntVal();
                    currRecd.pushOpd(currRecd.getLocalVal(slotIdx));
                    break;

                case Opcode.iaload:
                    slotIdx = currRecd.popOpd().getIntVal();
                    eleIdx = currRecd.popOpd().getIntVal();
                    iarr = currRecd.getLocalVal(slotIdx).getIntArr();
                    if(eleIdx >= iarr.length) {
                        System.err.println("Array Out Of Bound");
                        System.exit(-1);
                    }
                    currRecd.pushOpd(new Operand(iarr[eleIdx]));
                    break;
                case Opcode.daload:
                    slotIdx = currRecd.popOpd().getIntVal();
                    eleIdx = currRecd.popOpd().getIntVal();
                    darr = currRecd.getLocalVal(slotIdx).getDoubleArr();
                    if(eleIdx >= darr.length) {
                        System.err.println("Array Out Of Bound");
                        System.exit(-1);
                    }
                    currRecd.pushOpd(new Operand(darr[eleIdx]));
                    break;
                case Opcode.newarray: // unary
                    arrSize  = currRecd.popOpd().getIntVal();
                    slotIdx = currRecd.popOpd().getIntVal();
                    if(inst.getOpd() == Tag.DOUBLE) {
                        currRecd.storeLocalVal(slotIdx, new Operand(new double[arrSize]));
                    } else {
                        currRecd.storeLocalVal(slotIdx, new Operand(new int[arrSize]));
                    }
                    break;
                case Opcode.istore:
                    iopd1 = currRecd.popOpd().getIntVal();
                    slotIdx = currRecd.popOpd().getIntVal();
                    currRecd.storeLocalVal(slotIdx, new Operand(iopd1));
                    break;
                case Opcode.dstore:
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    slotIdx = currRecd.popOpd().getIntVal();
                    currRecd.storeLocalVal(slotIdx, new Operand(dopd1));
                    break;
                case Opcode.iastore:
                    iopd1 = currRecd.popOpd().getIntVal();
                    slotIdx = currRecd.popOpd().getIntVal();
                    eleIdx = currRecd.popOpd().getIntVal();
                    if(currRecd.storeLocalArrEle(slotIdx, eleIdx, iopd1) == -1) {
                        System.err.println("Array Out Of Bound");
                        System.exit(-1);
                    }
                    break;
                case Opcode.dastore:
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    slotIdx = currRecd.popOpd().getIntVal();
                    eleIdx = currRecd.popOpd().getIntVal();
                    if(currRecd.storeLocalArrEle(slotIdx, eleIdx, dopd1) == -1) {
                        System.err.println("Array Out Of Bound");
                        System.exit(-1);
                    }
                    break;

                // branch operation
                case Opcode.beo: // unary
                    iopd1 = currRecd.popOpd().getIntVal();
                    if(iopd1 == 1) {
                        jmp = true;
                        target = inst.getOpd();
                    }
                    break;
                case Opcode.bez: // unary
                    iopd1 = currRecd.popOpd().getIntVal();
                    if(iopd1 == 0) {
                        jmp = true;
                        target = inst.getOpd();
                    }
                    break;
                case Opcode.jmp: // unary
                    jmp = true;
                    target = inst.getOpd();
                    break;

                /* datatype is only used here */
                // compare operation
                case Opcode.teq:
                    op2 = currRecd.popOpd();
                    op1 = currRecd.popOpd();
                    if(op1.getDataType() == Tag.DOUBLE || op2.getDataType() == Tag.DOUBLE) {
                        if(op1.getDoubleVal() == op2.getDoubleVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    } else {
                        if(op1.getIntVal() == op2.getIntVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    }
                    break;
                case Opcode.tne:
                    op2 = currRecd.popOpd();
                    op1 = currRecd.popOpd();
                    if(op1.getDataType() == Tag.DOUBLE || op2.getDataType() == Tag.DOUBLE) {
                        if(op1.getDoubleVal() != op2.getDoubleVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    } else {
                        if(op1.getIntVal() != op2.getIntVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    }
                    break;
                case Opcode.tgt:
                    op2 = currRecd.popOpd();
                    op1 = currRecd.popOpd();
                    if(op1.getDataType() == Tag.DOUBLE || op2.getDataType() == Tag.DOUBLE) {
                        if(op1.getDoubleVal() > op2.getDoubleVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    } else {
                        if(op1.getIntVal() > op2.getIntVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    }
                    break;
                case Opcode.tlt:
                    op2 = currRecd.popOpd();
                    op1 = currRecd.popOpd();
                    if(op1.getDataType() == Tag.DOUBLE || op2.getDataType() == Tag.DOUBLE) {
                        if(op1.getDoubleVal() < op2.getDoubleVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    } else {
                        if(op1.getIntVal() < op2.getIntVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    }
                    break;
                case Opcode.tge:
                    op2 = currRecd.popOpd();
                    op1 = currRecd.popOpd();
                    if(op1.getDataType() == Tag.DOUBLE || op2.getDataType() == Tag.DOUBLE) {
                        if(op1.getDoubleVal() >= op2.getDoubleVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    } else {
                        if(op1.getIntVal() >= op2.getIntVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    }
                    break;
                case Opcode.tle:
                    op2 = currRecd.popOpd();
                    op1 = currRecd.popOpd();
                    if(op1.getDataType() == Tag.DOUBLE || op2.getDataType() == Tag.DOUBLE) {
                        if(op1.getDoubleVal() <= op2.getDoubleVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    } else {
                        if(op1.getIntVal() <= op2.getIntVal()) {
                            currRecd.pushOpd(new Operand(1));
                        } else {
                            currRecd.pushOpd(new Operand(0));
                        }
                    }
                    break;

                // logical operation
                case Opcode.and:
                    iopd2 = currRecd.popOpd().getIntVal();
                    iopd1 = currRecd.popOpd().getIntVal();
                    if(iopd1 == 1 && iopd2 == 1) {
                        currRecd.pushOpd(new Operand(1));
                    } else {
                        currRecd.pushOpd(new Operand(0));
                    }
                    break;
                case Opcode.or:
                    iopd1 = currRecd.popOpd().getIntVal();
                    iopd2 = currRecd.popOpd().getIntVal();
                    if(iopd1 == 1 || iopd2 == 1) {
                        currRecd.pushOpd(new Operand(1));
                    } else {
                        currRecd.pushOpd(new Operand(0));
                    }
                    break;

                // io operation
                case Opcode.iread:
                    sc = new Scanner(System.in);
                    iopd1 = sc.nextInt();
                    currRecd.pushOpd(new Operand(iopd1));
                    break;
                case Opcode.dread:
                    sc = new Scanner(System.in);
                    dopd1 = sc.nextDouble();
                    currRecd.pushOpd(new Operand(dopd1));
                    break;
                case Opcode.iwrite:
                    iopd1 = currRecd.popOpd().getIntVal();
                    System.out.println(iopd1);
                    break;
                case Opcode.dwrite:
                    dopd1 = currRecd.popOpd().getDoubleVal();
                    System.out.println(dopd1);
                    break;
                default:
                    break;
            }

            if(jmp == true) {
                jmp = false;
                ip = target;
            } else {
                ip++;
            }

        }
    }

}
