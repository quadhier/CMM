package CMMVM;

import java.util.Hashtable;

// %s/= \zs\d\+\ze//g
// let n = 0 | g/= \zs\ze;/s//\=n/ | let n+=1 


public class Opcode {

    // arithmetic
    public static final byte iadd = 0;
    public static final byte dadd = 1;
    public static final byte isub = 2;
    public static final byte dsub = 3;
    public static final byte imul = 4;
    public static final byte dmul = 5;
    public static final byte idiv = 6;
    public static final byte ddiv = 7;
    public static final byte rem = 8;

    // stack operation
    public static final byte iconst_0 = 9;
    public static final byte iconst_1 = 10;
    public static final byte ildc = 11;
    public static final byte dldc = 12;
    public static final byte ipush = 13;
    public static final byte iload = 14;
    public static final byte dload = 15;
    public static final byte iaload = 16;
    public static final byte daload = 17;
    public static final byte newarray = 18;
    public static final byte istore = 19;
    public static final byte dstore = 20;
    public static final byte iastore = 21;
    public static final byte dastore = 22;

    // branch operation
    public static final byte bez = 23; // branch if equal zero
    public static final byte beo = 24; // branch if equal one
    public static final byte jmp = 25; // branch if equal one



    // compare operation
    public static final byte teq = 26;
    public static final byte tne = 27;
    public static final byte tgt = 28;
    public static final byte tlt = 29;
    public static final byte tge = 30;
    public static final byte tle = 31;

    // logical operation
    public static final byte and = 32;
    public static final byte or = 33;


    // io operation
    public static final byte iread = 34;
    public static final byte dread = 35;
    public static final byte bread = 36;
    public static final byte iwrite = 37;
    public static final byte dwrite = 38;
    public static final byte bwrite = 39;


    private Opcode() {

    }


    public static Hashtable<Byte, String> opcodeLex;
    static {
        opcodeLex = new Hashtable<>();
        opcodeLex.put(Opcode.iadd, "iadd");
        opcodeLex.put(Opcode.dadd, "dadd");
        opcodeLex.put(Opcode.isub, "isub");
        opcodeLex.put(Opcode.dsub, "dsub");
        opcodeLex.put(Opcode.imul, "imul");
        opcodeLex.put(Opcode.dmul, "dmul");
        opcodeLex.put(Opcode.idiv, "idiv");
        opcodeLex.put(Opcode.ddiv, "ddiv");
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
        opcodeLex.put(Opcode.bez, "bez"); // unary
        opcodeLex.put(Opcode.beo, "beo"); // unary
        opcodeLex.put(Opcode.jmp, "jmp"); // unary
        opcodeLex.put(Opcode.teq, "teq");
        opcodeLex.put(Opcode.tne, "tne");
        opcodeLex.put(Opcode.tgt, "tgt");
        opcodeLex.put(Opcode.tlt, "tlt");
        opcodeLex.put(Opcode.tge, "tge");
        opcodeLex.put(Opcode.tle, "tle");
        opcodeLex.put(Opcode.and, "and");
        opcodeLex.put(Opcode.or, "or");
        opcodeLex.put(Opcode.iread, "iread");
        opcodeLex.put(Opcode.dread, "dread");
        opcodeLex.put(Opcode.dread, "bread");
        opcodeLex.put(Opcode.iwrite, "iwrite");
        opcodeLex.put(Opcode.dwrite, "dwrite");
        opcodeLex.put(Opcode.dwrite, "bwrite");
    }

}
