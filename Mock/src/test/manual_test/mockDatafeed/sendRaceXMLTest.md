## Testing sendRaceXML

#####Intent:
The intent of this test is to check that the race.xml is sent to the clients is correctly formatted with the current date and time
#####Reason:
The sendRaceXML() in BoatMocker requires manual testing because it formats the xml file with the current date and time information, which is hard to test in JUnit.
#####Steps:
1. Open intellij and run `Mock/src/test/java/mockDatafeed/BoatMockerTest`
2. Run `Visualizer/src/main/java/controllers/App`
#####Check:
* Check the output of sendRaceXMLTest is correct, especially RaceID, CreationTimeDate, and RaceStartTime. These should be today’s date and time

*last updated May/2017*