## Welcome
This is my project's readme. I will more or less follow the deliverables section of the instructions and proceed from there. But first a very brief overview of the project, what I was able to implement, and what I was not. 

I was not able to implement the front end in the sense it does not look like screens, however the functionality of each screen is there as is how you would work the application / website as a whole. I was also not able to implement folders though I think I was completely overthinking this (I, for some reason, could not find simple guides online about working with folders and MySQL databases ... I was planning on generating JPA entities for each folder and then joining their corresponding different tables in MySQL to create a sort of folder hierarchy within the tables in the database ... I am sure the actual way to implement directory hierarchies is much easier.) 

My project has a little bit of CSS and a decent amount of HTML however I had never worked with these before. The website is completely functional but I, for example, did not know exactly how to implement overlays or how to turn the image in my head into exactly what was on the screen. If had a little more time to get fully immersed in the front end world I am sure I could do it. Still (in my opinion) the website looks nice (though I may be biased). More discussion of features I implemented, those I did not, and the Frankensteinish ones (they are implemented but due to lack of knowledge in a design earlier on they are *a little weird* (though if you use the application as designed you will not encounter this ... but users do not always do that). 

## Demo

https://user-images.githubusercontent.com/108889300/186744202-6a5ac7bb-0bd6-4de6-95c9-b6a4be5bd05e.mp4

## Server and Client Source Code
This is available on the GitHub. 

## How to Setup and Run my Application
Firstly, I (unfortunately) did not know better and used MySQL as the database which is fine and all however you cannot embed a database within the application (like you can with the server and Tomcat) (I wish I had used h2 instead). I ultimately wanted the user to simply run "java -jar filedriveapplication.jar" and then connect to the server at "localhost:8080" and that is that. 

Still, I was very close to this and this is what you need to do (I have Windows however I'd imagine the instructions are similiar regardless):
1. **First we need to set up the MySQL Database / Schema  and name it "files".** 

	a) Install the MySQL Installer at https://dev.mysql.com/downloads/installer/. Run the installer wizard, we are going to need: MySQL Server (8.0), MySQL Workbench (8.0) and MySQL Connector/J (8.0). Everything with default options however make sure the TCP/IP port is 3306 (though this is default). 
	
	b) Load up MySQL workbench and create a MySQL Connection: 
		Connection Name: "files" (not including quotes).
		Port: 3306
    
	c) Create a new schema called "files".
  
2. **Verify you have JDK 17 or newer installed.**

	This can be located here: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
  
3. **Download my zip file, unzip, and run it with java -jar.**

	Next download "File Drive Application.zip" located here in this Git and unzip it somewhere. Then open a terminal and navigate to ~YOURPATH/filedriveapplication/0.0.1-SNAPSHOT and run "java -jar filedriveapplication-0.0.1SNAPSHOT.jar"

**You should now see Spring start and one of the last lines should be something like: 
Tomcat started on port(s): 8080 (http) with context path " ".**

Great! Now my server is running and you can open your browser and run "localhost:8080".

## Features Implemented (and not implemented .. and sort of implemented)

**Backend**

Most of the backend is completely implemented and works as intended. The main piece I could not implement (as mentioned before) was folder functionality. 

Nevertheless, users can register accounts with their name, email, and password. These users are stored are in the "users" table in the database, as generated by Hibernate from the Java User class thanks to Spring Data JPA. 

From here they can log in, upload files, delete files, share files, view their files, view the files they are sharing, and view the files that are being shared with them. They can download their files and the files being shared with them. 

Files are capped by the local MySQL server property: max-allowed-packet (and also I limited the file upload and download size to 1GB on the Tomcat Server). 

For some more detail: there are two main classes of data types in my project: Files and Users. They each have their corresponding JPA repositories and their JPA Entities. All this means is that they are given tables in the MySQL database thanks to Hibernate, and you can perform search, save, and delete operations. This is important for things like: creating new users or files, making sure there is not already an account with your username, checking for duplicate files, or deleting files. The more troublesome part was managing the relational mapping within Java and corresponding it with MySQL so changes made on the Java client side propagate through the server and into the database, or making sure they do not propagate enough (cascading types).

Sharing files between users was very interesting because I had to fuse together two tables within the JPA context and then create the table and query from it so you can view what files you are sharing or vice versa (look for the "MyFile_User" in MyFile.java). The AppController class contains all the interesting GETs/POSTs commands and can provide some insight into how data propagates between pages and from HTML to Java or vice versa. 

The main frankenstein thing was the handling of sharing files with an email not associated with a user. In this case I created a user for them with a default name and password so that they could then log in and view the contents. This would require someone to change their name and password but this is not yet implemented. (and don't worry all passwords are encrypted .. check the database).

With all things on this project it was all a learning experience and while I tried to follow the style of that which I found online, most of how I built everything was from learning from online resources and stitching together a solution. As such at times I may have done two different techniques to accomplish the same thing (for example sending data between backend and frontend). 

**Frontend**

I never was an art major. Before this project I had never really messed around with HTML or CSS so I knew this part was going to be my bane. Still, the functionality of the frontend is there (it shows all files you currently have and all files you are sharing due to iterating through the set of files sent from the backend) but certain visual aspects from the screens are not there: like the overlays or the hamburger tiles or the panes. 

But all in all I think the website looks okay although maybe bland (I did try to give meaning to the kind of buttons you were working with ... there may not be a lot of visual variety but when there was I tried to make it count). 

**Dependencies and Third Party Components**

All in all this was a Java Spring Boot Maven project that used Thymeleaf, Hibernate, Spring Security, and MySQL Connector Plugin to interact with the MySQL database.

**Spring Boot** - I used this (because you told me to) because it makes developing web applications in Java much easier (my first ever framework ... very exciting). 

**Maven** - This was used to manage dependencies ... this sort of works behind the scenes. This was also instrumental in deploying the application.

**Thymeleaf** - This was a frontend dependency that I saw reccomendded on a few Spring Boot tutoriel pages. It makes working with data from backend in the frontend easier though the more I worked with it the more I realized it is not exactly essential. 

**Hibernate** - This was allowed me to create this project built on MySQL but not manually type more than 5 queries. This is magic and allowed me to create classes and objects in Java and have them correspond to database tables and columns by automatically generating queries for me. 

**Spring Security** - This was used, as the name suggests, for security purposes. This provides login functionality and checking users' authentication and which pages they can see without being logged in. 

**MySQL Connector Plugin** - Fairly self explanatory.
