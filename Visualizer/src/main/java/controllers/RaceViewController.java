package controllers;

import javafx.animation.FadeTransition;

import javafx.geometry.Point2D;
import javafx.scene.Group;
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
import parsers.Converter;
import utilities.Annotation;
import utilities.DataSource;

import java.net.URL;
import java.util.*;

import static java.lang.Math.*;
import static javafx.scene.paint.Color.*;

/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {

    private final double boatLength = 20;
    private final double startWakeOffset= 3;
    private final double wakeWidthFactor=0.2;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private Label fpsCounter;
    @FXML private RadioButton allAnnotationsRadio;
    @FXML private RadioButton noAnnotationsRadio;
    @FXML private RadioButton someAnnotationsRadio;
    @FXML private CheckBox speedButton;
    @FXML private CheckBox nameButton;
    @FXML private CheckBox timeToMarkButton;
    @FXML private CheckBox timeFromMarkButton;
    @FXML private CheckBox fpsToggle;
    @FXML private Text status;
    @FXML private Group annotationGroup;

    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Map<Integer, Polygon> wakeModels = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeFromMarkAnnotations = new HashMap<>();
    private List<MutablePoint> courseBoundary = null;
    private List<CourseFeature> courseFeatures = null;
    private Map<String, Shape> markModels = new HashMap<>();
    private DataSource dataSource;
    private long startTimeNano = System.nanoTime();
    private long timeFromLastMark;
    private int counter = 0;

    private Line startLine;
    private Line finishLine;
    private Integer selectedBoatSourceId = 0;


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
        timeToMarkButton.setSelected(false);
        timeFromMarkButton.setSelected(false);
    }

    /**
     * Called when the user clicks all Annotations button.
     * Clears individual annotations
     */
    @FXML
    public void showAllAnnotations() {
        speedButton.setSelected(true);
        nameButton.setSelected(true);
        timeToMarkButton.setSelected(true);
        timeFromMarkButton.setSelected(true);
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
        drawAnnotations();
        animate(width, height);
    }

    /**
     * Observer method for table observer
     * Updates the selected boat property
     * @param sourceId Integer the sourceId of the selected boat
     */
    public void boatSelected(Integer sourceId) {
        this.selectedBoatSourceId = sourceId;
        for (Competitor boat : dataSource.getCompetitorsPosition()) {
            if (selectedBoatSourceId == boat.getSourceID()) {
                Competitor selectedBoat = boat;
                Color boatColor = boat.getColor();
                drawVirtualLine(boatColor, selectedBoat);
            }
        }

    }

    /**
     * Calculates whether boat is heading to the start line
     * and if it does calculates the virtual line points and returns them so they can be used for drawing
     * returns empty list if boat is not heading to the start line
     * @param selectedBoat selected boat
     * @return virtualLinePoints List<MutablePoint>
     */
    private List<MutablePoint> calcVirtualLinePoints(Competitor selectedBoat) {
        //TODO: MAYBE SPLIT IT UP EVEN MORE?? AND ALSO MOVE CALCULATION FUNCTIONS OUT OF THE RACEVIEWCONTROLLER?
        List<MutablePoint> virtualLinePoints = new ArrayList<>();
        Polygon boatModel = boatModels.get(selectedBoatSourceId);
        CourseFeature startLine1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0));
        CourseFeature startLine2 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(1));

        Point2D boatFront = boatModel.localToParent(0, 0);
        Point2D boatBack = boatModel.localToParent(0, -2000);

        double x1 = boatFront.getX();
        double x2 = boatBack.getX();
        double y1 = boatFront.getY();
        double y2 = boatBack.getY();

        double dy = y1 - y2;
        double dx = x1 - x2;

        double headingGradient = dy / dx;

        double c = y1 - (headingGradient * x1);

        double x3 = startLine1.getPixelLocations().get(0).getXValue();
        double y3 = startLine1.getPixelLocations().get(0).getYValue();
        double x4 = startLine2.getPixelLocations().get(0).getXValue();
        double y4 = startLine2.getPixelLocations().get(0).getYValue();

        double startLineGradient = (y3 - y4) / (x3 - x4);
        double d = y3 - (startLineGradient) * x3;

        double xa = (d - c) / (headingGradient - startLineGradient);

        //formula to check whether boat is heading towards the start line
        boolean intersects = !((xa < Double.max(Double.min(x1, x2), Double.min(x3, x4))) || (xa > Double.min(Double.max(x1, x2), Double.max(x3, x4))));
        if (intersects) {
            System.out.println("BOAT HEADING TO START LINE");
            double ya = headingGradient * xa + c;

            double deltaX = x1 - xa;
            double deltaY = y1 - ya;

            double distanceToRealLine = calcDistToReal(selectedBoat);
            double distanceToVirtualLine = calcDistToVirtual(selectedBoat);

            if (distanceToRealLine != 0) {
                double ratio = distanceToVirtualLine / distanceToRealLine;

                double deltaXRealToVirtual = (1 - ratio) * deltaX;
                double deltaYRealToVirtual = (1 - ratio) * deltaY;

                double x5 = x3 + deltaXRealToVirtual;
                double y5 = y3 + deltaYRealToVirtual;
                double x6 = x4 + deltaXRealToVirtual;
                double y6 = y4 + deltaYRealToVirtual;
                MutablePoint virtualLine1 = new MutablePoint(x5, y5);
                MutablePoint virtualLine2 = new MutablePoint(x6, y6);
                virtualLinePoints.add(virtualLine1);
                virtualLinePoints.add(virtualLine2);
            }
        }
        System.out.println("BOAT Not HEADING TO START LINE");
        return virtualLinePoints;
    }

    /**
     * Draws the virtual line of the selected boat with the same color
     * @param boatColor color of the boat
     * @param selectedBoat selected boat
     */
    private void drawVirtualLine(Color boatColor, Competitor selectedBoat) {
        List<MutablePoint> virtualLinePoints = calcVirtualLinePoints(selectedBoat);
        if (!virtualLinePoints.isEmpty()) {
            double x5 = virtualLinePoints.get(0).getXValue();
            double y5 = virtualLinePoints.get(0).getYValue();
            double x6 = virtualLinePoints.get(1).getXValue();
            double y6 = virtualLinePoints.get(1).getYValue();
            Line virtualLine = new Line();
            virtualLine.setStroke(boatColor);
            virtualLine.setStartX(x5);
            virtualLine.setStartY(y5);
            virtualLine.setEndX(x6);
            virtualLine.setEndY(y6);
            raceViewPane.getChildren().add(virtualLine);
        }
    }

        /*
        // Uncomment this for cool effects
        Line line = new Line();
        for (Competitor boat : dataSource.getCompetitorsPosition()) {
            if (selectedBoatSourceId == boat.getSourceID()) {
                line.setStroke(boat.getColor());
            }
        }
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
        raceViewPane.getChildren().add(line);
        */


    /**
     * Calculates the distance in metres from the selected boat to its virtual start line.
     * @param selectedBoat selected boat
     * @return double distance (m)
     */
    private double calcDistToVirtual(Competitor selectedBoat) {
        long expectedStartTime = dataSource.getExpectedStartTime();
        long messageTime = dataSource.getMessageTime();
        long timeUntilStart = Converter.convertToRelativeTime(expectedStartTime, messageTime) / 1000; // seconds
        double velocity = selectedBoat.getVelocity();
        return velocity * timeUntilStart; // metres
    }

    /**
     * Calculates the distance in metres from the selected boat to the race start line.
     * @param selectedBoat selected boat
     * @return double distance (m)
     */
    private double calcDistToReal(Competitor selectedBoat) {
        double boatLat = selectedBoat.getLatitude();
        double boatLon = selectedBoat.getLongitude();

        CourseFeature startLine1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0));
        double startLat = startLine1.getGPSPoint().getXValue();
        double startLon = startLine1.getGPSPoint().getYValue();

        long r = 6371000;
        double phiStart = Math.toRadians(startLat);
        double phiBoat = Math.toRadians(boatLat);

        double deltaPhi = Math.toRadians(boatLat - startLat);
        double deltaLambda = Math.toRadians(boatLon - startLon);

        double a = sin(deltaPhi / 2) * sin(deltaPhi / 2) + cos(phiStart) * cos(phiBoat) * sin(deltaLambda / 2) * sin(deltaLambda / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return r * c;
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
    }

    /**
     * Draw the mark of the course feature
     * @param courseFeature CourseFeature
     */
    private void drawMark(CourseFeature courseFeature) {
        double x = courseFeature.getPixelLocations().get(0).getXValue();
        double y = courseFeature.getPixelLocations().get(0).getYValue();
        Circle circle = new Circle(x, y, 4.5, ORANGERED);
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
            drawBackground(gc,4000,4000);
            gc.strokePolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.setFill(Color.POWDERBLUE);

            //shade inside the boundary
            gc.fillPolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.restore();

        }

    }


    /**
     * Draw annotations and move with boat positions
     */
    private void drawAnnotations() {
        List<Competitor> competitors = dataSource.getCompetitorsPosition();
        //move competitors and draw tracks
        for (Competitor boat : competitors) {
            int sourceID = boat.getSourceID();

            for (int i = 0; i < annotationGroup.getChildren().size(); i++) {
                String annotationType = ((CheckBox) annotationGroup.getChildren().get(i)).getText();
                Annotation annotation = Annotation.stringToAnnotation(annotationType);
                Label label = null;
                assert annotation != null;
                switch (annotation) {
                    case TEAM_NAME:
                        //name annotation
                        label = new Label(boat.getAbbreName());
                        this.nameAnnotations.put(sourceID, label);
                        break;
                    case BOAT_SPEED:
                        //speed annotation
                        label = new Label(String.valueOf(boat.getVelocity()) + "m/s");
                        this.speedAnnotations.put(sourceID, label);
                        break;
                    case EST_TIME_TO_NEXT_MARK:
                        //est time to next mark annotation
                        label = new Label(String.valueOf(boat.getTimeToNextMark() / 1000) + " seconds");
                        this.timeToMarkAnnotations.put(sourceID, label);
                        break;
                    case TIME_FROM_LAST_MARK:
                        //time from the last mark annotation
                        label = new Label(String.valueOf( timeFromLastMark / 1000) + " seconds");
                        this.timeFromMarkAnnotations.put(sourceID, label);
                }

                label.setFont(Font.font("Monospaced"));
                label.setTextFill(boat.getColor());
                this.raceViewPane.getChildren().add(label);

            }
        }
    }


    /**
     * Move annotations with competing boat positions
     * @param boat Competitor of competing boat
     */
    private void moveAnnotations(Competitor boat){
        int sourceID = boat.getSourceID();

        int offset = 10;
        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();

        //all selected will be true if all selected
        boolean allSelected = true;
        //none selected will be false if none selected
        boolean noneSelected = false;
        //change radio button depending on what is selected
        for (int i = 0; i < annotationGroup.getChildren().size(); i++) {
            CheckBox checkBox = (CheckBox) annotationGroup.getChildren().get(i);
            Annotation annotation = Annotation.stringToAnnotation(checkBox.getText());
            Label label = null;
            allSelected = allSelected && checkBox.isSelected();
            noneSelected = noneSelected || checkBox.isSelected();
            assert annotation != null;
            switch (annotation) {
                case TEAM_NAME:
                    label = this.nameAnnotations.get(sourceID);
                    break;
                case BOAT_SPEED:
                    label = this.speedAnnotations.get(sourceID);
                    label.setText(String.valueOf(boat.getVelocity()) + "m/s");
                    break;
                case EST_TIME_TO_NEXT_MARK:
                    label = this.timeToMarkAnnotations.get(sourceID);
                    if(boat.getTimeToNextMark() != 0){
                        label.setText(String.valueOf(boat.getTimeToNextMark() / 1000) + "s to Next Mark");
                    } else {
                        label.setText("--");
                    }
                    break;
                case TIME_FROM_LAST_MARK:
                    label= this.timeFromMarkAnnotations.get(sourceID);
                    if( timeFromLastMark != 0) {
                        label.setText(String.valueOf(timeFromLastMark / 1000) + "s from Last Mark");
                    } else {
                        label.setText("--");
                    }

            }
            label.setVisible(checkBox.isSelected());
            label.setLayoutX(xValue + 5);
            label.setLayoutY(yValue + offset);
            if (checkBox.isSelected()) {
                offset += 12;
            }

        }

        allAnnotationsRadio.setSelected(allSelected);
        noAnnotationsRadio.setSelected(!noneSelected);
        someAnnotationsRadio.setSelected(!allSelected && noneSelected);

        // draws FPS counter
        fpsCounter.setVisible(fpsToggle.isSelected());

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
            //Boats selected can be selected by clicking on them
            boatModel.setOnMouseClicked(event -> {
                selectedBoatSourceId = sourceId;
                System.out.println(selectedBoatSourceId);
            });
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
     * Draw the next dot of track for the boat on the canvas
     *
     * @param boat Competitor
     * @param gc   GraphicsContext the gc to draw the track on
     */
    private void drawTrack(Competitor boat, GraphicsContext gc) {
        gc.setFill(boat.getColor());
        gc.save();
        Dot dot = new Dot(boat.getPosition().getXValue(), boat.getPosition().getYValue());
        Circle circle = new Circle(dot.getX(), dot.getY(), 1.5, boat.getColor());
        //add fade transition
        FadeTransition ft=new FadeTransition(Duration.millis(20000),circle);
        ft.setFromValue(1);
        ft.setToValue(0.15);
//        ft.setOnFinished(event -> raceViewPane.getChildren().remove(circle));
        ft.play();
        this.raceViewPane.getChildren().add(circle);
        gc.restore();
    }


    /**
     * Draw background of the racecourse
     * @param gc GraphicsContext
     * @param width double - width of the canvas
     * @param height double- height of the canvas
     */
    private void drawBackground(GraphicsContext gc, double width, double height) {
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
            drawLine(startLine, dataSource.getStartMarks());
            drawLine(finishLine, dataSource.getFinishMarks());

//            // check if boundary needs to be redrawn
            if (dataSource.getCourseBoundary() != courseBoundary) {
                courseBoundary = dataSource.getCourseBoundary();
                drawBoundary(gc);
            }
        }
        List<Competitor> competitors = dataSource.getCompetitorsPosition();
        //move competitors and draw tracks
        for (Competitor boat : competitors) {

             timeFromLastMark = Converter.convertToRelativeTime(dataSource.getMessageTime(), boat.getTimeAtLastMark());

            if (counter % 70 == 0) {
                drawTrack(boat, gc);
            }
            this.drawWake(boat);
            this.drawBoat(boat);
            this.moveAnnotations(boat);

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
