# FileServer configurations
FileServer may be configured in several distinct ways. This chapter explains different configuration scenarios.

## Configuration files
* __application.yml__ - main configuration file loaded when server starts.
This is main configuration file. FileServer may be forced to use custom application.yml file 
when started with parameter ``--spring.config.location=/path/to/application.yml``
* __json configuration files__ - used for file-system persistence, see below for reference.

## Web server configuration
Web server is configured in __application.yml__ file. 
This is example of simple http server configuration.
```
server:
  port: 8080
  session:
    timeout: 10 #http session timeout in minutes
```
This is example of https server configuration.
```
server:
  port: 8443
  session:
    timeout: 10 #http session timeout in minutes
  ssl:
    key-store: /path/to/keystore.jks
    key-store-password: secret
    keyStoreType: JKS
    keyAlias: localhost
```

## Server data persistence
FileServer uses data about users and file access permissions in order to handle user requests. 
Some data may be changed using admin REST APIs. FileServer supports following persistence models.

### In-Memory persistence
This is default persistence model. Users and file access permissions are loaded from __application.yml__ file when server starts.
Admin users may change initial data using REST APIs, however all changes are lost when server is shutdown.
Single file application.yml is used for initial configuration. This is example snippet of application.yml user and file acces filter configuration.
```
fileserver:
   home: /opt/file-server/files
   data:
     storage: inmemory
   anonymous:        
     role: anonymous 
   admin:            
     role: master    
   users:
     - username: master
       password: secret
       roles:
         - master
         - public
         - anonymous
   filters:
     - path: '*'
       access: READ_WRITE
       roles:
         - master         
```

### File-System persistence
Users and file access permissions are loaded from json data files, 
and every modification in those data is persisted back in json files.
Required json files:
* __file-access-manager-data.json__ - stores file access filters. See [this](../src/main/resources/filesystem-configs/file-access-manager-data.json) example.
* __user-manager-data.json__ - stores user data. See [this](../src/main/resources/filesystem-configs/user-manager-data.json) example.
* __audit-data.log__ - log file which keeps audit records. New records are appended to the end of this file.

FileServer expects data files to be located in ``fileserver.data.basedir`` directory.
When FileServer is started or data is changed via admin REST APIs, new data is stored on the file system, json files are overwritten.
```
fileserver:
   home: /opt/file-server/files
   data:
     storage: filesystem
     basedir: /opt/files-erver/data
```