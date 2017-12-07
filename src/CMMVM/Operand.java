package CMMVM;

import Lexer.Tag;

public class Operand {

    private int tag; // Tag.VARLEXPR or Tag.ARRLEXPR
    private int dataType;

    private int intVal;
    private double doubleVal;
    private int[] intArr;
    private double[] doubleArr;

    public Operand(int val) {
        this.tag = Tag.VARLEXPR;
        this.intVal = val;
        this.dataType = Tag.INT;
    }

    public Operand(double val) {
        this.tag = Tag.VARLEXPR;
        this.doubleVal = val;
        this.dataType = Tag.DOUBLE;
    }

    public Operand(int[] arr) {
        this.tag = Tag.ARRLEXPR;
        this.intArr = arr;
        this.dataType = Tag.INT;
    }

    public Operand(double[] arr) {
        this.tag = Tag.ARRLEXPR;
        this.doubleArr = arr;
        this.dataType = Tag.DOUBLE;
    }

    public int getDataType() {
        return dataType;
    }

    public int getIntVal() {
        if(dataType == Tag.DOUBLE) {
            return (int)this.doubleVal;
        } else {
            return this.intVal;
        }
    }

    public double getDoubleVal() {
        if(dataType == Tag.DOUBLE) {
            return this.doubleVal;
        } else {
            return this.intVal;
        }
    }

    public int[] getIntArr() {
        return intArr;
    }

    public double[] getDoubleArr() {
        return doubleArr;
    }


}
