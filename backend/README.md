# OOSE Blog Comic Backend

## Guideline to build the image of project with GraalVM native-image

To run the application using Docker, follow these steps:
1. First you need to have GraalVM version 21 for on your computer
2. Set the PATH and JAVA_HOME environment
3. For the windows it could require native-image install (also gcc compiler )
4. run ./mvnw -Pnative native:compile -DskipTests (On the project root path)
5. After build successfully, try ./target/Blog to test
6. Build the Docker image:
   ```
   docker build -t blog .
   ```

7. Run the Docker container:
   ```
   # On Linux:
   docker run --rm --env-file .env blog
   ```
## Database Connection



The application connects to a MySQL database. Make sure your MySQL server is running and accessible at the host and port specified in the `.env` file.

By default, the application tries to connect to a MySQL database at `host.docker.internal:3306`. This hostname resolves to the host machine's IP address from within the Docker container.