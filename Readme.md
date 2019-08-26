1. [Project requirements](#easy-requirements) 
2. [Mount database and run app with docker](#dockerize)
3. [Managing project containers](#container-management)
4. [Example usages](#example-usages)
5. [Endpoints](#endpoints)

### <a name="easy-requirements"></a> 1. Project requirements
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

### <a name="example-usages"></a> 4. EXAMPLE USAGES
TO FILL

### <a name="endpoints"></a> 5. ENDPOINTS
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