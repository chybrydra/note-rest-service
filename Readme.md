1. [Project requirements](#requirements) 
2. [Mount database and run app with docker](#dockerize)
3. [Example usages](#example-usages)
4. [Endpoints](#endpoints)
5. [Running tests](#tests)

### <a name="requirements"></a> 1. Project requirements
For running the service ([point 2](#dockerize)), we will need:
- docker or docker toolbox
- docker-compose which should be included by default to docker/docker toolbox (1.11.2 version of docker-compose was used for development)
- maven release 3.0 or later release installed and configured (mvn command available at terminal)

Requirements to run tests([point 5](#tests))
- java 8 (or later release, but 8 is recommended)
- optionally Sonarqube working at localhost:9000 if we want coverage

### <a name="dockerize"></a> 2. Mounting database and running project with docker-compose
##### To mount database and run project we will need to:

1. Check if maven is installed and configured by running command: ```mvn -version```
2. Open project root directory in terminal. You will know you're in the right place, if there is a ```docker-compose.yml``` here. 
3. Have docker running, you can verify it by running f.e. ```docker``` command which should list some options.
4. Run command: ```docker-compose up```. Docker-compose will:
    - mount mysql database and expose it to port 9001
    - mount phpmyadmin and expose it to port 8081 (user: root, password: pass)
    - mount note service and expose it to port 8084 using OpenJDK 8, which makes us able to run app even without JDK installed locally.   
5. Verifying if service was run correctly
- We know that app is running, if terminal provided us with message similar to: ```napi           | 2019-08-28 09:47:40,032 [main] INFO p.l.noteservice.NoteServiceApplication - Started NoteServiceApplication in 8.212 seconds (JVM running for 8.64)```
- If service did not run correctly, type ```docker-compose down -v```, delete {project-root}/docker/volumes directory and type ```docker-compose up``` again
6. The service is now available at:
    - for standard docker: ```http://localhost:8084/api/notes```
    - for docker-toolbox: ```http://[docker-machine-ip]:8084/api/notes```
7. To stop the service use CTRL+C in terminal that runs the service.
8. To rerun service, type ```docker-compose up``` in project root directory again.
9. To delete service containers from docker, just type ```docker-compose down -v``` while in project root directory. 
This will not delete MySQL data though. To delete MySQL data we also need to delete directory: {project-root}/docker/volumes.

>MySQL data is stored and accessible at path {project-root}/docker/volumes.
>At {project-root}/docker/volumes there is also mapped directory with service logs

>If we use Docker Toolbox (it is required for some Windows versions) then docker runs on Virtual Machine. 
>In this case, our containers are not available at localhost, but on docker-machine ip.
>To check docker-machine-ip, we need to run command: ```docker-machine ip```

### <a name="example-usages"></a> 3. Example usages
##### Example usages using Postman
> For Docker Toolbox use docker-machine ip instead of `localhost`. Command to find docker-machine ip is `docker-machine ip`.  

(1) Create GET request at ```http://localhost:8084/api/notes``` and send.
This should return information that no notes were found because no notes were persisted yet.  

(2) Create POST request at ```http://localhost:8084/api/notes``` with body "raw"->"JSON" as follows:
```json
{
  "title": "first note",
  "content": "this is my first note"
}
```
this will create a first note, then repeat with body:
```json
{
  "title": "second note",
  "content": "this is my second note"
}
```
at this moment there are 2 notes

(3) Send request from point 1. - this will show both notes, first note should be returned with id=1 if there were no records

(4) Now send GET request at ```http://localhost:8084/api/notes/1``` to show only the first note.

(5) Send PUT request at ```http://localhost:8084/api/notes/1``` with body:
```json
{
  "title": "first note",
  "content": "just edited first note"
}
```
and immediately:
```json
{
  "title": "first note",
  "content": "just edited first note again"
}
```
(6) Do again point 4, this will return recent version of first note

(7) Now sent GET request at ```http://localhost:8084/api/notes/1/history``` to view changes history for the first note

(8) Send DELETE request at ```http://localhost:8084/api/notes/1``` 

(9) Do again point 4 - note is deleted so there is only information that it was not found.

(10) Do again point 7

### <a name="endpoints"></a> 4. Endpoints

##### Endpoints:
- ```GET: /api/notes``` - returns recent versions of all notes as JSON 
>Request success: status 200 OK \
>Request error: status 404 NOT FOUND 
- ```GET: /api/notes/{id}``` - returns recent version of a note with id={id}
>Request success: status 200 OK \
>Request error: status 404 NOT FOUND
- ```GET: /api/notes/{id}/history``` - returns change history for note with id={id}
>Request success: status 200 OK \
>Request error: status 404 NOT FOUND
- ```POST: /api/notes``` - saves new note if request body contains proper object (description below)
>Request success: status 201 CREATED \
>Request error: status 400 BAD REQUEST
- ```PUT: /api/notes/{id}``` - updates note with id={id} if request body contains proper object
>Request success: status 201 CREATED \
>Request error: status 400 BAD REQUEST / 404 NOT FOUND
- ```DELETE: /api/notes/{id}``` - deletes note with id={id} (it is not really deleted, but accessible only at GET:api/notes/{id}/history)
>Request success: status 200 OK \
>Request error: status 404 NOT FOUND

##### Proper object for POST and PUT methods:
```json
{
  "title": "some title here",
  "content": "some content here"
}
```
both "title" and "content" are required, at least as empty string (cannot be null).

The examples below shows INVALID objects:
```json
{
  "content": "some content here"
}
```
```json
{
  "title": "some title here"
}
```

For error, json like below is returned in http response body:
```json
{
    "status": "NOT_FOUND",
    "timestamp": "29-08-2019 12:17:04 PM",
    "message": "Some message",
    "debugMessage": "Some more info",
    "errorPath": "http://localhost:8084/api/notes/9"
}
```

F.e. if we send POST Request ```http://localhost:8084/api/notes```
```json
{
	"title": "aaabb"
}
```
which is missing "content", then we will have returned:
```json
{
    "status": "BAD_REQUEST",
    "timestamp": "29-08-2019 12:17:29 PM",
    "message": "Binding exception.",
    "debugMessage": "content: must not be null;",
    "errorPath": "http://localhost:8084/api/notes"
}
```

### <a name="tests"></a> 5. App tests:

To run tests, we have a few approaches. We will need JRE 8 to be installed and configured
(or later release but it might not work exactly as expected).

a) To run unit tests:
>- open project root directory in terminal
>- type ```mvn clean test```

b) To run unit tests with coverage (this approach requires Sonarqube to be running at localhost:9000, then we can visit localhost:9000 to find project coverage):
>- open project root directory in terminal
>- type ```mvn clean test sonar:sonar```

c) To run integration tests:
>- open project root directory in terminal
>- type ```mvn clean test -Dprofile=test```

d) To test manually:
>- open project root directory in terminal
>- type ```mvn clean package``` to create *.jar
>- type ```cd target```
>- type ```java -jar NoteApp.jar --spring.profiles.active=test```
>- now at ```http://localhost:8084/api/h2``` there is an H2 in-memory database available, to log in use:
>>- url: ```jdbc:h2:mem:notes```
>>- user: ```root```
>>- password: ```pass```
>- now you can send requests using postman (examples described [here](#example-usages)) or using curl and see changes directly in database.