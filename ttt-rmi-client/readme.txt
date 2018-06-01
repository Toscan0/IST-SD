This is the Java RMI implementation of the Tic Tac Toe game client.

The client depends on the interface module, 
where the interface shared between server and client is defined.
The client needs to know the interface to generate the proxy.


Instructions using Maven:
------------------------

Make sure that you installed the interface module first.

To compile and copy the properties file to the output directory:
  mvn compile

To generate launch scripts for Windows and Linux:
  (appassembler:assemble is attached to install phase)
  mvn install

To run the RMI client:
  Using Maven exec plugin:
    mvn exec:java
  Using Maven appassembler plugin:
    On Windows:
      target\appassembler\bin\ttt-rmi-client
    On Linux:
      ./target/appassembler/bin/ttt-rmi-client


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2018-03-03
leic-sod@disciplinas.tecnico.ulisboa.pt
