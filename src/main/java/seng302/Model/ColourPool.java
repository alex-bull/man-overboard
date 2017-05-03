package seng302.Model;

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
        this.colours.add(Color.CORAL);
        this.colours.add(Color.YELLOWGREEN);
        this.colours.add(Color.MEDIUMPURPLE);
        this.colours.add(Color.RED);
        this.colours.add(Color.YELLOW);
        this.colours.add(Color.DEEPSKYBLUE);
        this.colours.add(Color.DEEPPINK);
        this.colours.add(Color.ORANGE);
        this.colours.add(Color.LIGHTSEAGREEN);
        this.colours.add(Color.BLUEVIOLET);
        this.colours.add(Color.GREEN);
        this.colours.add(Color.LAWNGREEN);
        this.colours.add(Color.DARKCYAN);


    }

    public List<Color> getColours() {
        return colours;
    }
}
