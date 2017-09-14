##Testing TCPClient and TCPServer
#####Intent: 
To ensure that the communication between Mock and Visualizer is intended, and multiple hosts can connect to the same server
#####Reason: 
Testing TCP/IP communication between two modules is hard, and testing connection across different PC is impossible
#####Steps:
1. Log in to two PC A and B
2. Find the ip address of A with ifconfig command(Linux) or ipconfig(Windows)
3. Run App.java on A and B, with B’s server address changed to A’s ip address in settings
4. Run BoatMocker.java on A
5. Connect to the server with A and B
#####Check:
* There are two boats with different sourceID
* Boats have different color
* All controls work as intended, click the question mark at top left corner to see all available controls

*last updated 13/Sep/2017*