##Testing speed power ups can be picked up and used
#####Intent: 
To test that speed boosts can be picked up by a boat and used
#####Reason: 
The icon is part of the visualiser and needs to be toggled to invisible/visible when a power up is picked up and then the boat should move faster when it is used
#####Steps:
1. Run BoatMocker through intellij or Mock.jar
2. Run at least two instances of App through intellij or Visualizer.jar 
3. Connect to localhost on App.
4. Press ready
5. Wait for about 30 seconds and a power up should appear
6. Move one boat to pick up the power up
7. The boat that picked it up should have a power up icon 
8. Press D to activate the boost on the boat that picked it up
#####Check:
* The power ups should keep spawning at approximately every 30 seconds and disappear in about 1 minute
* The boat that picked it up can only press D to use the power up
* When the power up is used the boat should speed up 
* The icon should disappear on both screens when it is picked up

##Testing potion power ups can be picked up and used
#####Intent: 
To test that potions can be picked up by a boat and used
#####Reason: 
The potion icon is part of the visualiser and needs to be toggled to invisible/visible when a power up is picked up and then the boat should increase health when used
#####Steps:
1. Start at least two instances of app and one instance of boat mocker normally (see previous tests)
2. Wait for about 30 seconds and a health potion should appear
3. Move one boat to pick up the potion
4. The boat that picked it up should have a potion icon shown below the health bar
5. Press F to activate the potion on the boat that picked it up
#####Check:
* The potions should keep spawning at approximately every 40 seconds and disappear in about 1 minute
* The boat that picked it up can only press F to use the potion
* When the potion is used, the boat’s health should increase by half its max value 
* The potion icon should disappear on both screens when it is picked up



*last updated 13/Sep/2017*