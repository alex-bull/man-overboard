package controllers;

import Elements.HealthBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
