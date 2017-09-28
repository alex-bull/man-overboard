package controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Created by khe60 on 26/09/17.
 */
public class PerformanceController {
    @FXML
    private Text performanceText;

    private int fpsCounter=0;
    private long lastFpsTime;

    public void refresh(long latency){
        if(System.currentTimeMillis()-lastFpsTime>1000){
            performanceText.setText(String.format("FPS: %d Latency: %d ms",fpsCounter,latency));
            fpsCounter=0;
            lastFpsTime=System.currentTimeMillis();
        }
        fpsCounter++;
    }

}
