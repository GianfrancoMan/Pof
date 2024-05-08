# POF *(Planty Of Food)*.
## Technology
[JavaSE ](https://www.oracle.com/java/technologies/java-se-glance.html)  
### To use the application locally:
- Clone or download the repository
- Go to the project folder and run the following commands to generete the class files:
  ```
  javac manca/pof/Main.java
  ```
  ```
  javac manca/pof/model/*.java
  ```
  ```
  javac manca/pof/service/*.java
  ```
  ```
  javac manca/pof/controller/*.java
  ```
- To generate the executable jar file run
  ```
  jar cfe food.jar manca.pof.Main manca/pof/Main.class manca/pof/controller/*.class manca/pof/service/*.class manca/pof/model/*.class
  ```
- To launch the application run
  ```
  java -jar food.jar
  ```
# Description
### This is  a Java application that simulates the sales management of the POF (Planty Of Food) point of sale.The application is for command line interface and the data source consists of the following csv files which will be loaded during application initialization
... in progress
