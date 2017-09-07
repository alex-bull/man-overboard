package controllers;

import Elements.HealthBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    @FXML private ImageView speed;

    private HealthBar screenHealthBar = new HealthBar();
    private DataSource dataSource;


    /**
     * Setup
     * @param dataSource Datasource
     */
    void setup(DataSource dataSource) {
        this.dataSource = dataSource;
        this.healthPane.getChildren().add(screenHealthBar);
        this.speed.setVisible(false);
        gamerTagLabel.setText(dataSource.getCompetitor().getTeamName());
    }


    /**
     * refresh the panel
     */
    void refresh() {

        Competitor boat = dataSource.getCompetitor();
        screenHealthBar.update(boat, 15, 5);
        String speed = String.format("%.1f", boat.getVelocity());
        boatSpeedLabel.setText(speed + "m/s");
        if(boat.hasSpeedBoost()) {
            this.speed.setVisible(true);
        }
        else {
            this.speed.setVisible(false);
        }

    }

    /**
     * Hides the speed icon
     */
    void hideBoost() {
        System.out.println("HIDNG BOOST");
        this.speed.setVisible(false);
    }


}
