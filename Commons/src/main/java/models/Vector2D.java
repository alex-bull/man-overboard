package models;

/**
 * Created by mgo65 on 10/08/17.
 */
public class Vector2D {



    private Double x;
    private Double y;

    /**
     * Create a vector from p1 to p2
     * @param x1 p1x
     * @param y1 p1y
     * @param x2 p2x
     * @param y2 p2y
     */
    public Vector2D(Double x1, Double y1, Double x2, Double y2) {

        this.x = x2 - x1;
        this.y = y2 - y1;
    }

    /**
     * Normalises the vector
     */
    public void normalise() {

        Double magnitude = Math.sqrt((x * x) + (y * y));
        this.x = x / magnitude;
        this.y = y / magnitude;
    }


    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

}
