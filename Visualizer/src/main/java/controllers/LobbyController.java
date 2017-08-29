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
import javafx.util.Duration;
import models.Competitor;
import parsers.RaceStatusEnum;
import utilities.DataSource;
import utilities.EnvironmentConfig;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Created by rjc249 on 5/04/17.
 * Controller for the start scene.
 */
public class LobbyController implements Initializable {

    private DataSource dataSource;
    private final int STARTTIME = 10;
    @FXML private ListView<String> starterList;
    @FXML private Button readyButton;
    @FXML private Label countdownLabel;
    @FXML private Button leaveButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label gameStartLabel;
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
     * Begins connection to server
     * Continuously polls the datasource to update the view
     * Uses an animation timer as it is updating the GUI thread
     */
    void begin() {

        Scene scene=primaryStage.getScene();
        boolean connected = this.dataSource.receive(EnvironmentConfig.host, EnvironmentConfig.port, scene);
        if (!connected) {
            System.out.println("Failed to connect to server");
            return;
        }

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateList();
                checkStatus();
            }
        };
        timer.start();
    }



    /**
     * Initialiser for LobbyController
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressIndicator.setVisible(true);
        countdownLabel.setText("");
        gameStartLabel.setVisible(false);
        primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        starterList.setItems(competitorList);

    }


    /**
     * Called when the ready button is pressed
     * Tell server that player is ready
     */
    @FXML
    public void playerReady() {

    }


    /**
     * Called when leave lobby is pressed
     * Tell server that player has left and exit lobby
     */
    @FXML
    public void leaveLobby() {

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
    }



    /**
     * Check the current race status
     * Change to the raceView upon started signal
     */
    public void checkStatus() {

        if (dataSource.getRaceStatus() == RaceStatusEnum.STARTED) {
            System.out.println("game beginning...");
            this.loadRaceView();
        }
    }


    /**
     * Updates the list with the competitors in the datasource
     */
    private void updateList() {
        this.progressIndicator.setVisible(false);
        this.competitorList.clear();
        this.competitorList.addAll(dataSource.getCompetitorsPosition().stream().map(Competitor::getTeamName).collect(Collectors.toList()));
    }



    /**
     * Countdown until the race start, updates the countdown time text.
     * Loads the raceView
     */
    private void loadRaceView() {

        this.timer.stop(); //cancel the animation timer
        this.leaveButton.setDisable(true); //cant leave once game is starting
        this.readyButton.setDisable(true);

        //count down for 5 seconds
        gameStartLabel.setVisible(true);
        countdownLabel.textProperty().bind(timeSeconds.asString());
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
                primaryStage.setScene(scene);
            }
        });
    }





}
