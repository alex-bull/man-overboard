package controllers;

import models.Boat;
import models.Competitor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by msl47 on 10/05/17.
 */
public class TableControllerTest {
    TableController tableController;
    @Test
    public void setTable() throws Exception {
        tableController=new TableController();

        List<Competitor> competitors=new ArrayList<>();
        Boat boat1=mock(Boat.class);
        when(boat1.getTeamName()).thenReturn("First");
        when(boat1.getCurrentLegIndex()).thenReturn(1);
        competitors.add(boat1);
        Boat boat2=mock(Boat.class);
        when(boat2.getCurrentLegIndex()).thenReturn(1);
        when(boat2.getTeamName()).thenReturn("second");
        competitors.add(boat2);
        Boat boat3=mock(Boat.class);
        when(boat3.getTeamName()).thenReturn("Third");
        when(boat3.getCurrentLegIndex()).thenReturn(1);
        competitors.add(boat3);
        Boat boat4=mock(Boat.class);
        when(boat4.getCurrentLegIndex()).thenReturn(1);
        when(boat4.getTeamName()).thenReturn("Fourth");
        competitors.add(boat4);
        Boat boat5=mock(Boat.class);
        when(boat5.getTeamName()).thenReturn("Fifth");
        when(boat5.getCurrentLegIndex()).thenReturn(1);
        competitors.add(boat5);
        Boat boat6=mock(Boat.class);
        when(boat6.getCurrentLegIndex()).thenReturn(1);
        when(boat6.getTeamName()).thenReturn("sixth");
        competitors.add(boat6);

        tableController.setTable(competitors);
        tableController.printTable();


        //new order
        when(boat4.getCurrentLegIndex()).thenReturn(2);
        when(boat5.getCurrentLegIndex()).thenReturn(2);
        when(boat6.getCurrentLegIndex()).thenReturn(2);
        tableController.setTable(competitors);
        tableController.printTable();
        //new order
        when(boat1.getCurrentLegIndex()).thenReturn(2);
        when(boat2.getCurrentLegIndex()).thenReturn(2);
        when(boat3.getCurrentLegIndex()).thenReturn(2);
        tableController.setTable(competitors);
        tableController.printTable();

    }


}