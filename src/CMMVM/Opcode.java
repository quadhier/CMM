package CMMVM;

public class Opcode {

    // arithmetic
    public static final byte add = 0x01;
    public static final byte sub = 0x02;
    public static final byte mul = 0x03;
    public static final byte div = 0x04;
    public static final byte rem = 0x06;

    // stack operation
    public static final byte ipush = 0x07;
    public static final byte iload = 0x08;
    public static final byte dload = 0x09;
    public static final byte aload = 0x0a;
    public static final byte newarray = 0x0b;
    public static final byte store = 0x0c;
    public static final byte iastore = 0x0d;
    public static final byte dastore = 0x0e;

    // branch operation
    public static final byte beq = 0x0f; // branch if equal
    public static final byte bne = 0x10; // branch if not equal
    public static final byte bgt = 0x11; // branch if greater than
    public static final byte blt = 0x12; // branch if less than
    public static final byte bge = 0x13; // branch if greater than or equal to
    public static final byte ble = 0x14; // branch if less than or equal to

    // io
    public static final byte read = 0x15;
    public static final byte write = 0x16;


}
