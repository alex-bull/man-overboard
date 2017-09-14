##Testing Boat’s Sail state
#####Intent: 
The intent of this test is to confirm the effect of the sail in/out key when it is pressed.
#####Reason:
Testing the effect of the sail in/out key on the visualiser is not ideal as it is a visual effect.
#####Steps: 
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App
4. Press ready
5. Press sail in/out key (shift) to see the effect, remember to change the heading of boat
#####Check:
* When sail in/out key is pressed, the effect is shown on the visualiser. (Powered up or Luffing)
* The boat also moves or stops depending on the state. (Moving if sails are out, Stops when sails are in)

*last updated 13/Sep/2017*