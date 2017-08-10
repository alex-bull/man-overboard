package models;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 10/08/17.
 */
public class ForceTest {
    @Test
    public void getXAndY() throws Exception {
        Force verticalForce=new Force(10,0,false);
        assertEquals(0,verticalForce.getX(),0.001);
        assertEquals(10,verticalForce.getY(),0.001);

        Force verticalForce2=new Force(10,180,false);
        assertEquals(0,verticalForce2.getX(),0.001);
        assertEquals(-10,verticalForce2.getY(),0.001);

        Force horizontalForce=new Force(10,90,false);
        assertEquals(10,horizontalForce.getX(),0.001);
        assertEquals(0,horizontalForce.getY(),0.001);

        Force horizontalForce2=new Force(10,270,false);
        assertEquals(-10,horizontalForce2.getX(),0.001);
        assertEquals(0,horizontalForce2.getY(),0.001);

        Force force1=new Force(10,60,false);
        assertEquals(8.66,force1.getX(),0.001);
        assertEquals(5,force1.getY(),0.001);

        Force force2=new Force(10,210,false);
        assertEquals(-5,force2.getX(),0.001);
        assertEquals(-8.66,force2.getY(),0.001);

    }


    @Test
    public void getMagnitudeAndDirection() throws Exception {

        Force verticalForce=new Force(0,10,true);
        assertEquals(0,verticalForce.getDirection(),0.001);
        assertEquals(10,verticalForce.getMagnitude(),0.001);

        Force verticalForce2=new Force(0,-10,true);
        assertEquals(180,verticalForce2.getDirection(),0.001);
        assertEquals(10,verticalForce2.getMagnitude(),0.001);

        Force horizontalForce=new Force(10,0,true);
        assertEquals(90,horizontalForce.getDirection(),0.001);
        assertEquals(10,horizontalForce.getMagnitude(),0.001);

        Force horizontalForce2=new Force(-10,0,true);
        assertEquals(270,horizontalForce2.getDirection(),0.001);
        assertEquals(10,horizontalForce2.getMagnitude(),0.001);

        Force force1=new Force(8.66,5,true);
        assertEquals(60,force1.getDirection(),0.001);
        assertEquals(10,force1.getMagnitude(),0.001);

        Force force2=new Force(-5,-8.66,true);
        assertEquals(210,force2.getDirection(),0.001);
        assertEquals(10,force2.getMagnitude(),0.001);



    }



}