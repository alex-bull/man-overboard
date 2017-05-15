package controllers;

import javafx.animation.FadeTransition;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;
import com.google.common.primitives.Doubles;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import models.*;
import utilities.DataSource;

import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.*;

/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable {

    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private Label fpsCounter;
    @FXML private RadioButton allAnnotationsRadio;
    @FXML private RadioButton noAnnotationsRadio;
    @FXML private RadioButton someAnnotationsRadio;
    @FXML private CheckBox speedButton;
    @FXML private CheckBox nameButton;
    @FXML private CheckBox fpsToggle;
    @FXML private Text status;

    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Map<Integer, Polygon> wakeModels = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private List<MutablePoint> courseBoundary = null;
    private List<CourseFeature> courseFeatures = null;
    private Map<String, Shape> markModels = new HashMap<>();
    private DataSource dataSource;
    private long startTimeNano = System.nanoTime();
    private int counter = 0;

    private Line startLine;
    private Line finishLine;
    private final double boatLength = 20;
    private final double startWakeOffset= 3;
    private final double wakeWidthFactor=0.2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startLine=new Line();
        finishLine=new Line();
        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        final ToggleGroup annotations = new ToggleGroup();
        allAnnotationsRadio.setToggleGroup(annotations);
        noAnnotationsRadio.setToggleGroup(annotations);
        someAnnotationsRadio.setToggleGroup(annotations);
        allAnnotationsRadio.setSelected(true);
        showAllAnnotations();
        fpsToggle.setSelected(true);

    }

    /**
     * Called when the user clicks no annotations button.
     * Clears individual annotations
     */
    @FXML
    public void clearAnnotations() {
        speedButton.setSelected(false);
        nameButton.setSelected(false);
    }

    /**
     * Called when the user clicks all Annotations button.
     * Clears individual annotations
     */
    @FXML
    public void showAllAnnotations() {
        speedButton.setSelected(true);
        nameButton.setSelected(true);
    }

    /**
     * Sets the race and the race start time and then animates the race
     *
     * @param width  double the width of the canvas
     * @param height double the height of the canvas
     */
    void begin(double width, double height, DataSource dataSource) {

        raceViewCanvas.setHeight(height);
        raceViewCanvas.setWidth(width);
        raceViewPane.setPrefHeight(height);
        raceViewPane.setPrefWidth(width);

        this.dataSource = dataSource;

        animate(width, height);
    }


    /**
     * Draws the course features on the canvas
     */
    private void drawCourse() {

        // loops through all course features
        for (CourseFeature courseFeature : courseFeatures) {
            Shape mark = this.markModels.get(courseFeature.getName());
            if (mark != null) {
                this.raceViewPane.getChildren().remove(mark);
            }
            drawMark(courseFeature);
        }
    }

    /**
     * Draws the line for gates
     *
     * @param gatesID List of integer of the gates
     */
    private void drawLine(Line line, List<Integer> gatesID) {
        double x1 = dataSource.getStoredFeatures().get(gatesID.get(0)).getPixelLocations().get(0).getXValue();
        double y1 = dataSource.getStoredFeatures().get(gatesID.get(0)).getPixelLocations().get(0).getYValue();
        double x2 = dataSource.getStoredFeatures().get(gatesID.get(1)).getPixelLocations().get(0).getXValue();
        double y2 = dataSource.getStoredFeatures().get(gatesID.get(1)).getPixelLocations().get(0).getYValue();
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
//        System.out.println("X1 " + x1);
//        System.out.println("Y1 " + y1);
//        System.out.println("X2 " + x2);
//        System.out.println("Y2 " + y2);
//
//        System.out.println("------------------------------------------");

    }

    private void drawMark(CourseFeature courseFeature) {


        double x = courseFeature.getPixelLocations().get(0).getXValue();
        double y = courseFeature.getPixelLocations().get(0).getYValue();
        Circle circle = new Circle(x, y, 7.5, ORANGERED);
        this.raceViewPane.getChildren().add(circle);
        this.markModels.put(courseFeature.getName(), circle);


    }

    /**
     * Draw boundary
     *
     * @param gc GraphicsContext
     */
    private void drawBoundary(GraphicsContext gc) {

        if (courseBoundary != null) {
            gc.save();
            ArrayList<Double> boundaryX = new ArrayList<>();
            ArrayList<Double> boundaryY = new ArrayList<>();

            for (MutablePoint point : courseBoundary) {
                boundaryX.add(point.getXValue());
                boundaryY.add(point.getYValue());
            }
            gc.setLineDashes(5);
            gc.setLineWidth(0.8);
            gc.clearRect(0,0,4000,4000);
            drawBackGround(gc,4000,4000);
            gc.strokePolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.setFill(Color.POWDERBLUE);

            //shade inside the boundary
            gc.fillPolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.restore();

        }

    }


    /**
     * Draw annotations
     * @param boat Competitor a competing boat
     */
    private void drawAnnotations(Competitor boat) {

        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();
        Label nameLabel;
        Label speedLabel;

        if (nameAnnotations.get(boat.getSourceID()) == null) {
            nameLabel = new Label(boat.getAbbreName());
            nameLabel.setFont(Font.font("Monospaced"));
            nameLabel.setTextFill(boat.getColor());
            this.raceViewPane.getChildren().add(nameLabel);
            this.nameAnnotations.put(boat.getSourceID(), nameLabel);
        }

        if (speedAnnotations.get(boat.getSourceID()) == null) {
            speedLabel = new Label(String.valueOf(boat.getVelocity()) + "m/s");
            speedLabel.setFont(Font.font("Monospaced"));
            speedLabel.setTextFill(boat.getColor());
            this.raceViewPane.getChildren().add(speedLabel);
            this.speedAnnotations.put(boat.getSourceID(), speedLabel);
        }

        nameLabel = nameAnnotations.get(boat.getSourceID());
        speedLabel = speedAnnotations.get(boat.getSourceID());

        if (nameButton.isSelected()) {
            nameLabel.toFront();
            nameLabel.setText(boat.getAbbreName());
            nameLabel.setLayoutX(xValue - 25);
            nameLabel.setLayoutY(yValue - 25);
        } else {
            nameLabel.setText("");
        }

        if (speedButton.isSelected()) {
            speedLabel.toFront();
            speedLabel.setText(String.valueOf(boat.getVelocity()) + "m/s");
            speedLabel.setLayoutX(xValue + 5);
            speedLabel.setLayoutY(yValue + 15);
        } else {
            speedLabel.setText("");
        }

        if (!(nameButton.isSelected() && speedButton.isSelected() || !nameButton.isSelected() && !speedButton.isSelected())) {
            someAnnotationsRadio.setSelected(true);
        }
        // draws FPS counter
        if (fpsToggle.isSelected()) {
            fpsCounter.setVisible(true);
        } else {
            fpsCounter.setVisible(false);
        }
    }



    /**
     * Draw or move a boat model for a competitor
     * @param boat Competitor a competing boat
     */
    private void drawBoat(Competitor boat) {
        Integer sourceId = boat.getSourceID();

        if (boatModels.get(sourceId) == null) {
            Polygon boatModel = new Polygon();
            boatModel.getPoints().addAll(
                    0.0, 0.0, //top
                    -5.0, 20.0, //left
                    5.0, 20.0); //right
            boatModel.setFill(boat.getColor());
            boatModel.setStroke(BLACK);
            boatModel.setStrokeWidth(1);
            //add to the pane and store a reference
            this.raceViewPane.getChildren().add(boatModel);
            this.boatModels.put(boat.getSourceID(), boatModel);
        }
        //Translate and rotate the corresponding boat models
        boatModels.get(sourceId).setLayoutX(boat.getPosition().getXValue());
        boatModels.get(sourceId).setLayoutY(boat.getPosition().getYValue());
        boatModels.get(sourceId).toFront();
        boatModels.get(sourceId).getTransforms().clear();
        boatModels.get(sourceId).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
    }

    /**
     * Draw boat competitor
     *
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {
        if (wakeModels.get(boat.getSourceID()) == null) {
            double wakeLength = boat.getVelocity();
            Polygon wake=new Polygon();
            wake.getPoints().addAll(-startWakeOffset,boatLength,startWakeOffset,boatLength,startWakeOffset+wakeLength*wakeWidthFactor,wakeLength + boatLength,-startWakeOffset-wakeLength*wakeWidthFactor,wakeLength + boatLength);
            LinearGradient linearGradient=new LinearGradient(0.0,0,0.0,1,true, CycleMethod.NO_CYCLE,new Stop(0.0f,Color.rgb(0,0,255,0.7)),new Stop(1.0f,TRANSPARENT));
            wake.setFill(linearGradient);
            raceViewPane.getChildren().add(wake);
            this.wakeModels.put(boat.getSourceID(), wake);
        }
        double newLength = boat.getVelocity() * 2;
        Polygon wakeModel = wakeModels.get(boat.getSourceID());
        wakeModel.getTransforms().clear();
        wakeModel.getPoints().clear();
        wakeModel.getPoints().addAll(-startWakeOffset,boatLength,startWakeOffset,boatLength,startWakeOffset+newLength*wakeWidthFactor,newLength + boatLength,-startWakeOffset-newLength*wakeWidthFactor,newLength + boatLength);
        wakeModel.getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));
        wakeModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        wakeModel.toFront();

    }


    /**
     * Draw boat wakes and factor it with its velocity
     *
     * @param boat  Competitor a competitor
     * @param index Index
     */
    private void moveWake(Competitor boat, Integer index) {

        double newLength = boat.getVelocity() * 2;

        Polygon wakeModel = wakeModels.get(index);
        wakeModel.getTransforms().clear();
        wakeModel.getPoints().clear();
        wakeModel.getPoints().addAll(-startWakeOffset,boatLength,startWakeOffset,boatLength,startWakeOffset+newLength*wakeWidthFactor,newLength + boatLength,-startWakeOffset-newLength*wakeWidthFactor,newLength + boatLength);
        wakeModel.getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));
        wakeModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        wakeModel.toFront();
    }



    /**
     * Draw the next dot of track for the boat on the canvas
     *
     * @param boat Competitor
     * @param gc   GraphicsContext the gc to draw the track on
     */
    private void drawTrack(Competitor boat, GraphicsContext gc) {
        gc.setFill(boat.getColor());
        gc.save();
        Dot dot = new Dot(boat.getPosition().getXValue(), boat.getPosition().getYValue());
//        Rotate r = new Rotate(boat.getCurrentHeading(), boat.getPosition().getXValue(), boat.getPosition().getYValue());
//        Rotate rr = new Rotate(-boat.getCurrentHeading(), boat.getPosition().getXValue(), boat.getPosition().getYValue());
//
//        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
//        gc.translate(0, 3);
//        gc.fillOval(boat.getPosition().getXValue() - 1, boat.getPosition().getYValue() - 1, 2, 2);
//        gc.setTransform(rr.getMxx(), rr.getMyx(), rr.getMxy(), rr.getMyy(), rr.getTx(), rr.getTy());
        Circle circle = new Circle(dot.getX(), dot.getY(), 1.5, boat.getColor());
        //add fade transition
        FadeTransition ft=new FadeTransition(Duration.millis(20000),circle);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(event -> raceViewPane.getChildren().remove(circle));
        ft.play();
        this.raceViewPane.getChildren().add(circle);
        gc.restore();
    }


    private void moveMark(String name, double x, double y) {
        this.markModels.get(name).setLayoutX(x);
        this.markModels.get(name).setLayoutY(y);
    }

    /**
     *
     * @param gc
     * @param width
     * @param height
     */
    public void drawBackGround(GraphicsContext gc, double width, double height) {
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, width, height);
    }

    /**
     * Refreshes the contents of the display to match the datasource
     * @param dataSource DataSource the data to display
     */
    void refresh(DataSource dataSource) {

        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();
        this.dataSource = dataSource;
        String statusString = "Race status: " + dataSource.getRaceStatus();
        if (!statusString.equals(status.getText())) {
            status.setText("Race status: " + dataSource.getRaceStatus());
        }

        counter++; // increment fps counter

        // calculate fps
        long currentTimeNano = System.nanoTime();
        if (currentTimeNano > startTimeNano + 1000000000) {
            startTimeNano = System.nanoTime();
            fpsCounter.setText(String.format("FPS: %d", counter));
            counter = 0;
        }
        // check if course features need to be redrawn
        if (dataSource.getCourseFeatures() != (courseFeatures)) {
            courseFeatures = dataSource.getCourseFeatures();
            drawCourse();

            // check if boundary needs to be redrawn
            if (dataSource.getCourseBoundary() != courseBoundary) {
                courseBoundary = dataSource.getCourseBoundary();
                drawBoundary(gc);
                drawLine(startLine, dataSource.getStartMarks());
                drawLine(finishLine, dataSource.getFinishMarks());
            }
        }
        List<Competitor> competitors = dataSource.getCompetitorsPosition();
        //move competitors and draw tracks
        for (Competitor boat : competitors) {
            if (counter % 70 == 0) {
                drawTrack(boat, gc);
            }
            this.drawWake(boat);
            this.drawBoat(boat);
            this.drawAnnotations(boat);

        }

    }



    /**
     * Starts the animation timer to animate the race
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    private void animate(double width, double height) {

        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, width, height);
        while (dataSource.getCompetitorsPosition() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
