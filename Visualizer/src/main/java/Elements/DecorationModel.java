package Elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.MutablePoint;
import parsers.xml.race.Decoration;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by psu43 on 20/09/17.
 * Decorations for a theme
 */
public class DecorationModel extends ImageView {
    private double imageWidth;
    private double imageHeight;
    private Decoration decoration;

    /**
     * Constructs a Decoration item
     * @param decoration Decoration a decoration model
     */
    public DecorationModel(Decoration decoration) {
        this.decoration = decoration;
        double scale = 0.5;
        Image image = getRandomImage();
        if(decoration.getId().contains("Igloo")) {
            image = getRandomIglooImage();
        }
        else if(decoration.getId().contains("Penguin")) {
            image = getRandomPenguinImage();
            scale = 0.2;
        }
        else if(decoration.getId().contains("Bermuda")) {
            image = getRandomBermudaImage();
            scale = 0.4;
        }
        else if (decoration.getId().contains("Amazon")) {
            image = getRandomAmazonImage();
            scale = 0.5;
        }
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.setImage(image);
        this.setFitHeight(imageHeight * scale);
        this.setFitWidth(imageWidth * scale);

    }

    /**
     * Generate a random image for a decoration
     * @return Image the image
     */
    private Image getRandomImage() {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add("images/bermuda/island.png");
        Random random = new Random();
        String chosenPath = imagePaths.get(random.nextInt(imagePaths.size()));
        return new Image(getClass().getClassLoader().getResource(chosenPath).toString());
    }

    /**
     * Generate a random image for an igloo
     * @return Image the image
     */
    private Image getRandomIglooImage() {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add("images/antarctica/igloo.png");
        imagePaths.add("images/antarctica/igloo-flip.png");
        Random random = new Random();
        String chosenPath = imagePaths.get(random.nextInt(imagePaths.size()));
        return new Image(getClass().getClassLoader().getResource(chosenPath).toString());
    }

    /**
     * Generate a random image for a penguin
     * @return Image the image
     */
    private Image getRandomPenguinImage() {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add("images/antarctica/penguins.png");
        imagePaths.add("images/antarctica/penguins-flip.png");
        imagePaths.add("images/antarctica/pengs.png");
        Random random = new Random();
        String chosenPath = imagePaths.get(random.nextInt(imagePaths.size()));
        return new Image(getClass().getClassLoader().getResource(chosenPath).toString());
    }

    /**
     * Generate a random image for bermuda course
     * @return Image the image
     */
    private Image getRandomBermudaImage() {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add("images/bermuda/shipwreck.png");
        imagePaths.add("images/bermuda/supersonic.png");
        Random random = new Random();
        String chosenPath = imagePaths.get(random.nextInt(imagePaths.size()));
        return new Image(getClass().getClassLoader().getResource(chosenPath).toString());
    }

    /**
     * Generate a random image for an amazon themed decoration
     * @return Image the image
     */
    private Image getRandomAmazonImage() {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add("images/amazon/temple.png");
        imagePaths.add("images/amazon/temple-flip.png");
        imagePaths.add("images/amazon/jaguar.png");
        Random random = new Random();
        String chosenPath = imagePaths.get(random.nextInt(imagePaths.size()));
        return new Image(getClass().getClassLoader().getResource(chosenPath).toString());
    }


    /**
     * Update the position of the decoration model
     * @param isZoom boolean true if zoomed in
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width double, the screen width
     * @param height double, the screen height
     */
    public void update(boolean isZoom, MutablePoint currentPosition17, double width, double height) {
        if (isZoom) {
            MutablePoint p = this.decoration.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
            this.relocate(p.getXValue()-imageWidth/2,p.getYValue()-imageHeight/2);
        } else {
            MutablePoint p=this.decoration.getPosition();
            this.relocate(p.getXValue()-imageWidth/2,p.getYValue()-imageHeight/2);
        }
    }

}
