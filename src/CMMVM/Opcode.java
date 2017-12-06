package CMMVM;

import java.util.Hashtable;

public class Opcode {

    // arithmetic
    public static final byte add = 0x01;
    public static final byte sub = 0x02;
    public static final byte mul = 0x03;
    public static final byte div = 0x04;
    public static final byte rem = 0x05;

    // stack operation
    public static final byte iconst_0 = 0x06;
    public static final byte iconst_1 = 0x07;
    public static final byte ildc = 0x08;
    public static final byte dldc = 0x09;
    public static final byte ipush = 0x0a;
    public static final byte iload = 0x0b;
    public static final byte dload = 0x0c;
    public static final byte iaload = 0x0d;
    public static final byte daload = 0x0e;
    public static final byte newarray = 0x0f;
    public static final byte istore = 0x10;
    public static final byte dstore = 0x11;
    public static final byte iastore = 0x12;
    public static final byte dastore = 0x13;

    // branch operation
    public static final byte beq = 0x14; // branch if equal
    public static final byte bne = 0x15; // branch if not equal
    public static final byte bgt = 0x16; // branch if greater than
    public static final byte blt = 0x17; // branch if less than
    public static final byte bge = 0x18; // branch if greater than or equal to
    public static final byte ble = 0x19; // branch if less than or equal to

    // io
    public static final byte iread = 0x1a;
    public static final byte dread = 0x1b;
    public static final byte iwrite = 0x1c;
    public static final byte dwrite = 0x1d;

    private Opcode() {

    }


    public static Hashtable<Byte, String> opcodeLex;
    static {
        opcodeLex = new Hashtable<>();
        opcodeLex.put(Opcode.add, "add");
        opcodeLex.put(Opcode.sub, "sub");
        opcodeLex.put(Opcode.mul, "mul");
        opcodeLex.put(Opcode.div, "div");
        opcodeLex.put(Opcode.rem, "rem");
        opcodeLex.put(Opcode.iconst_0, "iconst_0");
        opcodeLex.put(Opcode.iconst_1, "iconst_1");
        opcodeLex.put(Opcode.ildc, "ildc"); // unary
        opcodeLex.put(Opcode.dldc, "dldc"); // unary
        opcodeLex.put(Opcode.ipush, "ipush"); // unary
        opcodeLex.put(Opcode.iload, "iload");
        opcodeLex.put(Opcode.dload, "dload");
        opcodeLex.put(Opcode.iaload, "iaload");
        opcodeLex.put(Opcode.daload, "daload");
        opcodeLex.put(Opcode.newarray, "newarray"); // unary
        opcodeLex.put(Opcode.istore, "istore");
        opcodeLex.put(Opcode.dstore, "dstore");
        opcodeLex.put(Opcode.iastore, "iastore");
        opcodeLex.put(Opcode.dastore, "dastore");
        opcodeLex.put(Opcode.beq, "beq");
        opcodeLex.put(Opcode.bne, "bne");
        opcodeLex.put(Opcode.bgt, "bgt");
        opcodeLex.put(Opcode.blt, "blt");
        opcodeLex.put(Opcode.bge, "bge");
        opcodeLex.put(Opcode.ble, "ble");
        opcodeLex.put(Opcode.iread, "iread");
        opcodeLex.put(Opcode.dread, "dread");
        opcodeLex.put(Opcode.iwrite, "iwrite");
        opcodeLex.put(Opcode.dwrite, "dwrite");
    }

}
