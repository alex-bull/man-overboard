##Testing mark rounding
#####Intent: 
To test that the user cannot advance to the next leg without properly rounding the mark
#####Reason: 
We do not want the user to cheat by “rounding” a mark without going around it
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready

######for mark rounding
5. Sail over the start line
6. Sail towards the first mark
7. Pass close by the mark but on its left side so the mark has not technically been rounded 
8. Circle back and pass the mark on its right side

######for gate rounding
5. Sail over the start line
6. Sail around the first mark
7. Sail towards the next gate (northward)
8. Pass through the gate
9. Immediately turn and go back through the gate without rounding either mark
10. Go through the gate again and round one of the marks


#####Check:
######for mark rounding
* Arrow continues to point towards the mark until the user passes it properly - defined by crossing an imaginary line intersecting the start gate and the first mark, on the opposite side of the first mark
* When the boat crosses the imaginary line the arrow switches to point towards the next gate
######for gate rounding
* Arrow continues to point towards the gate’s centre until the user rounds one of the marks which comprise the gate
* When the boat crosses the imaginary line the arrow switches to point towards the next gate

*last updated 13/Sep/2017*