package controllers;

import Elements.HealthBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Competitor;
import utilities.DataSource;

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
    private Label gamerTagLabel;
    @FXML
    private Label boatSpeedLabel;
    @FXML
    private GridPane imageGrid;
    @FXML
    private GridPane player;
    @FXML
    private ImageView speed;
    @FXML
    private ImageView potion;

    private HealthBar screenHealthBar = new HealthBar();
    private DataSource dataSource;
    private Stage stage;


    /**
     * Setup
     *
     * @param dataSource Datasource
     */
    void setup(DataSource dataSource, Stage stage) {

        this.dataSource = dataSource;
        this.healthPane.getChildren().add(screenHealthBar);
        this.healthPane.toBack();
        this.speed.setVisible(false);
        this.potion.setVisible(false);
        gamerTagLabel.setText(dataSource.getCompetitor().getTeamName());
        if (dataSource.getCompetitor().getTeamName() != "Spectator") {
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

        }

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
            this.speed.setVisible(true);
        } else {
            this.speed.setVisible(false);
        }

        Double w = stage.getWidth() / 2.5;
        player.setPrefSize(w, w / 2.5);


        if (boat.hasPotion()) {
            this.potion.setVisible(true);
        } else {
            this.potion.setVisible(false);
        }

    }

    /**
     * Hides the speed icon
     */
    void hideBoost() {
        this.speed.setVisible(false);
    }

    /**
     * Hides the potion icon
     */
    void hidePotion() {
        this.potion.setVisible(false);

    }


}
