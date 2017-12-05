package CMMVM;


public class ConstPool {

    private Object[] constants;

    public ConstPool(Object[] constants) {
        this.constants = constants;
    }

    public int getInt(int i) {
        return (Integer) constants[i];
    }

    public double getDouble(int i) {
        return (Double) constants[i];
    }

}
