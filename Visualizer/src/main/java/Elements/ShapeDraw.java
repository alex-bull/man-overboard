package Elements;

import com.google.common.collect.Iterables;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import models.MutablePoint;

import java.util.List;

/**
 * Created by mattgoodson on 1/09/17.
 * A tool for drawing shapes in JavaFx
 */
public class ShapeDraw {

    /**
     * redraw a movePolygon on a canvas
     *
     * @param polygon the movePolygon too be redrawn
     * @param vertices List the vertices of the movePolygon
     */
    public static void movePolygon(Polygon polygon, Double[] vertices) {
        if (vertices != null) {

            polygon.getPoints().setAll(vertices);


        }
    }

    /**
     * Draws a line using the given coordinates
     *
     * @param line Line, the object to update
     * @param p1   MutablePoint, the start of the line
     * @param p2   MutablePoint, the end of the line
     */
    public static void line(Line line, MutablePoint p1, MutablePoint p2) {

        double x1 = p1.getXValue();
        double y1 = p1.getYValue();
        double x2 = p2.getXValue();
        double y2 = p2.getYValue();
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
    }

}
