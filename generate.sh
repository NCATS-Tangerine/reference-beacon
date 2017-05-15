# This script attempts to download swagger-codegen-cli.jar and then use
# it to generate a SpringBoot server stub

if [ -f "./api/swagger-codegen-cli.jar" ]
then
	echo "swagger-codegen-cli.jar already downloaded\n"
else

	echo "downloading swagger-codegen-cli.jar\n"
	# wget creates a file whether or not it's able to download it or not. So if there's a download error we want to delete the file created.
	wget http://central.maven.org/maven2/io/swagger/swagger-codegen-cli/2.2.2/swagger-codegen-cli-2.2.2.jar -O api/swagger-codegen-cli.jar || rm -f api/swagger-codegen-cli.jar
fi

if [ -f "./api/swagger-codegen-cli.jar" ]
then
	java -jar api/swagger-codegen-cli.jar generate -i api/knowledge-beacon-api.yaml -l spring -o server -c api/springBootServerGenerateOptions.json
	echo "\nCode generation a success.\n"
else

	echo "\nDownload failed! Aborting.\n"
fi

