# Simple Product REST Application

This is a simple REST application implementing CRUD operations over Products.

Products can be anything, from milk to car tires.
They have a name, description, 10-digit identifying code, availability and price (in HRK and EUR currencies).

## 1) Installation & Setup of Application

Below are detailed instructions for setting up this application.


### 1.1) Download (Cloning the Git Repository)
First things first, you need to download the source code to your local computer.

Open your console and type in:

<code>
git clone git@github.com:jseric/simple_product_rest.git
</code>

### 1.2) Importing Application into the IDE
The next step is importing the application into the IDE. These instructions will guide you how to import into IntelliJ IDEA.
If needed, instructions for other IDEs can be added later on.

#### 1.2.1) IntelliJ IDEA
- Open your IntelliJ IDEA application
- In the "Welcome to IntelliJ IDEA" window, select "Open".
- Next, the "Open File or Project" window should open. In it, find the location of the git repository you cloned in step 1.
Once you find the directory, expand it and click on the "pom.xml" file and select "Ok".
- Next, the "Open Project" window should open. In it, select "Open as Project" option.
- In case the next window is labeled "Trust and open Project 'simple_product_rest'?", select "Trust Project" option.

### 1.3) Setting up Run Configurations
The next step is setting up running configurations for the application.

- In the Menu bar, select "Run" > "Edit Configurations". "Run/Debug Configurations" window should open.

#### 1.3.1) Editing the Spring Boot Running Configuration
The Spring Boot running configuration is the configuration you would use to start up the application.

- In the "Run/Debug Configurations" window, select the existing "SimpleProductRestApplication" (inside "Spring Boot" section)
- Rename the configuration to <code>SimpleProductRestApplication develop</code> (or to something else you want)
- For that configuration, under "Active profiles", write: <code>develop</code>
  - This will ensure that the application is started with the "develop" maven profile 
- Select "Apply"


#### 1.3.2) Creating a Maven Configuration for Running Tests
Another configuration will need to be added for running tests.
This is done because the testing section requires some different settings, such as HNB (Croatian National Bank) API URI.

- In the "Run/Debug Configurations" window, select the "+" button.
- In the dropdown menu, select "Maven" option.
- Now you need to add a few settings for the new configuration. 
  - Under "Name", write: <code>simple_product_rest [test]</code>
    - Under "Run", write: <code>test -f pom.xml</code>
  - Under "Profiles", write: <code>test</code>
 
 
## 2) Setting Up the Database
Next, you will find instructions for setting up the database

## 2.1) Creating the Database and Tables
In the directory <code>db_scripts/simple_product_db/</code> you will find all the necessary scripts for setting up the database.
- <code>createDatabase.sql</code> - Script for creating the simple_product_db database.
- <code>public/product/createTable.sql</code> - For the newly created database, run this script to create the Product table inside the <code>public</code> schema.

## 2.2) Modifying the Properties File
Next, you might need to change the properties file of the Java application, to ensure correct DB connection URL and credentials.

- Open up <code>src/main/resources/application-develop.properties</code> and check and/or change the URL, username and password for the database (lines 1-3).
- Next, open up <code>src/main/resources/application-test.properties</code> and repeat the previous step.


# 3) Running the Application and Tests
Now, you can finally run the application or start the tests.

## 3.1) Running the Application
- To run the application, find and click the "Open 'Edit Run/Debug Configurations' Dialog" button.
  - It is located in the 2nd menu bar, on the right side, between the "Build Project" (hammer icon) and "Run" (run icon) buttons.
- In the dropdown menu that appears, select the <code>SimpleProductRest develop</code> configuration.
- Next to the "Open 'Edit Run/Debug Configurations' Dialog", select "Run" or "Debug" button, for respective actions.

## 3.2) Running the test scenarios
- To run tests, find and click the "Open 'Edit Run/Debug Configurations' Dialog" button.
- In the dropdown menu that appears, select the <code>simple_product_rest [test]</code> configuration.
- Next to the "Open 'Edit Run/Debug Configurations' Dialog", select "Run" or "Debug" button, for running or debugging, respectively.


# 4) Try out the application REST API
You can try out the API via different Web clients or applications.
In this document, you will find instructions for Postman, an application for testing APIs.

You can download postman here: https://www.postman.com/downloads

After downloading and installing Postman, open it up and import the API collection (<code>postman_collections/Simple Product REST API.postman_collection.json</code>).
- In Postman, select "File" > "Import".
- In the Import window, select "Upload files" and select the API collection file mentioned above.

The Simple Product REST API collection contains all the REST API calls that can be made towards the application.

# 5) The End
This is it. You can now try out the API.
