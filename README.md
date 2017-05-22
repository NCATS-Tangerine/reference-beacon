# Reference-Beacon

### Eclipse Project

The project is a Gradle build. Managing the project within Eclipse generally requires the Gradle "BuildShip" tooling and importing the project from Git, then (re-)importing the same project as a Gradle project.

### Run Configuration

Move or copy and paste `config/application.properties` and `config/ogm.properties`to `server/src/main/resources/application.properties` and `server/src/main/resources/ogm.properties`, respectively. Then customize the database credentials in the properties files to your needs. You can change the port on which this server is run in the `server/src/main/resources/application.properties` file.

To run this server execute `gradle build` in the root directory, and then execute `java -jar build/libs/translator-knowledge-beacon-4.0.6.jar`. Then, navigate to `http://localhost:8080/api` in your browser. 

## Code Generation

If the API specification changes, we can re-generate the server by running `sh generate.sh <specification-file> [<output-dir>]` on Linux. The script will download the `swagger-codegen-cli.jar` file and automatically use it to generate the server stub. Upon doing this, the server project will be overwritten. We will have to manually go back into each of the controller classes and wire them back up to the appropriate methods in `ControllerImpl`. This is why the implementation of these classes is being kept within `ControllerImpl`.