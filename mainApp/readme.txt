This is a (very) simple Java application


Instructions using Maven:
------------------------

To compile:
  mvn compile

To run using exec plugin:
  mvn exec:java

To generate launch scripts for Windows and Linux:
  (appassembler:assemble is attached to install phase)
  mvn install

To run using appassembler plugin:
  On Windows:
    target\appassembler\bin\java-app test 1 2 3
  On Linux:
    ./target/appassembler/bin/java-app test 1 2 3


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2018-02-18
leic-sod@disciplinas.tecnico.ulisboa.pt
