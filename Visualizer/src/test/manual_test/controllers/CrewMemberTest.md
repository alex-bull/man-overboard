##Testing falling crew members
#####Intent: 
To test that crew members are falling during collision and you can pick them up when near them
#####Reason: 
As crew members are falling randomly it’s hard to write an automated test for it, also most of this feature is visualizing which is untestable unless done manually
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run at least two instances of App through intellij or Visualizer.jar 
3. Connect to localhost on App.
4. Press ready
5. Collide with boundary
5. Collide with course feature
6. Collide with some other boat
7. Pickup crew by going near them 
#####Check:
* there is 32% chance of dropping a crew member, 10% for 2
* Crew members are scattered randomly near point of collision, and have animations
* Boats can pick crew members up when going near them
* Health is gained when picking up crew members
* Check that maximum velocity is decreased when health is low
* Check that maximum velocity is regained when picking up crew members

*last updated 13/Sep/2017*