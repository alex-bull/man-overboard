package Elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import models.MutablePoint;

import java.util.List;

/**
 * Created by mattgoodson on 1/09/17.
 */
public class CanvasDraw {

    public static void polygon(GraphicsContext gc, List<MutablePoint> vertices, Color fill) {
        if (vertices != null) {
            gc.save();

            double[] boundaryX=new double[vertices.size()];
            double[] boundaryY=new double[vertices.size()];
            for(int i=0;i<vertices.size();i++){
                boundaryX[i]=vertices.get(i).getXValue();
                boundaryY[i]=vertices.get(i).getYValue();
            }

            gc.setLineDashes(5);
            gc.setLineWidth(0.8);
            gc.clearRect(0, 0, 4000, 4000);

            gc.strokePolygon(boundaryX, boundaryY, boundaryX.length);
            gc.setGlobalAlpha(0.4);
            gc.setFill(fill);
            //shade inside the boundary
            gc.fillPolygon(boundaryX,boundaryY, boundaryX.length);
            gc.setGlobalAlpha(1.0);
            gc.restore();
        }
    }

}
