package CMMVM;
import CFG.TAC;


public class Bytecode {

    int offset;

    byte opt;
    int opd;

    public Bytecode(int offset, byte opt, int opd) {
        this.offset = offset;
        this.opt = opt;
        this.opd = opd;
    }

    public int getOffset() {
        return offset;
    }

    public int getOpt() {
        return opt;
    }

    public int getOpd() {
        return opd;
    }

}
