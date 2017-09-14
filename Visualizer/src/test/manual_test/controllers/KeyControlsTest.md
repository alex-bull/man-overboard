##Testing Upwind/Downwind key controls
#####Intent:
To test that the boat’s direction can always be controlled
#####Reason: 
Clicking on the radio buttons when the upwind/downwind control buttons were the up/down arrow keys resulted in the boat not being controllable because the keys were being used to switch radio buttons rather than upwind/downwind.
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. Steer boat using `W` and `A` keys
6. Select a radio button (or annotation)
7. Steer boat using `W` and `A` keys
#####Check:
* Boat responds to key presses by moving upwind when W is pressed and downwind when A is pressed at all times
* When holding `W` or `A` down the boat should not do a 360 turn

##Testing Sails In/Out controls
#####Intent: 
To test that the boat’s sails can be toggled.
#####Reason: 
Making sure that only the current boat/highlighted boat’s sails is toggled when the shift key is pressed.
#####Steps: 
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar twice
3. Connect to localhost on App.
4. Press ready
5. Steer boat outwards so they are not on top of each other.
6. Press shift key for a boat.

#####Check:
* When sail in/out key is pressed, the effect is shown on the visualiser. (Powered up or Luffing)
* The boat also moves or stops depending on the state. (Moving if sails are out, Stops when sails are in)
* The boat doesn't instantly gain its full speed or stop.
* The sail line switches from in/out ONLY for the highlighted boat.


*last updated 13/Sep/2017*
