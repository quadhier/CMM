package CMMVM;

public class Operand {

    private int tag;
    private int dataType;

    private int intVal;
    private double doublVal;
    //private boolean boolVal;
    private Object arrVal;

    public Operand(int tag, int val) {
        this.tag = tag;
        this.intVal = val;
    }

    public Operand(int tag, double val) {
        this.tag = tag;
        this.doublVal = val;
    }

    public Operand(int tag, boolean val) {
        this.tag = tag;
        //this.boolVal = val;
    }

    public int getTag() {
        return this.tag;
    }

    public int getDataType() {
        return dataType;
    }

    public int getIntVal() {
        return this.intVal;
    }

    public double getDoublVal() {
        return this.doublVal;
    }

//    public boolean getBoolVal() {
//        return this.boolVal;
//    }

}
