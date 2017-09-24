package Elements;

import javafx.scene.Group;
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

    /**
     * Initializes a health bar
     */
    public HealthBar() {

        this.healthBarBackground = new Line();
        this.getChildren().add(healthBarBackground);
        this.healthBar = new Line();
        this.getChildren().add(healthBar);
    }


    /**
     * Update health bar using a scale and default positioning
     *
     * @param boat        Competitor, the boat
     * @param lengthScale int, the scale factor for bar length
     * @param widthScale  int, the scale factor for bar width
     */
    public void update(Competitor boat, int lengthScale, int widthScale) {

        double strokeWidth = 5 * widthScale;
        double offset = 0;
        double maxBarLength = 1 * lengthScale;
        double healthLevel = boat.getHealthLevel() * lengthScale;
        double healthSize = ((healthLevel / boat.getMaxHealth()) * maxBarLength) / lengthScale;
        Color healthColour = calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        healthBarBackground.setStrokeWidth(strokeWidth + (2 * widthScale));
        healthBarBackground.setStartX(0);
        healthBarBackground.setStartY(0 - offset);
        healthBarBackground.setEndX(0 + maxBarLength);
        healthBarBackground.setEndY(0 - offset);
        healthBarBackground.setStroke(Color.WHITE);

        healthBar.setStrokeWidth(strokeWidth);
        healthBar.setStartX(0);
        healthBar.setStartY(0 - offset);
        healthBar.setEndX(0 + healthSize);
        healthBar.setEndY(0 - offset);

        healthBar.setStroke(healthColour);
        healthBar.toFront();

    }

    /**
     * Updates the position and color of the health bar to match the current state of the boat
     *
     * @param boat  Competitor, the boat for the health bar
     * @param boatX double, the screen coords of the boat on x axis
     * @param boatY double, the screen coords of the boat on y axis
     * @param zoom  boolean, true if the view is Zoomed
     * @return boolean, false if the boat died
     */
    public boolean update(Competitor boat, double boatX, double boatY, boolean zoom) {
        boolean alive = true;
        int scale = 1;
        if (zoom) scale *= 2;
        double strokeWidth = 5 * scale;
        double offset = 20 * scale;
        double maxBarLength = 30 * scale;
        double healthLevel = boat.getHealthLevel() * scale;
        double healthSize = ((healthLevel / boat.getMaxHealth()) * maxBarLength) / scale;

        System.out.println("health level " + healthLevel);
        if (healthLevel > 0) {
            Color healthColour = calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
            healthBarBackground.setStrokeWidth(strokeWidth + (2 * scale));
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
        } else if(boat.getStatus() != DSQ) {
            healthBar.setVisible(false);
            this.getChildren().clear();
            boat.setStatus(DSQ);
            alive = false;
        }
        return alive;
    }


    /**
     * Returns the color of the health bar
     *
     * @param health    double the health level
     * @param maxHealth double the max health level
     * @return Color the bar color
     */
    public Color calculateHealthColour(double health, double maxHealth) {

        double percentage = health / maxHealth;
        if (percentage > 0.7) {
            return Color.GREEN;
        } else if (percentage > 0.6) {
            return Color.GREENYELLOW;
        } else if (percentage > 0.5) {
            return Color.YELLOW;
        } else if (percentage > 0.4) {
            return Color.ORANGE;
        } else {
            return Color.RED;
        }
    }

}
