package seng302.Controllers;

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
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import seng302.Factories.CourseFactory;
import seng302.Factories.RaceFactory;
import seng302.Model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;

/**
 * Created by rjc249 on 5/04/17.
 * Controller for the start scene.
 */
public class StarterController implements Initializable, ClockHandler {


    @FXML private ListView<Competitor> starterList;
    @FXML private Label countdownText;
    @FXML private Text worldClockValue;
    @FXML private Button confirmButton;
    @FXML private ChoiceBox<Integer> numBoatsInput;
    @FXML private ChoiceBox<Integer> durationInput;
    @FXML private Button countdownButton;
    @FXML private Text raceStatus;

    private Clock worldClock;
    private Stage primaryStage;
    private String courseFile;
    private Race r;
    private ObservableList<Competitor> compList;
    private int numBoats;
    private Rectangle2D primaryScreenBounds;
    private final int STARTTIME = 1;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);
    private DataReceiver dataReceiver;

    /**
     * Takes an XML course file so the course information is set
     * @param courseFile String the XML courseFile
     */
    public void setCourseFile(String courseFile) {
        this.courseFile = courseFile;
    }

    /**
     * Sets the stage
     * @param primaryStage Stage the stage for this window
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Implementation of ClockHandler interface method
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime, Clock clock) {
        if(clock == worldClock) {
            worldClockValue.setText(newTime);
        }
    }

    /**
     * Initialiser for StarterController
     * @param location URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        try {
            dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
//            dataReceiver = new DataReceiver("csse-s302staff.canterbury.ac.nz", 4941);
            dataReceiver.setCanvasDimensions(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Timer receiverTimer=new Timer();
        receiverTimer.schedule(dataReceiver,0,1);

        while (dataReceiver.getCourseTimezone() == null) {
            System.out.print("");
        }

        this.worldClock = new WorldClock(this, dataReceiver.getCourseTimezone());
        worldClock.start();

        countdownText.textProperty().bind(timeSeconds.asString());
        numBoatsInput.setItems(FXCollections.observableArrayList(2, 3, 4, 5, 6));
        durationInput.setItems(FXCollections.observableArrayList(1, 5));
        compList = FXCollections.observableArrayList();

        starterList.setCellFactory(new Callback<ListView<Competitor>, ListCell<Competitor>>() {
            @Override
            public ListCell<Competitor> call(ListView<Competitor> param) {
                ListCell<Competitor> cell = new ListCell<Competitor>() {

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
                return cell;
            }
        });
        starterList.setItems(compList);
        dataReceiver.setCompetitors(compList);

    }

    /**
     * Switches from start view to course view. Called when user clicks start button.
     * Starts countdown if the list of starters is not empty.
     */
    @FXML
    void switchToCourseView(){
        // check that starter table is not empty
        if(!starterList.getItems().isEmpty()) {
            startCountdown();
            countdownButton.setDisable(true);

        }
        else {
            // inform user to press confirm
            Stage thisStage = (Stage) confirmButton.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.initOwner(thisStage);
            alert.setHeaderText(null);
            alert.setContentText("Please make sure there are competitors.");
            alert.showAndWait();
        }

    }

    /**
     * Countdown until the race start, updates the countdown time text.
     */
    private void startCountdown() {
        //count down for 5 seconds
        timeSeconds.set(STARTTIME);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(STARTTIME + 1),
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
                MainController mainController = loader.getController();
                mainController.setRace(r, dataReceiver, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), numBoats);
                primaryStage.setTitle("RaceVision");
                primaryStage.setWidth(primaryScreenBounds.getWidth());
                primaryStage.setHeight(primaryScreenBounds.getHeight());
                primaryStage.setMinHeight(primaryScreenBounds.getHeight());
                primaryStage.setMinWidth(primaryScreenBounds.getWidth());
                primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));


            }
        });
    }


    /**
     * Called when the user clicks confirm. Updates the competitors table.
     */
    @FXML
    public void confirmStream() {
        if(dataReceiver.getCompetitors().size() == 0) {
            Stage thisStage = (Stage) confirmButton.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.initOwner(thisStage);
            alert.setHeaderText(null);
            alert.setContentText("Sorry there are no competitors at the moment.");
            alert.showAndWait();
        }
        //create course
        double height = primaryScreenBounds.getHeight() * 0.8;
        Course raceCourse = new CourseFactory().createCourse(primaryScreenBounds.getWidth() * 0.70, height, courseFile);

        r = new RaceFactory().createRace(numBoats, 1, raceCourse);

        compList.setAll(dataReceiver.getCompetitors());
        raceStatus.setText(dataReceiver.getRaceStatus());
    }


    /**
     * Collects the information about the number of boats and duration of the race. Called when user clicks confirm.
     * Display the starting boat information in a listView (starterList)
     */
    @FXML
    public void collectInfo() throws Exception {

        //checks duration and number of boats have been selected
        if (numBoatsInput.getValue() == null || durationInput.getValue() == null) {
            return;
        }

        //retrieve input values
        numBoats = numBoatsInput.getValue();
        int duration = durationInput.getValue();

        //create course
        double height = primaryScreenBounds.getHeight() * 0.8;
        Course raceCourse = new CourseFactory().createCourse(primaryScreenBounds.getWidth() * 0.70, height, courseFile);

        r = new RaceFactory().createRace(numBoats, duration, raceCourse);

        compList.setAll(dataReceiver.getCompetitors());
    }
}
