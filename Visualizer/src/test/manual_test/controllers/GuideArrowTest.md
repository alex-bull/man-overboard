##Testing guide arrow
#####Intent: 
To test that the guide arrow always points towards the next mark or the centre of the gates
#####Reason: 
Visual aspect needs to be accurate and clear in order to guide the user where to go
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. Sail towards the starting line
6. Press Q to zoom in 
7. Sail over the starting line
8. Press Q to zoom out
#####Check:
* Arrow points in a fixed place towards the start line when zoomed out
* Arrow points towards the start line when zoomed in and remains with the boat, changing angle when necessary
* After the boat crosses the start line the arrow points towards the first mark
* When zoomed out the fixed-place arrow points towards the first mark

*last updated 13/Sep/2017*
