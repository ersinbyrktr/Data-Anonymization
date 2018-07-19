# Data-Anonymization

## Installation
Cloning the library by simply executing 

`git clone https://github.com/ersinbyrktr/Data-Anonymization.git`

is enough to download the library. We used Maven framework to handle all dependencies except ARX library since it wasn’t possible because of lack of framework support in ARX. So, in case an IDE used, the import operation of modules in pom.xml will be achieved automatically. 
On the other hand for ARX library a manual operation might be required depending on the IDE. 

For Intellij IDE:
Right clicking on lib/libarx-3.7.0.jar file and click on the “Add as Library...” option.

For Eclipse:
Go to Properties (for the Project) -> Java Build Path -> Libraries , select lib/libarx-3.7.0.jar  and click on the source , there will be option to attach the source and Javadocs.
## Database Setup
### a) Local Database Setup
To install the databases locally we provide a docker-compose file, which can be used to spawn all three required databases (mongodb, mysql and postgresql). For an alternative without docker see b). To start the docker containers execute “docker-compose run” in the console from the folder containing the “docker-compose.yml” file. This will start the containers locally and expose the needed ports. Please use for the connection address “localhost” or if docker runs on a VM, the IP of the VM. The full connection setting are as follows.

- MongoDB
    
    ConnectStr = mongodb://localhost:27017/test
    
    Collection Name = privacy
    
- MySql
    
    ConnectStr  = jdbc:mysql://localhost:3306
    
    Db = peng

    User = peng

    Password = admin
- Postgres

    ConnectStr = jdbc:postgresql://localhost:5432
    
    Db = peng

    User = peng

    Password = admin

### b) Remote Database Setup 
For easier testing and in case docker is not available on the local machine we provide a hosted solution with all three databases deployed in the cloud (AWS ECS), which will be accessible until the end of the project. For that the connection settings are as followed.

- MongoDB
    
    ConnectStr = mongodb://18.185.114.122:27017/test
    
    Collection Name = privacy
    
- MySql
    
    ConnectStr  = jdbc:mysql://18.185.114.122:3306
    
    Db = peng

    User = peng

    Password = admin
- Postgres

    ConnectStr = jdbc:postgresql://18.185.114.122:5432
    
    Db = peng

    User = peng

    Password = admin
  
## Preparing Database / Importing Data

If you use the cloud hosted Databases, no data import is necessary. For the locally run database we provide the following files which can be run through the database interface to import the example data.

- Postgres.sql
- use_case_dataset.sql
- exampleMongoDBData.json
## Executing the Examples
The library provides three example implementations (“ExampleMongo, ExampleSQL…) with a small and easy to evaluate dataset, as well as one example (ExampleUseCase) with a bigger dataset (about 20000 rows), which represent a more complex system, as explained in the use case section. Following will be a short introduction of how the “examplPostgres” works.
…

All four examples can be run by executing the “public static void main” function. The result of the anonymization will be printed in the output console. Like shown here. 

##  Hierarchies
ARX uses full-domain generalizations for achieving the k-anonymity. These hierarchies should be provided by the user for each distinct value in the database requiring user to manually add all the values and corresponding hierarchies. We identified certain hierarchies like StarHierarchy and RangeHierarchy those does not require this overhead of preprocessing by the user and implemented those as part of database service. This provides common implementation for all the database services, by extending the generalized class user can benefit from these simplifications in their custom database service. However, all types of columns can be inferred for building hierarchies without user knowledge, for example, Street can be generalized with hierarchiy of Street, Area, District,City with increasing order of generalization and this cannot be inferred without user knowledge. We suggest building pool of such hierarchies to achieve reusability.
## Suppression based l-diversity: 
ARX in its current state can only process the data if all the data points in the input has l distinct values. We provided suppression based l-diversity that can be configured by users, in this mode database connector drops the values that do not meet the necessary l criteria and considers the data that satisfies l-diversity. To use suppression based l-diversity user need to set ARX configuration with l-diversity parameter as 1 and execute query against database with the “executeQueryWithSuppression”  provided by database connector. This gives partial data that is anonymized with l-diversity.

