package controllers;

import Elements.HealthBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
    private ImageView spectating;
    @FXML
    private Text speedText;
    @FXML
    private Text nameText;
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
    private ImageView tinySpeed=new ImageView(new Image(getClass().getClassLoader().getResource("images/powerups/tinyspeed.png").toString()));
    private ImageView greySpeed=new ImageView(new Image(getClass().getClassLoader().getResource("images/powerups/speed_grey.png").toString()));
    private ImageView tinyHealth=new ImageView(new Image(getClass().getClassLoader().getResource("images/powerups/tinyhealth.png").toString()));
    private ImageView greyHealth=new ImageView(new Image(getClass().getClassLoader().getResource("images/powerups/potion_grey.png").toString()));

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
        gamerTagLabel.setText(dataSource.getCompetitor().getTeamName());

        Competitor boat = dataSource.getCompetitor();
        String[] imagePaths = {"images/boats/yachtLandscape.png",
                "images/boats/cogLandscape.png",
                "images/boats/frigateLandscape.png",
                "images/boats/galleonLandscape.png",
                "images/boats/boatLandscape.png",
                "images/boats/catLandscape.png",
                "images/boats/pirateLandscape.png"};
        Image boatImage;
        try {
            boatImage= new Image(getClass().getClassLoader().getResource(imagePaths[boat.getBoatType()]).toString());
        }
        catch (IndexOutOfBoundsException e){
            boatImage=new Image(getClass().getClassLoader().getResource("images/cross-small").toString());
        }
        playerImageView.setImage(boatImage);
//
//        if (boat.getBoatType() == 0) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/yachtLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        } else if (boat.getBoatType() == 1) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/cogLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        } else if (boat.getBoatType() == 2) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/frigateLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        } else if (boat.getBoatType() == 3) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/galleonLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        } else if (boat.getBoatType() == 4) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/boatLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        } else if (boat.getBoatType() == 5) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/catLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        } else if (boat.getBoatType() == 6) {
//            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boats/pirateLandscape.png").toString());
//            playerImageView.setImage(boatImage);
//        }

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
        screenHealthBar.update(boat.getHealthLevel(),boat.getMaxHealth(), barLength, 5);
        boatSpeedLabel.setText(String.format("%.1f m/s", boat.getVelocity()));
        if (boat.hasSpeedBoost()) {
            this.speed.setGraphic(tinySpeed);
        } else {
            this.speed.setGraphic(greySpeed);
        }
        if (boat.hasPotion()) {
            this.potion.setGraphic(tinyHealth);
        } else {
            this.potion.setGraphic(greyHealth);
        }

//        Double w = stage.getWidth() / 2.5;
//        player.setPrefSize(w, w / 2.5);




    }

    /**
     * Hide the player pane
     */
    void hideAll() {


//
//        @FXML
//        private GridPane boatInfoGrid;
//        @FXML
//        private GridPane player;
//

//        player.setVisible(false);
        spectating.setVisible(true);
        playerImageView1.setVisible(false);
        gamerTagLabel.setVisible(false);
        boatSpeedLabel.setVisible(false);
        speed.setVisible(false);
        potion.setVisible(false);
        playerImageView.setVisible(false);
        healthPane.setVisible(false);
        imageGrid.setVisible(false);
        nameText.setVisible(false);
        speedText.setVisible(false);
    }


    /**
     * When boost button is clicked. The controller sends the packet for using boost
     *
     */
    @FXML
    public void useBoost() {
        if (dataSource.getCompetitor().hasSpeedBoost()) {
            this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.BOOST.getValue(), dataSource.getSourceID()));
            dataSource.getCompetitor().disableBoost();
//            this.speed.setGraphic(greySpeed);
        }
    }

    /**
     * When potion button is clicked. The controller sends the packet for using potion
     *
     */
    @FXML
    public void usePotion() {
        if (dataSource.getCompetitor().hasPotion()) {
            this.dataSource.send(this.binaryPackager.packageBoatAction(BoatAction.POTION.getValue(), dataSource.getSourceID()));
            dataSource.getCompetitor().usePotion();
//            this.potion.setGraphic(greyHealth);
        }
    }
}
