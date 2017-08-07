package controllers;

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
import utilities.DataSource;
import utilities.EnvironmentConfig;
import utilities.StreamDelegate;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by rjc249 on 5/04/17.
 * Controller for the start scene.
 */
public class StarterController implements Initializable, ClockHandler, StreamDelegate {

    private DataSource dataSource;
    private final int STARTTIME = 1;
    @FXML private ListView<Competitor> starterList;
    @FXML private Label worldClockValue;
    @FXML private Button confirmButton;
    @FXML private Label countDownLabel;
    @FXML private TextField hostField;
    @FXML private ProgressIndicator progressIndicator;
    private Clock worldClock;
    private Stage primaryStage;
    private ObservableList<Competitor> compList;
    private Rectangle2D primaryScreenBounds;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);

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
     *
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime, Clock clock) {
        if (clock == worldClock) {
            worldClockValue.setText(newTime);
        }
    }

    /**
     * Initialiser for StarterController
     *
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressIndicator.setVisible(false);
        primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        compList = FXCollections.observableArrayList();
        starterList.setCellFactory(new Callback<ListView<Competitor>, ListCell<Competitor>>() {
            @Override
            public ListCell<Competitor> call(ListView<Competitor> param) {
                return new ListCell<Competitor>() {

                    @Override
                    protected void updateItem(Competitor boat, boolean empty) {
                        super.updateItem(boat, empty);
                        if (boat != null) {
                            setText(boat.getTeamName() + ": " + boat.getVelocity() + " m/s");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        starterList.setItems(compList);
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
     * StreamDelegate method
     * Alerts the user if the connection failed
     */
    public void streamFailed() {
        this.progressIndicator.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection Failed");
        alert.setHeaderText(null);
        alert.setContentText("Sorry, cannot connect to this stream right now.");
        alert.showAndWait();
        this.hostField.setDisable(false);
        this.confirmButton.setDisable(false);

    }


    /**
     * StreamDelegate method
     * Call set fields
     * Change to the raceView
     */
    public void streamStarted() {

        System.out.println("Streaming data");
        progressIndicator.setVisible(false);
        this.setFields();
        this.loadRaceView();
    }




    /**
     * Set fields using data from the stream
     */
    private void setFields() {


        while (dataSource.getCourseTimezone() == null) {
            System.out.print("");
        }
        this.worldClock = new WorldClock(this, dataSource.getCourseTimezone());
        worldClock.start();
        compList.setAll(dataSource.getCompetitorsPosition());

        if (dataSource.getCompetitorsPosition().size() == 0) {
            Stage thisStage = (Stage) confirmButton.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.initOwner(thisStage);
            alert.setHeaderText(null);
            alert.setContentText("Sorry, this data stream hasn't started.");
            alert.showAndWait();
        }
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
                primaryStage.setTitle("RaceVision");
//                primaryStage.setMaxWidth(primaryScreenBounds.getWidth());
//                primaryStage.setMaxHeight(primaryScreenBounds.getHeight());
                primaryStage.setScene(scene);
                primaryStage.setFullScreen(true);
            }
        });
    }

}
