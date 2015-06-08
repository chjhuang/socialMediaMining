package ucas.dataMining.dao;

/**
 * 
 * To change this template use File | Settings | File Templates.
 */
public class Instance {
    public int label;
    public int[] x;

    public Instance(int label, int[] x) {
        this.label = label;
        this.x = x;
    }

    public int getLabel() {
        return label;
    }

    public int[] getX() {
        return x;
    }
}
