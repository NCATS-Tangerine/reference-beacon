# Reference-Beacon

### Setup

Move or copy and paste `config/ogm.properties` to `server/src/main/resources/ogm.properties`. Then fill out the properties file.

To run this server execute `gradle build` in the root directory, and then execute `java -jar build/libs/translator-knowledge-beacon-4.0.6.jar`. Then, navigate to `localhost:8080` in your browser. You can change the port on which this server is run in the `server/src/main/resources/application.properties` file.

## Code Generation

If the API specification changes, we can re-generate the server by running `sh generate.sh` on Linux. The script will download the `swagger-codegen-cli.jar` file and automatically use it to generate the server stub. Upon doing this, the server project will be overwritten. We will have to manually go back into each of the controller classes and wire them back up to the appropriate methods in `ControllerImpl`. This is why the implementation of these classes is being kept within `ControllerImpl`.