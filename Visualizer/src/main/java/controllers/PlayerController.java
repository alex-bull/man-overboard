package controllers;

import Elements.HealthBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Competitor;
import parsers.boatAction.BoatAction;
import utilities.DataSource;
import utility.BinaryPackager;

/**
 * Created by mattgoodson on 1/09/17.
 * Controller for player info panel
 */
public class PlayerController {


    @FXML
    private Pane healthPane;
    @FXML
    private ImageView playerImageView;
    @FXML
    private ImageView playerImageView1;
    @FXML
    private Label gamerTagLabel;
    @FXML
    private Label boatSpeedLabel;
    @FXML
    private GridPane imageGrid;
    @FXML
    private GridPane boatInfoGrid;
    @FXML
    private GridPane player;
    @FXML
    private Button speed;
    @FXML
    private Button potion;

    private HealthBar screenHealthBar = new HealthBar();
    private DataSource dataSource;
    private Stage stage;
    private BinaryPackager binaryPackager;


    /**
     * Setup
     *
     * @param dataSource Datasource
     */
    void setup(DataSource dataSource, Stage stage) {

        this.dataSource = dataSource;
        this.healthPane.getChildren().add(screenHealthBar);
        this.healthPane.toBack();
        this.binaryPackager = new BinaryPackager();
//        this.speed.setVisible(false);
//        this.potion.setVisible(false);
        gamerTagLabel.setText(dataSource.getCompetitor().getTeamName());

        Competitor boat = dataSource.getCompetitor();
        if (boat.getBoatType() == 0) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/yachtLandscape.png").toString());
            playerImageView.setImage(boatImage);
        } else if (boat.getBoatType() == 1) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/cogLandscape.png").toString());
            playerImageView.setImage(boatImage);
        } else if (boat.getBoatType() == 2) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/frigateLandscape.png").toString());
            playerImageView.setImage(boatImage);
        } else if (boat.getBoatType() == 3) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/galleonLandscape.png").toString());
            playerImageView.setImage(boatImage);
        } else if (boat.getBoatType() == 4) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boatLandscape.png").toString());
            playerImageView.setImage(boatImage);
        } else if (boat.getBoatType() == 5) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/catLandscape.png").toString());
            playerImageView.setImage(boatImage);
        } else if (boat.getBoatType() == 6) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/pirateLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }

        playerImageView.setPreserveRatio(false);
        playerImageView.fitWidthProperty().bind(imageGrid.widthProperty());
        playerImageView.fitHeightProperty().bind(imageGrid.heightProperty());

        playerImageView1.setPreserveRatio(false);
        playerImageView1.fitWidthProperty().bind(boatInfoGrid.widthProperty());

        this.stage = stage;


    }


    /**
     * refresh the panel
     */
    void refresh() {

        Competitor boat = dataSource.getCompetitor();
        Integer barLength = (int) Math.round(healthPane.getWidth());
        screenHealthBar.update(boat, barLength, 5);
        String speed = String.format("%.1f", boat.getVelocity());
        boatSpeedLabel.setText(speed + "m/s");
        if (boat.hasSpeedBoost()) {
            this.speed.setGraphic(new ImageView(new Image("/images/tinyspeed.png")));
        } else {
            this.speed.setGraphic(new ImageView(new Image("/images/greySpeed.png")));
        }

        Double w = stage.getWidth() / 2.5;
        player.setPrefSize(w, w / 2.5);


        if (boat.hasPotion()) {
            this.potion.setGraphic(new ImageView(new Image("/images/tinyhealth.png")));
        } else {
            this.potion.setGraphic(new ImageView(new Image("/images/greyPotion.png")));
        }

    }

    /**
     * Greys out the speed icon
     */
    void greyOutBoost() {
        this.speed.setGraphic(new ImageView(new Image("/images/greySpeed.png")));
    }

    /**
     * Greys out the potion icon
     */
    void greyOutPotion() {
        this.potion.setGraphic(new ImageView(new Image("/images/greyPotion.png")));

    }

    /**
     * When boost button is clicked. The controller sends the packet for using boost
     * @param actionEvent
     */

    public void useBoost(ActionEvent actionEvent) {
        if (dataSource.getCompetitor().hasSpeedBoost()) {
            this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.BOOST.getValue(), dataSource.getSourceID()));
            dataSource.getCompetitor().disableBoost();
            greyOutBoost();
        }
    }

    /**
     * When potion button is clicked. The controller sends the packet for using potion
     * @param actionEvent
     */
    public void usePotion(ActionEvent actionEvent) {
        if (dataSource.getCompetitor().hasPotion()) {
            this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.POTION.getValue(), dataSource.getSourceID()));
            dataSource.getCompetitor().usePotion();
            greyOutPotion();
        }
    }
}
