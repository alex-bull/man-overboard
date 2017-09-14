##Testing Boat Collisions
#####Intent: 
The intent of this test is to check that boats react correctly to making an impact with other boats or a course marker.
#####Reason: 
There are visual aspects of this feature that would be too difficult/take too long to code which would hinder progress.
#####Steps:
######Single boat/client:
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. Steer boat in direction of a course mark or boundary.
6. Wait until the boat collides with the course marker or boundary.

######Multiple boats/clients:
1. Run App through intellij or Visualizer.jar as many times as you wish the number of boats/clients to be.
2. Run BoatMocker through intellij or Mock.jar
3. Connect to localhost on all instances of App that are running before the window for connecting closes.
4. Press ready on all clients
5. Focus on one instance of App and press boat control keys.
6. Steer boat into other boats or course markers or boundary.
7. Wait for collision.
8. **Optional**: Repeat with other instances/windows of App.
#####Check:
* On collision with another boat both boat is pushed in a sensible direction(according to physics)
* And all of the common checks at the bottom of this document

##Testing Course Boundary Collision
#####Intent: 
To test that the boat collides with the course boundary.
#####Reason: 
There are visual effects when the boat collides with the course boundary which is not ideal to test it with automated tests
#####Steps: 
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. Make boat sail towards the course boundary (dotted lines) using controls (W and S)
#####Check:
* Boat collides with **any** of the edges of course boundary and gets knocked back
* Visual effect for collision is shown, see above test
* And all of the common checks at the bottom of this document

----
#####Common Checks:
* On collision with any object, the screen shakes, health is reduced and red border is displayed.
* The health bar’s length decreases
* As it decreases the colour of the bar should change from Green -> Yellow -> Orange -> Red
* Health bars updates are visible for all boats



*last updated 13/Sep/2017*

