This is the Java RMI implementation of the Tic Tac Toe game server.
The server starts a built-in RMI registry.

The server depends on the interface module, 
where the interface shared between server and client is defined.
The server needs to know the interface to provide an implementation for it.


Instructions using Maven:
------------------------

Make sure that you installed the interface module first.

To compile:
  mvn compile

To generate launch scripts for Windows and Linux:
  (appassembler:assemble is attached to install phase)
  mvn install

To run the RMI server:
  Using Maven exec plugin:
    mvn exec:java 
  Using Maven appassembler plugin:
    On Windows:
      target\appassembler\bin\ttt-rmi-server
    On Linux:
      ./target/appassembler/bin/ttt-rmi-server


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2018-03-03
leic-sod@disciplinas.tecnico.ulisboa.pt
