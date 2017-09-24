package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import parsers.boatAction.BoatAction;
import utilities.DataSource;
import utilities.Sounds;
import utility.BinaryPackager;

import static javafx.scene.input.KeyCode.Q;


/**
 * Created by psu43 on 22/03/17.
 * The parent controller for the view
 */
public class MainController {

    @FXML
    private TableController tableController;
    @FXML
    private RaceViewController raceViewController;
    @FXML
    private SplitPane splitPane;
    @FXML
    private WindController windController;
    @FXML
    private PlayerController playerController;
    @FXML
    private GridPane loadingPane;
    @FXML
    private Slider sailSlider;
    @FXML
    private TimerController timerController;
    private DataSource dataSource;
    private BinaryPackager binaryPackager;
    private boolean playing = false;
    private boolean flag = false;


    /**
     * updates the slider and sends corresponding packet
     */
    @FXML
    public void updateSlider() {
        if (sailSlider.getValue() < 0.5) {
            this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.SAILS_OUT.getValue(), dataSource.getSourceID()));
        } else {
            this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.SAILS_IN.getValue(), dataSource.getSourceID()));
        }
    }

    @FXML
    public void zoomOut(KeyEvent event){
        if (event.getCode()==Q){
            raceViewController.zoomIn();
        }
    }

    /**
     * Handle control key events
     *
     * @param event KeyEvent
     */
    @FXML
    public void keyPressed(KeyEvent event) {
//        System.out.println("key pressed "+System.currentTimeMillis());

        switch (event.getCode()) {
            case W:
                this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.UPWIND.getValue(), dataSource.getSourceID()));
                break;
            case S:
                this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.DOWNWIND.getValue(), dataSource.getSourceID()));
                break;
            case SPACE:
//                    this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.VMG.getValue(), dataSource.getSourceID()));
                break;
            case SHIFT:
                this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.SWITCH_SAILS.getValue(), dataSource.getSourceID()));
                sailSlider.setValue(this.dataSource.getCompetitor().getSailValue());
                break;
            case ENTER:
                this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.TACK_GYBE.getValue(), dataSource.getSourceID()));
                break;
            case Q:
                raceViewController.zoomOut();

                if (!tableController.isVisible()) {
                    tableController.makeVisible();
                }
                break;
            case BACK_QUOTE:
                if (raceViewController.isZoom() && tableController.isVisible()) {
                    tableController.makeInvisible();
                } else if (raceViewController.isZoom()) {
                    tableController.makeVisible();
                }
                break;

            case A:
                if (dataSource.getZoomLevel() < 18 && raceViewController.isZoom()) {
                    dataSource.changeScaling(1);
                    raceViewController.zoomIn();
                }
                break;
            case D:
                if (dataSource.getZoomLevel() > 13 && raceViewController.isZoom()) {
                    dataSource.changeScaling(-1);
                    raceViewController.zoomIn();
                }
                break;
            case DIGIT1:
                if (dataSource.getCompetitor().hasSpeedBoost()) {
                    this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.BOOST.getValue(), dataSource.getSourceID()));
                    dataSource.getCompetitor().disableBoost();
                    playerController.greyOutBoost();
                }
                break;
            case DIGIT2:
                if (dataSource.getCompetitor().hasPotion()) {
                    this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.POTION.getValue(), dataSource.getSourceID()));
                    dataSource.getCompetitor().usePotion();
                    playerController.greyOutPotion();
                }
                break;
        }
    }


    /**
     * Begins the race loop which updates child controllers at ~60fps
     *
     * @param dataSource DataSource the data to display
     * @param width      double the screen width
     * @param height     double the screen height
     */
    void beginRace(DataSource dataSource, double width, double height) {
        this.dataSource = dataSource;
        raceViewController.begin(width, height, dataSource);
        timerController.begin(dataSource);
        tableController.addObserver(raceViewController);
        playerController.setup(dataSource, App.getPrimaryStage());
        this.binaryPackager = new BinaryPackager();


        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                dataSource.update();
                if (raceViewController.isLoaded()) {
                    if (!playing) playGameMusic();
                    raceViewController.refresh();
                    tableController.refresh(dataSource);
                    windController.refresh(dataSource.getWindDirection(), dataSource.getWindSpeed());
                    playerController.refresh();
                    sailSlider.toFront();
                    loadingPane.setVisible(false);
                    if (!flag) {
                        raceViewController.toggleZoom();
                        flag = true;
                    }
                } else {
                    loadingPane.toFront();
                    loadingPane.setVisible(true);

                }
            }
        };
        timer.start();

    }


    /**
     * Play the game music loop
     */
    private void playGameMusic() {
        Sounds.player.loopMP3("sounds/bensound-epic.mp3");
        Sounds.player.setVolume("sounds/bensound-epic.mp3", 0.5);
        playing = true;
    }
}
