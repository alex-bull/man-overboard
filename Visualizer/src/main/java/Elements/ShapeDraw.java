package Elements;

import com.google.common.collect.Iterables;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import models.MutablePoint;

import java.util.List;

/**
 * Created by mattgoodson on 1/09/17.
 * A tool for drawing shapes in JavaFx
 */
public class ShapeDraw {

    /**
     * Draw a polygon on a canvas
     *
     * @param gc       GraphicsContext, where to draw the polygon
     * @param vertices List the vertices of the polygon
     * @param fill     Color the color of the polygon fill
     */
    public static void polygon(GraphicsContext gc, List<MutablePoint> vertices, Color fill) {
        if (vertices != null) {
            gc.save();

            double[] boundaryX = new double[vertices.size()];
            double[] boundaryY = new double[vertices.size()];
            for (int i = 0; i < vertices.size(); i++) {
                boundaryX[i] = vertices.get(i).getXValue();
                boundaryY[i] = vertices.get(i).getYValue();
            }

            gc.setLineDashes(5);
            gc.setLineWidth(0.8);
            gc.clearRect(0, 0, 4000, 4000);

            gc.strokePolygon(boundaryX, boundaryY, boundaryX.length);
            gc.setGlobalAlpha(0.4);
            gc.setFill(fill);
            //shade inside the boundary
            gc.fillPolygon(boundaryX, boundaryY, boundaryX.length);
            gc.setGlobalAlpha(1.0);
            gc.restore();
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
