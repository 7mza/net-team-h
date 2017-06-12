#### About
Peer to peer JAVA project, M1 WI UJM 2015-16  
Separated into two projects : Hub (tracker) and Player(client)  
  
### How to run
you need :
- maven 3
- jdk 8
  
to build hub : mvn package  
to run hub : java -jar targer/hub-2.0-SNAPSHOT.jar

to build player : mvn package  
to run player : java -jar targer/player-2.0-SNAPSHOT.jar

### Architecture
- Hub : for handling start/stop/libr
- HubHandler : for handling networking operation
- Util : shared common utilities
- HubCommands : Spring shell commands related to HUb
- Player : for handling start/stop/connect/libr
- PlayerHandler : for handling networking operation
- Util : shared common utilities
- PlayerCommands : Spring shell commands related to player

