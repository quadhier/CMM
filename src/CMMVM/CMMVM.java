package CMMVM;

import java.util.ArrayList;
import java.util.Stack;

public class CMMVM {

    // all the instructions
    ArrayList<Bytecode> prog;
    // runtime stack,
    // as there is no function definition temporarily
    // it has only one frame
    Stack<ActRecord> rtStack;
    // current stack
    ActRecord currentActRecord;

    ConstPool constantPool;

    public CMMVM(ArrayList<Bytecode> prog) {
        this.prog = prog;
        rtStack = new Stack<ActRecord>();
        currentActRecord = new ActRecord();
        rtStack.push(currentActRecord);
    }

    public void execute() {

        int ip = 0;

        while(ip < prog.size()) {

            Bytecode inst = prog.get(ip);

            switch (inst.getOpt()) {
                // arithmetic
                case Opcode.iadd:
                    break;
                case Opcode.dadd:
                    break;
                case Opcode.isub:
                    break;
                case Opcode.dsub:
                    break;
                case Opcode.imul:
                    break;
                case Opcode.dmul:
                    break;
                case Opcode.idiv:
                    break;
                case Opcode.rem:
                    break;

                // stack operation
                case Opcode.iconst_0:
                    break;
                case Opcode.iconst_1:
                        break;
                case Opcode.ildc:
                    break;
                case Opcode.dldc:
                    break;
                case Opcode.ipush:
                    break;
                case Opcode.iload:
                    break;
                case Opcode.dload:
                    break;
                case Opcode.iaload:
                    break;
                case Opcode.daload:
                    break;
                case Opcode.newarray:
                    break;
                case Opcode.istore:
                    break;
                case Opcode.dstore:
                    break;
                case Opcode.iastore:
                    break;
                case Opcode.dastore:
                    break;

                // branch operation
                case Opcode.beq:
                    break;
                case Opcode.bne:
                    break;
                case Opcode.bgt:
                    break;
                case Opcode.blt:
                    break;
                case Opcode.bge:
                    break;
                case Opcode.ble:
                    break;

                // io operation
                case Opcode.iread:
                    break;
                case Opcode.dread:
                    break;
                case Opcode.iwrite:
                    break;
                case Opcode.dwrite:
                    break;
                default:
                        break;
            }

        }
    }

}
