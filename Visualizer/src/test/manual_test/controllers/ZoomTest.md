##Testing zooming
#####Intent: 
To test that the boat can be zoomed in correctly
#####Reason: 
Testing zooming is hard to do with automated tests due to the need to read data from google maps
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. Sail around for a bit
6. Press q to zoom in 
7. Press `A` or `D` to zoom in/out more
7. Sail around for a bit
8. Press q to zoom out
#####Check:
* Trails are only drawn when zoomed in
* Boat is always located at the center of the screen when zoomed in
* The course and google maps are in sync each other 
* When pressing A or D the course elements scale with the zoom


*last updated 13/Sep/2017*
