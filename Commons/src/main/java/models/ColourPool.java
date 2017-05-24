package models;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psu43 on 2/05/17.
 * A range of colours to store, can be used for boat colours.
 */
public class ColourPool {

    private List<Color> colours = new ArrayList<>();

    /**
     * Constructs a colour pool with colours.
     */
    public ColourPool() {


        this.colours.add(Color.rgb(0,20,168)); // ZAFFRE
        this.colours.add(Color.BLACK);
        this.colours.add(Color.BLUEVIOLET);
        this.colours.add(Color.GREEN);
        this.colours.add(Color.BROWN);
        this.colours.add(Color.GRAY.darker());


        this.colours.add(Color.MAGENTA);
        this.colours.add(Color.ORANGE);
        this.colours.add(Color.MEDIUMPURPLE);
        this.colours.add(Color.YELLOW);
        this.colours.add(Color.SEAGREEN);
        this.colours.add(Color.ROYALBLUE);
        this.colours.add(Color.LIGHTCORAL);
        this.colours.add(Color.DARKCYAN);
        this.colours.add(Color.DEEPPINK);
        this.colours.add(Color.DEEPSKYBLUE);
        this.colours.add(Color.ORANGERED);
        this.colours.add(Color.LAWNGREEN);


    }

    public List<Color> getColours() {
        return colours;
    }
}
