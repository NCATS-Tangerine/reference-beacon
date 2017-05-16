# Reference-Beacon

##Setup

Move or copy and paste `config/ogm.properties` to `server/src/main/resources/ogm.properties`. Then fill out the properties file.

To run this server clone it, then go into the root directory and execute `gradle build`, and then execute `java -jar build/libs/translator-knowledge-beacon-4.0.6.jar`. Then, navigate to `localhost:8080` in your browser. You can change the port on which this server is run in the `server/src/main/resources/application.properties` file.