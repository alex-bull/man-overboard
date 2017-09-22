package utilities;

/**
 * Created by psu43 on 20/07/17.
 * Array container.
 */
public class ArrayContainer {

    private double[] x;

    private double[] y;

    /**
     * Constructor for an array container
     *
     * @param x double[] array of x values
     * @param y double[] array of y values
     */
    public ArrayContainer(double[] x, double[] y) {
        this.x = x;
        this.y = y;
    }


    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }


}
