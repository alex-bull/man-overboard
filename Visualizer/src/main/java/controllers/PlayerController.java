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

import java.util.Objects;

/**
 * Created by mattgoodson on 1/09/17.
 * Controller for player info panel
 */
public class PlayerController {


    @FXML private Pane healthPane;
    @FXML private ImageView playerImageView;
    @FXML private Label gamerTagLabel;
    @FXML private Label boatSpeedLabel;
    @FXML private GridPane imageGrid;
    @FXML private GridPane player;

    private HealthBar screenHealthBar = new HealthBar();
    private DataSource dataSource;
    private Stage stage;



    /**
     * Setup
     * @param dataSource Datasource
     */
    void setuo(DataSource dataSource, Stage stage) {
        this.dataSource = dataSource;
        this.healthPane.getChildren().add(screenHealthBar);
        gamerTagLabel.setText(dataSource.getCompetitor().getTeamName());
        Competitor boat = dataSource.getCompetitor();
        if (boat.getBoatType() == 0) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/yachtLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }
        else if (boat.getBoatType() == 1){
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/cogLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }
        else if (boat.getBoatType() == 2){
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/frigateLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }
        else if (boat.getBoatType() == 3){
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/galleonLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }
        else if (boat.getBoatType() == 4){
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boatLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }
        else if (boat.getBoatType() == 5){
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/catLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }
        else if (boat.getBoatType() == 6){
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/pirateLandscape.png").toString());
            playerImageView.setImage(boatImage);
        }

        playerImageView.setPreserveRatio(false);
        playerImageView.fitWidthProperty().bind(imageGrid.widthProperty());
        playerImageView.fitHeightProperty().bind(imageGrid.heightProperty());

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

        Double w = stage.getWidth() / 2.5;
        player.setPrefSize(w, w/2.5);



    }


}
