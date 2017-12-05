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
                case Opcode.add:
                    break;
                case Opcode.sub:
                    break;
                case Opcode.mul:
                    break;
                case Opcode.div:
                    break;
                case Opcode.rem:
                    break;

                // stack operation
                case Opcode.ipush:
                    break;
                case Opcode.iload:
                    break;
                case Opcode.dload:
                    break;
                case Opcode.aload:
                    break;
                case Opcode.newarray:
                    break;
                case Opcode.store:
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
                case Opcode.read:
                    break;
                case Opcode.write:
                    break;
                default:
                        break;
            }

        }
    }

}
