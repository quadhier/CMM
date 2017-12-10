package CMMVM;
import CFG.TAC;

import java.io.Serializable;


public class Bytecode implements Serializable {

    private int offset;

    private byte opt;
    private int opd;

    public Bytecode(int offset, byte opt) {
        this.offset = offset;
        this.opt = opt;
        this.opd = -1;
    }

    public Bytecode(int offset, byte opt, int opd) {
        this.offset = offset;
        this.opt = opt;
        this.opd = opd;
    }

    public int getOffset() {
        return offset;
    }

    public byte getOpt() {
        return opt;
    }

    public int getOpd() {
        return opd;
    }

    public void setOpd(int opd) {
        this.opd = opd;
    }
}
