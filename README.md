# TimeIT Server
TimeIT server is a time tracking tool and as such has two purposes.

1. To be a central hub for multiple TimeIT clients
(https://github.com/Hoglet/TimeIT) so that all clients have the
same data.

2. To be able to use through a web browser as a simple time logging
tool.

The databases are decentralized which means that all nodes, including the
server, works stand alone without connection to each other.

The web interface is naturally more limited than the clients. There is no
automatic time tracking and no idle detection.

## Building and running
1. Download the source into a folder of choice
2. Open a terminal in source folder
3. Run following command:
   1. Unix: ./gradlew jar
   2. Windows: gradlew jar
4. java -jar ./build/libs/server-1.0.jar
5. Start a browser and point it to http://localhost:8080/
   Initial username and password: admin

## Choosing an alternate database backend
TimeIT Server is by default using H2 as database. This works for small amounts
of data but quickly becomes slow as number of time slices increases.

Testing has been done with postgresql (and shortly with mariadb) and is used in
the server used by the author.

To change preferred database you need to edit src/main/config/template-config.yml

## Hacking the code
Fork the code here:
https://github.com/Hoglet/TimeIT-Server/fork

To generate a project for eclipse you can then run:
./gradlew eclipse

## Known problems
* No multi language support. Only English available
* One timezone, the servers timezone.
