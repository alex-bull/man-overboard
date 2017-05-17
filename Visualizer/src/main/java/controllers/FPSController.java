package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by mgo65 on 12/05/17.
 */
public class FPSController {

    private long startTimeNano = System.nanoTime();
    private int counter = 0;
    @FXML private Label FPSLabel;


    /**
     * Refreshes the text in the FPS label
     */
    public void refresh() {
        counter++; // increment fps counter

        // calculate fps
        long currentTimeNano = System.nanoTime();
        if (currentTimeNano > startTimeNano + 1000000000) {
            startTimeNano = System.nanoTime();
            FPSLabel.setText(String.format("FPS: %d", counter));
            counter = 0;
        }
    }

    /**
     * Called when the FPS label is clicked
     * Hides or shows the label
     */
    @FXML public void labelClicked() {

        if (FPSLabel.isVisible()) {
            FPSLabel.setVisible(false);
            return;
        }
        FPSLabel.setVisible(true);
    }
}
