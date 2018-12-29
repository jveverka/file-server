[![Build Status](https://travis-ci.org/jveverka/file-server.svg?branch=master)](https://travis-ci.org/jveverka/file-server)

# FileServer 
This FileServer makes specified *base directory* accessible via REST APIs allowing you 
to list, download, upload, move and delete files and create empty directories. It also provides 
user access control and security.

![architecture](docs/architecture-01.svg)

## Features implemented
* Standalone java server, running on linux system.
* Runs on the top of file system of host server.
* Provides access via REST APIs to the file system.
* File system operations supported
  - list / read content of the directory
  - download file, upload file
  - create empty directory
  - delete file or directory
  - move file or directory
* Uses Role based access control 
* Supports multi-tenancy (many users and roles)
* Access for anonymous users (user not logged in) 
* Transport protocols: http or https
* Single jar distribution (~16MB).  

## Planned features
* web UI / web client for REST APIs
* admin UI for user access management (users and roles)
* compressed directory download
* compressed directory upload
* dynamic user access management (local database required)

## Requirements for developers
* [JDK 11](https://jdk.java.net/11/) or later (JDK 8 is supported as well)
* [Gradle 5.0](https://gradle.org/next-steps/?version=5.0&format=bin) or later

## Runtime requirements
* [JDK 11](https://jdk.java.net/11/) or later (JDK 8 is supported as well)
* Requires read/write access to *base directory* on local file system.

### Rest Endpoints
All REST endpoints use 'dynamic' path. This means that path ``**`` is used as relative path in *base directory*.
See also [postman collection example](docs/FileServer.postman_collection.json).

#### Get volume info
* __GET__ http://localhost:8888/services/files/storageinfo - get info about storage (storage base path, free space and total space)  
  ``curl -X GET http://localhost:8888/services/files/storageinfo -b /tmp/cookies.txt``

#### Get list of files  
* __GET__ http://localhost:8888/services/files/list/** - list content directory or subdirectory  
  ``curl -X GET http://localhost:8888/services/files/list/ -b /tmp/cookies.txt``

#### Download file  
* __GET__ http://localhost:8888/services/files/download/** - download file on path. file must exist.   
  ``curl -X GET http://localhost:8888/services/files/list/path/to/001-data.txt -b /tmp/cookies.txt``

#### Upload file
* __POST__ http://localhost:8888/services/files/upload/** - upload file, parent directory(ies) must exist before upload  
 ``curl -F 'file=@/local/path/to/file.txt' http://localhost:8888/services/files/upload/path/to/001-data.txt -b /tmp/cookies.txt``

#### Delete files and/or directories
* __DELETE__ http://localhost:8888/services/files/delete/** - delete file or directory  
  ``curl -X DELETE http://localhost:8888/services/files/delete/path/to/001-data.txt -b /tmp/cookies.txt``

#### Create empty directory
* __POST__ http://localhost:8888/services/files/createdir/** - create empty directory  
  ``curl -X POST http://localhost:8888/services/files/createdir/path/to/directory -b /tmp/cookies.txt``

#### Move file or directory
* __POST__ http://localhost:8888/services/files/move/** - move file or directory. If source is file, destination must be also a file, If source is directory, destination must be directory as well.
  ``curl -X POST http://localhost:8888/services/files/move/path/to/source -b /tmp/cookies.txt -d '{ "destinationPath": "path/to/destination" }''``

### Security
In order to use file server REST endpoints above, user's http session must be authorized.
Users have defined their roles and access rights to files and directories. 
See this [example](src/main/resources/application.yml) of server configuration.

#### login
* __POST__ http://localhost:8888/services/auth/login  
  ``curl -X POST http://localhost:8888/services/auth/login -H "Content-Type: application/json" -d '{ "userName": "master", "password": "secret" }' -c /tmp/cookies.txt``

#### logout
* __GET__ http://localhost:8888/services/auth/logout  
  ``curl -X GET http://localhost:8888/services/auth/logout -b /tmp/cookies.txt``

### Build and Run
Variable ``file.server.home`` in ``application.yml`` file defines *base directory* to be exposed via REST APIs.
```
gradle clean build test
java -jar build/libs/file-server-1.0.1-SNAPSHOT.jar --spring.config.location=file:./src/main/resources/application.yml
```
