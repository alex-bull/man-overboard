package utilities;

import models.Force;
import models.MutablePoint;
import models.Vector;
import org.junit.Test;

import static org.junit.Assert.*;
import static utilities.CollisionUtility.calculateFinalVelocity;

/**
 * Created by khe60 on 11/08/17.
 * tests collision utilities
 */
public class CollisionUtilityTest {
    @Test
    public void calculateCollisionTest() throws Exception {
        Vector v1=new Force(1.250759173891439,8.444606039221345,true);
        Vector v2= new Force(1.4150487780311531, 8.966877802492347,true);
        Vector x1=new MutablePoint(81.89062021246276,103.71389817287886);
        Vector x2=new MutablePoint(81.89080952758869,103.7131678520832);

        assertEquals(1.1342438693586618, calculateFinalVelocity(v1,v2,x1,x2).getXValue(),0.0000001);
        assertEquals(8.89408702758722, calculateFinalVelocity(v1,v2,x1,x2).getYValue(),0.0000001);


        v1=new Force(-10,0,true);
        v2= new Force(10, 0,true);
        x1=new MutablePoint(1.0,0.0);
        x2=new MutablePoint(0.0,0.0);

        assertEquals(10, calculateFinalVelocity(v1,v2,x1,x2).getXValue(),0.0000001);
        assertEquals(-10, calculateFinalVelocity(v2,v1,x2,x1).getXValue(),0.0000001);

        v1=new Force(-10,0,true);
        v2= new Force(-5, 0,true);
        x1=new MutablePoint(1.0,0.0);
        x2=new MutablePoint(0.0,0.0);

        assertEquals(-5, calculateFinalVelocity(v1,v2,x1,x2).getXValue(),0.0000001);
        assertEquals(0, calculateFinalVelocity(v1,v2,x1,x2).getYValue(),0.0000001);
        assertEquals(-10, calculateFinalVelocity(v2,v1,x2,x1).getXValue(),0.0000001);

    }

}