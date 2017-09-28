package controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.util.Duration;
import models.Competitor;
import parsers.RaceStatusEnum;
import utilities.DataSource;
import utilities.EnvironmentConfig;
import utilities.Sounds;
import utility.BinaryPackager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static parsers.xml.race.ThemeEnum.*;


/**
 * Created by rjc249 on 5/04/17.
 * Controller for the start scene.
 */
public class LobbyController implements Initializable {

    private final int STARTTIME = 5;
    BinaryPackager binaryPackager = new BinaryPackager();
    private DataSource dataSource;
    @FXML private ListView<String> starterList;
    @FXML private Button readyButton;
    @FXML private Label countdownLabel;
    @FXML private Button leaveButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label gameStartLabel;
    @FXML private ImageView boatImageView;
    @FXML private ImageView courseImageView;
    @FXML private Label locationLabel;
    @FXML private Label gameTypeLabel;
    @FXML private Label loadingLabel;
    @FXML private GridPane gameGridPane;
    @FXML private GridPane playerGridPane;
    @FXML private TextField nameText;
    @FXML private Button confirmButton;
    @FXML private Button leftButton;
    @FXML private Button rightButton;
    private Image yacht;
    private Image cog;
    private Image frigate;
    private Image galleon;
    private Image boat;
    private Image cat;
    private Image pirate;
    private ArrayList<Image> boatImages = new ArrayList<>();
    private Integer index = 0;
    private ObservableList<String> competitorList = FXCollections.observableArrayList();
    private Rectangle2D primaryScreenBounds;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);
    private AnimationTimer timer;
    private boolean boatSelected = false;
    private Image courseThemeImage;
    private  Boolean connected = false;

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Begins connection to server
     * Continuously tries to connect on background thread
     */
    void begin() {

        Scene scene = App.getScene();

        //start sound loop
        Sounds.player.loopMP3WithFadeIn("sounds/bensound-instinct.mp3", 4);

        //Connect to a game in the background
        Task connect = new Task() {

            @Override
            public Boolean call() {
                boolean connected = dataSource.receive(EnvironmentConfig.host, EnvironmentConfig.port, scene);
                while (!connected) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    connected = dataSource.receive(EnvironmentConfig.host, EnvironmentConfig.port, scene);
                }
                return true;
            }
        };

        connect.setOnSucceeded(event -> this.loop());
        Thread connection = new Thread(connect);
        connection.start();
    }


    /**
     * Start the main loop
     * Continuously polls the datasource to update the view
     * Uses an animation timer as it is updating the GUI thread
     */
    private void loop() {
        connected = true;
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                dataSource.update();
                updateList();
                checkStatus();
                updateCourseImage();
            }
        };
        timer.start();
    }


    /**
     * Initialiser for LobbyController
     *
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressIndicator.setVisible(true);
        loadingLabel.setVisible(true);
        countdownLabel.setText("");
        gameStartLabel.setVisible(false);
        primaryScreenBounds = Screen.getPrimary().getBounds();
        starterList.setItems(competitorList);

        yacht = new Image(getClass().getClassLoader().getResource("images/boats/yachtLandscape.png").toString());
        cog = new Image(getClass().getClassLoader().getResource("images/boats/cogLandscape.png").toString());
        frigate = new Image(getClass().getClassLoader().getResource("images/boats/frigateLandscape.png").toString());
        galleon = new Image(getClass().getClassLoader().getResource("images/boats/galleonLandscape.png").toString());
        boat = new Image(getClass().getClassLoader().getResource("images/boats/boatLandscape.png").toString());
        cat = new Image(getClass().getClassLoader().getResource("images/boats/catLandscape.png").toString());
        pirate = new Image(getClass().getClassLoader().getResource("images/boats/pirateLandscape.png").toString());

        addTextLimiter(nameText, 15);

        boatImages.add(yacht);
        boatImages.add(cog);
        boatImages.add(frigate);
        boatImages.add(galleon);
        boatImages.add(boat);
        boatImages.add(cat);
        boatImages.add(pirate);
        boatImageView.setImage(yacht);
        //image resizing cant be done in fxml >(
        courseImageView.setPreserveRatio(false);
        courseImageView.fitWidthProperty().bind(gameGridPane.widthProperty());
        courseImageView.fitHeightProperty().bind(gameGridPane.heightProperty());

        boatImageView.setPreserveRatio(false);
        boatImageView.fitWidthProperty().bind(playerGridPane.widthProperty());
        boatImageView.fitHeightProperty().bind(playerGridPane.heightProperty());
    }


    /**
     * Called when the ready button is pressed
     * Tell server that player is ready
     */
    @FXML
    public void playerReady() {
        if (dataSource.getSourceID() == 0) return; //player has not connected yet
//        soundPlayer.playSound("sounds/im-ready.au");
        readyButton.setDisable(true);
        dataSource.send(new BinaryPackager().packagePlayerReady());
    }


    /**
     * Called when leave lobby is pressed
     * Tell server that player has left and exit lobby
     */
    @FXML
    public void leaveLobby() {
        if (timer != null) timer.stop();
        dataSource.send(new BinaryPackager().packageLeaveLobby());
        this.loadStartView();
    }

    /**
     * Confirms boat customisation
     */
    @FXML
    public void confirmBoatDetails() {
        if (dataSource.getCompetitor() == null) return;
        Competitor boat = dataSource.getCompetitor();

        if (!nameText.getText().equals(""))
            this.dataSource.send(binaryPackager.packageBoatName(boat.getSourceID(), nameText.getText()));
        else nameText.setText(boat.getTeamName()); //use server assigned name

        this.dataSource.send(binaryPackager.packageBoatModel(boat.getSourceID(), index % boatImages.size()));
        confirmButton.setVisible(false);
        nameText.setDisable(true);
        leftButton.setVisible(false);
        rightButton.setVisible(false);
        boatSelected = true;
    }

    /**
     * display previous boat image
     */
    @FXML
    public void showPreviousBoat() {
        index--;
        if (index < 0) {
            index += boatImages.size();
        }
        boatImageView.setImage(boatImages.get(index % boatImages.size()));
    }

    /**
     * display next boat image
     */
    @FXML
    public void showNextBoat() {
        index++;
        boatImageView.setImage(boatImages.get(index % boatImages.size()));

    }


    /**
     * Limits character input count on text field
     * @param tf TextField
     * @param maxLength int, the max number of chars
     */
    private void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }


    /**
     * Check the current race status
     * Change to the raceView upon started signal
     */
    public void checkStatus() {
        if (dataSource.getStoredCompetitors().containsKey(dataSource.getSourceID()) && dataSource.getRaceStatus() == RaceStatusEnum.STARTED && dataSource.getMessageTime() > 0) {
            System.out.println("game beginning...");
            this.loadRaceView();
        }
        if (dataSource.isSpectating() && dataSource.getMessageTime() > 0) {
            System.out.println("Spectating game...");
            this.loadRaceView();
        }
    }


    /**
     * Updates the list with the competitors in the datasource
     */
    private void updateList() {
        this.competitorList.clear();
        if (dataSource.getCompetitorsPosition().size() > 0) {
            this.progressIndicator.setVisible(false);
            this.loadingLabel.setVisible(false);
            this.competitorList.addAll(dataSource.getCompetitorsPosition().stream().map(Competitor::getTeamName).collect(Collectors.toList()));
        }
    }

    /**
     * Updates the course theme image
     */
    private void updateCourseImage() {

        if(courseThemeImage == null) {
            if(dataSource.getThemeId() == ANTARCTICA) {
                this.courseThemeImage = new Image(getClass().getClassLoader().getResource("images/antarctica/antarcticaTheme.png").toString());
                this.locationLabel.setText("Antarctica");
                courseImageView.setImage(this.courseThemeImage);
            }
            else if(dataSource.getThemeId() == AMAZON) {
                this.courseThemeImage = new Image(getClass().getClassLoader().getResource("images/amazon/amazonTheme.png").toString());
                this.locationLabel.setText("Amazon");
                courseImageView.setImage(this.courseThemeImage);
            }
            else if(dataSource.getThemeId() == NILE) {
                this.courseThemeImage = new Image(getClass().getClassLoader().getResource("images/nile/nileTheme.png").toString());
                this.locationLabel.setText("Nile");
                courseImageView.setImage(this.courseThemeImage);
            }
            else if (dataSource.getThemeId() != null) {
                this.courseThemeImage = new Image(getClass().getClassLoader().getResource("images/bermuda/bermudaTheme.png").toString());
                this.locationLabel.setText("Bermuda");
                courseImageView.setImage(this.courseThemeImage);
            }
        }

    }


    /**
     * Loads the start view
     */
    private void loadStartView() {

        //clean up first
        if (timer != null) timer.stop();
        Sounds.player.fadeOut("sounds/bensound-instinct.mp3", 2);
        if (connected) dataSource.disconnect();
        dataSource = null;

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
     * Countdown until the race start, updates the countdown time text.
     * Loads the raceView
     */
    private void loadRaceView() {

        //clean up

        if (timer != null) timer.stop();
        this.leaveButton.setDisable(true); //cant leave once game is starting
        this.readyButton.setDisable(true);

        //hide all boat selection controls
        confirmButton.setVisible(false);
        nameText.setDisable(true);
        leftButton.setVisible(false);
        rightButton.setVisible(false);

        if (!boatSelected) boatImageView.setImage(boatImages.get(0)); //if none selected then use default image

        if (!dataSource.isSpectating()) this.nameText.setText(dataSource.getCompetitor().getTeamName());

        Sounds.player.fadeOut("sounds/bensound-instinct.mp3", 10);

        //count down for 10 seconds
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
                  //  e.printStackTrace();
                }

                assert root != null;
                MainController mainController = loader.getController();
                mainController.beginRace(dataSource, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
                App.getScene().setRoot(root);
            }
        });
    }


}
