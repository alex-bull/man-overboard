package controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import parsers.boatAction.BoatAction;
import utilities.DataSource;
import utilities.Interpreter;
import utilities.Sounds;
import utility.BinaryPackager;

import java.io.IOException;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.D;


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
    @FXML
    private Label goText;
    @FXML
    private Label stopText;

    @FXML
    private PerformanceController performanceController;

    private DataSource dataSource;
    private BinaryPackager binaryPackager;
    private boolean playing = false;
    private boolean zoomFlag = false;
    private AnimationTimer timer;

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


    /**
     * Handle control key events
     *
     * @param event KeyEvent
     */
    @FXML
    public void keyPressed(KeyEvent event) {
        if (raceViewController.finishFlag || raceViewController.exit) return;
        if (dataSource.isSpectating()) {
            if (event.getCode() == A) {
                if (dataSource.getZoomLevel() < 18 && raceViewController.isZoom()) {
                    dataSource.changeScaling(1);
                    raceViewController.zoomIn();
                }
            }
            else if (event.getCode() == D) {
                if (dataSource.getZoomLevel() > 12 && raceViewController.isZoom()) {
                    dataSource.changeScaling(-1);
                    raceViewController.zoomIn();
                }
            }
            return;
        }

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
                raceViewController.toggleZoom();

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
                playerController.useBoost();
                break;
            case DIGIT2:
                playerController.usePotion();
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
        this.binaryPackager = new BinaryPackager();

        if (!dataSource.isSpectating()) playerController.setup(dataSource, App.getPrimaryStage());
        else playerController.hideAll();



        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (raceViewController.finishFlag) { //game finished
                    timer.stop();
                    returnToLobby();
                }
                if (raceViewController.exit) { //quit game
                    timer.stop();
                    returnToStart();
                }
                dataSource.update();
                if (raceViewController.isLoaded()) {
                    if (!playing) playGameMusic();
                    raceViewController.refresh();
                    tableController.refresh(dataSource);
                    windController.refresh(dataSource.getWindDirection(), dataSource.getWindSpeed());
                    if (!dataSource.isSpectating()) {
                        playerController.refresh();
                        sailSlider.toFront();
                        goText.toFront();
                        stopText.toFront();

                    }
                    performanceController.refresh(dataSource.getLatency());

                    loadingPane.setVisible(false);

                } else {
                    loadingPane.toFront();
                    loadingPane.setVisible(true);

                }
            }
        };
        timer.start();

    }


    private void returnToLobby() {

        dataSource.disconnect();
        this.dataSource = null;

        //countdown
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(10)));
        timeline.play();

        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Sounds.player.stop("sounds/bensound-epic.mp3");
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("lobby.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                 //   e.printStackTrace();
                    System.exit(1);
                }

                LobbyController lobbyController = loader.getController();
                Interpreter interpreter = new Interpreter();
                interpreter.setPrimaryStage(App.getPrimaryStage());
                lobbyController.setDataSource(interpreter);
                lobbyController.begin();
                App.getScene().setRoot(root);
            }
        });
    }


    /**
     * Take the player back to the start screen
     */
    private void returnToStart() {
        //clean up first
        dataSource.send(new BinaryPackager().packageLeaveLobby());
        dataSource.disconnect();
        dataSource = null;

        Sounds.player.stop("sounds/bensound-epic.mp3");

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("start.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
           // e.printStackTrace();
        }

        assert root != null;
        StartController startController = loader.getController();
        startController.begin();
        App.getScene().setRoot(root);
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
