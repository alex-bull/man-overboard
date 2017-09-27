##Testing Leaving a Game
#####Intent: 
The intent of this test is to check that the mocker gracefully handles players leaving throughout the game
#####Reason: 
It would take a very long time to write effective automated tests for this
#####Steps:
######Single boat/client clean disconnect:
1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. press quit button on screen
6. press leave
6. The client should go back to the start screen
#####Check:
* The server should restart
* The client should return to the start screen

#####Steps:
######Single boat/client unclean disconnect:

1. Run BoatMocker through intellij or Mock.jar
2. Run App through intellij or Visualizer.jar
3. Connect to localhost on App.
4. Press ready
5. Press esc to exit full screen
6. Close game window
#####Check:
* The server should restart

#####Steps:
######Multiple boat/client clean disconnect:

1. Run BoatMocker through intellij or Mock.jar
2. Run at least 2 app instances through intellij or Visualizer.jar
3. Connect to localhost on apps.
4. Press ready on apps
5. Press quit button on an app instance
6. Press leave on the app instance
5. Repeat steps 5 and 6 for all instances
#####Check:
* The boats should disappear from the other players view when they quit
* The server should restart only once the last app instance is quit

#####Steps:
######Multiple boat/client unclean disconnect:

1. Run BoatMocker through intellij or Mock.jar
2. Run at least 2 app instances through intellij or Visualizer.jar
3. Connect to localhost on apps.
4. Press ready on apps
5. Press esc to exit full screen on an app instance
6. Close the window for the app instance
5. Repeat steps 5 and 6 for all instances
#####Check:
* The boats should disappear from the other players view when they close the window
* The server should restart only once the last app instance window is closed

*last updated 27/Sep/2017*