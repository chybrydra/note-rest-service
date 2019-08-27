1. [Project requirements](#requirements) 
2. [Mount database and run app with docker](#dockerize)
3. [Managing project containers](#container-management)
4. [Example usages](#example-usages)
5. [Endpoints](#endpoints)

### <a name="requirements"></a> 1. Project requirements
For running the service the easiest way, we will need:
- docker or docker toolbox
- docker-compose which should be included by default to docker/docker toolbox
- maven release 3.0 or later

### <a name="dockerize"></a> 2. Mounting database and running project with docker-compose
##### To mount database () we will need to:
1. check if maven is installed and configured by running command: ```mvn -version```
2. Open main project directory in terminal. You will know you're in the right place, if there is a ```docker-compose.yml``` here. 
3. Have docker running, you can verify it by running f.e. ```docker ps``` command
4. Run command: ```docker-compose up```. Docker-compose will:
    - mount mysql database and expose it to port 9001
    - mount phpmyadmin and expose it to port 8081 (user: root, password: pass)
    - mount note service and expose it to port 8084 using OpenJDK 8, which makes us able to run app even without JDK installed locally.   
5. The service is now available at:
    - for standard docker: ```http://localhost:8084/api/notes```
    - for docker-toolbox: ```http://[docker-machine-ip]:8084/api/notes```

If we use docker toolbox (it is required for some Windows versions) then docker runs on Virtual Machine. 
In this case, our containers are not available at localhost.

To check docker-machine-ip, we need to run command: ```docker-machine ip```

### <a name="container-management"></a> 3. Managing project containers
##### Now there are 3 new containers running. We only need a few docker commands to control containers:
- ```docker ps``` - shows all running containers
- ```docker ps -a``` - shows all containers (even those that are stopped)
- ```docker stop <container hash> <another container hash> ...``` - stop container, we usually need only 3 first characters of the hash as they should be unique
- ```docker start <container hash> <another container hash> ...``` - run container, also needs only 3 characters of hash
- to see container hashes we use ```docker ps [-a]```
- ```docker rm ```

##### Additionally for Docker Toolbox:
- ``` docker-machine ip``` - to get our docker ip address
- ```docker-machine stop [virtual machine name]``` - to stop virtual machine with docker
- ```docker-machine ls``` - to view docker virtual box name

### <a name="example-usages"></a> 4. Example usages
##### Example usages using Postman
! for Docker Toolbox use docker-machine ip instead of `localhost`. Command to find docker-machine ip is `docker-machine ip`.  
! instead of reading this point, you can go to ```http://localhost:8084/api/swagger-ui.html``` which contains swagger-ui service documentation.

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

### <a name="endpoints"></a> 5. Endpoints
! instead of reading this point, you can go to ```http://localhost:8084/api/swagger-ui.html``` which contains swagger-ui service documentation.
##### Endpoints:
- ```GET: /api/notes``` - returns recent versions of all notes as JSON
- ```GET: /api/notes/{id}``` - returns recent version of a note with id={id}
- ```GET: /api/notes/{id}/history``` - returns change history for note with id={id}
- ```POST: /api/notes``` - saves new note if request body contains proper object (description below)
- ```PUT: /api/notes/{id}``` - updates note with id={id} if request body contains proper object
- ```DELETE: /api/notes/{id}``` - deletes note with id={id}

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