# Reference-Beacon

https://rkb.ncats.io/swagger-ui.html

### Eclipse Project

The project is a Gradle build. Managing the project within Eclipse generally requires the Gradle "BuildShip" tooling and importing the project from Git, then (re-)importing the same project as a Gradle project. Note that the folder paths noted below are all relative to the 'reference-beacon' project folder (i.e. if your reference beacon code is in your home directory under the 'reference-beacon' folder, then 'config/application.properties' is actually the full path '~/reference-beacon/config/application.properties').

### Run Configuration

Move or copy and paste `config/application.properties` and `config/ogm.properties`to `server/src/main/resources/application.properties` and `server/src/main/resources/ogm.properties`, respectively. Then customize the database credentials in the properties files to your needs. You can change the port on which this server is run in the `server/src/main/resources/application.properties` file.

To run the database tests, you also need to copy the `config/ogm.properties` file to `database/src/test/resources/ogm.properties`.

To run this server execute 

	gradle clean build 

in the root directory, and then execute `java -jar server/build/libs/reference-beacon-#.#.#.jar` (where '#' are the current release numbers built there).

Then, navigate to `http://localhost:8080` in your browser. 
 
The application is also easily deployed into a Docker container using the provided Docker container. Assuming that you've installed Docker on your Linux machines and after performing your gradle build (above) in the reference beacon project root folder, type something like the following:

	docker build -t ncats:rkb .
	
to build the Docker image. You may then run the image using something like the following:

	docker run --rm -p 8075:8080 ncats:rkb &

Here, we've told docker to redirect the Docker port 8080 of the reference beacon web application, to a host system port 8075. This trick is useful if you are deploying several Java applications which default to using port 8080. The reference beacon should now be visible at **http://localhost:8075**.

## Code Generation

If the API specification changes, we can re-generate the server by running 

	sh generate.sh server <specification-file>

on Linux. The script will download the `swagger-codegen-cli.jar` file and automatically use it to generate the server stub. Upon doing this the server project will be overwritten, except for those files specified within `generate.sh`. We will have to manually go back into each of the controller classes and wire them back up to the appropriate methods in `ControllerImpl`. This is why the implementation of these classes is being kept within `ControllerImpl`.

