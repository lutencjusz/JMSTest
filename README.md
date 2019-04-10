# JMSTest
The system created to test the capabilities of the JMS Appache-activeMQ 5.15.2 queue engine with the ability to configure the number of threads and the server load. The system consists of applications:
- `SOAModelESB` - in the SOA model acts as an ESB bus that processes data between queues
- `SOAJMSTestClient` - graphic client (heavy) with the ability to configure the load in terms of severity of SQL queries as well as the number of threads, parallelism and length of their work. The entire test is shown in the form of a short report.
The whole is adapted to show in eclipse.

The SOAJAR directory contains compiled to JARs. Files to run in a local Java (Windows) environment using SOA.bat.

# Instalacja JMSTest
In order to run the solution:
1. Install and run the JMS Appache-activeMQ server (5.15.2 or newer)
2. create the `SOAJAR` directory and copy files to it from the `SOAJAR` directory on the repository, creating the following structure:
```
.\SOAJAR\SOAModelESB.jar
.\SOAJAR\SOAJMSTestClient.jar
.\SOA.bat - plik uruchomieniowy (w systemach Linux należy na bazie SOA.bat utworzyć plik uruchomieniowy np. SOA.sh)
```
3. run batch (in eviroment Windows - SOA.bat)
