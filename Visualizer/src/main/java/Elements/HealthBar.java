package Elements;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import models.Competitor;

import static parsers.BoatStatusEnum.DSQ;


/**
 * Created by mgo65 on 31/08/17.
 * A visual representation of a health level
 */
public class HealthBar extends Group {

    private Line healthBarBackground;
    private Line healthBar;
    private ImageView ripImage;

    /**
     * Initializes a health bar
     */
    public HealthBar() {

        this.healthBarBackground = new Line();
        this.getChildren().add(healthBarBackground);
        this.healthBar = new Line();
        this.getChildren().add(healthBar);
        this.ripImage = new ImageView(new Image("/cross.png"));
        this.ripImage.setPreserveRatio(true);
    }


    /**
     * Updates the position and color of the health bar to match the current state of the boat
     * @param boat Competitor, the boat for the health bar
     * @param boatX double, the screen coords of the boat on x axis
     * @param boatY double, the screen coords of the boat on y axis
     * @param zoom boolean, true if the view is Zoomed
     * @return boolean, false if the boat died
     */
    public boolean update(Competitor boat, double boatX, double boatY, boolean zoom) {

        int scale = 1;
        if (zoom) scale = 2;
        double strokeWidth = 5 * scale;
        double offset = 20 * scale;
        double maxBarLength = 30 * scale;
        int healthLevel = boat.getHealthLevel() * scale;
        double tombstoneSize = 30 * scale;
        double healthSize = ((healthLevel / (double) boat.getMaxHealth()) * maxBarLength) / scale;


        if(healthLevel > 0) {
            Color healthColour = calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
            healthBarBackground.setStrokeWidth(strokeWidth);
            healthBarBackground.setStartX(boatX);
            healthBarBackground.setStartY(boatY - offset);
            healthBarBackground.setEndX(boatX + maxBarLength);
            healthBarBackground.setEndY(boatY - offset);
            healthBarBackground.setStroke(Color.WHITE);

            healthBar.setStrokeWidth(strokeWidth);
            healthBar.setStartX(boatX);
            healthBar.setStartY(boatY - offset);
            healthBar.setEndX(boatX + healthSize);
            healthBar.setEndY(boatY - offset);

            healthBar.setStroke(healthColour);
            healthBar.toFront();
            return true;
        }

        if(boat.getStatus() != DSQ) {
            this.getChildren().clear();
            boat.setStatus(DSQ);
            this.getChildren().add(ripImage);
            ripImage.setX(boatX);
            ripImage.setY(boatY);
            ripImage.setFitHeight(tombstoneSize);
            ripImage.setFitHeight(tombstoneSize);
            return false;
        }
        return true;
    }





    /**
     * Returns the color of the health bar
     * @param health double the health level
     * @param maxHealth double the max health level
     * @return Color the bar color
     */
    public Color calculateHealthColour(double health, double maxHealth) {

        double percentage = health/maxHealth;
        if(percentage > 0.7) {
            return Color.GREEN;
        }
        else if(percentage > 0.6) {
            return Color.GREENYELLOW;
        }
        else if(percentage > 0.5) {
            return Color.YELLOW;
        }
        else if(percentage > 0.4) {
            return Color.ORANGE;
        }
        else {
            return Color.RED;
        }
    }

}
