package controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import models.Clock;
import models.ClockHandler;
import models.Competitor;
import models.WorldClock;
import parsers.RaceStatusEnum;
import utilities.DataSource;
import utilities.EnvironmentConfig;
import utilities.StreamObserver;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


/**
 * Created by rjc249 on 5/04/17.
 * Controller for the start scene.
 */
public class LobbyController implements Initializable, ClockHandler, StreamObserver {

    private DataSource dataSource;
    private final int STARTTIME = 1;
    @FXML private ListView<String> starterList;
    @FXML private Label worldClockValue;
    @FXML private Button confirmButton;
    @FXML private Label countDownLabel;
    @FXML private TextField hostField;
    @FXML private ProgressIndicator progressIndicator;
    private Clock worldClock;
    private Stage primaryStage;
    private ObservableList<String> competitorList = FXCollections.observableArrayList();
    private Rectangle2D primaryScreenBounds;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);
    private AnimationTimer timer;

    /**
     * Sets the stage
     * @param primaryStage Stage the stage for this window
     */
    void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Implementation of ClockHandler interface method
     * @param newTime The currentTime of the clock
     *
     */
    public void clockTicked(String newTime, Clock clock) {
        if (clock == worldClock) {
            worldClockValue.setText(newTime);
        }
    }


    /**
     * Initialiser for LobbyController
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressIndicator.setVisible(false);
        primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        starterList.setItems(competitorList);

    }


    /**
     * Called when the user clicks confirm.
     * Begins streaming data from the selected server
     */
    @FXML
    public void confirmStream() {

        //get the selected stream
        String host = this.hostField.getText();
        if (host == null || host.equals("")) {
            System.out.println("Invalid host");
            return;
        }
        progressIndicator.setVisible(true);
        this.hostField.setDisable(true);
        this.confirmButton.setDisable(true);

        Scene scene=primaryStage.getScene();
        this.dataSource.receive(host, EnvironmentConfig.port, scene, this);

    }


    /**
     * StreamObserver method
     * Alerts the user if the connection failed
     */
    public void streamFailed() {
        this.progressIndicator.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection to server failed");
        alert.setHeaderText(null);
        alert.setContentText("Sorry, cannot connect to this game right now.");
        alert.showAndWait();
        this.hostField.setDisable(false);
        this.confirmButton.setDisable(false);

    }


    /**
     * StreamObserver method
     * Change to the raceView upon preparatory signal
     */
    public void raceStatusUpdated(RaceStatusEnum status) {

        System.out.println("Status updated " + status.toString());

        if (status == RaceStatusEnum.PREPARATORY) {
            this.timer.stop();
            this.loadRaceView();
        }
    }


    /**
     * StreamObserver method
     * Updates the list with the new boats
     */
    public void boatsUpdated() {
        this.progressIndicator.setVisible(false);
        this.competitorList.clear();
        this.competitorList.addAll(dataSource.getCompetitorsPosition().stream().map(Competitor::getTeamName).collect(Collectors.toList()));
    }



    /**
     * Countdown until the race start, updates the countdown time text.
     * Loads the raceView
     */
    private void loadRaceView() {

        //count down for 5 seconds
        countDownLabel.textProperty().bind(timeSeconds.asString());

        timeSeconds.set(STARTTIME);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(STARTTIME),
                        new KeyValue(timeSeconds, 0)));
        timeline.play();

        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            //after 5 seconds, load race course view
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert root != null;
                Scene scene = new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());

                MainController mainController = loader.getController();

                mainController.beginRace(dataSource, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
                primaryStage.setTitle("Man Overboard");
//                primaryStage.setMaxWidth(primaryScreenBounds.getWidth());
//                primaryStage.setMaxHeight(primaryScreenBounds.getHeight());
                primaryStage.setScene(scene);

            }
        });
    }


    /**
     * Continuously polls the datasource to update the view
     * Uses an animation timer as it is updating the GUI thread
     */
    void loop() {

        this.timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                boatsUpdated();
            }
        };

        timer.start();
    }


}
