##Testing rip image is shown correctly with multiple players
#####Intent: 
To test that when a boat dies the cross appears consistently in multiplayer mode
#####Reason: 
The icon cannot be unit tested. It is an image that replaces the boat when it dies.
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run at least two instances of App through intellij or Visualizer.jar 
3. Connect to localhost on App.
4. Press ready
2. Kill one boat by collision with boundary or marks
3. With the other boat, zoom in and check for the rip image
4. Check again zooming out
5. With the boat that is dead, zoom in and out as well
6. With the alive boat, kill it
7. Now check zooming in and out
#####Check:
* The image should be shown upwards and not at an angle. E.g. boat’s heading
* The image should be consistent on both screens.
* When zooming in and out, the image should stay in place relative to the course

*last updated 13/Sep/2017*