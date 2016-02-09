[Product website](http://blastcse.github.io/) </br>
[Developer website](http://mkhuat.github.io/blast-dev/)


#Developer Documentation
Install Android Studio and run the following from the desired directory</br>
- To work on the latest stable release</br>
```
git clone https://github.com/sheend/blast.git
```
- To work on the current release</br>
```
git clone https://github.com/sheend/blast.git
git checkout develop
```
All project files are now ready to be opened, compiled, built and run in Android Studio </br>
The project can run on the Android built-in emulators or third party emulators such as [Genymotion](https://www.genymotion.com/)

#Directory Breakdown
Inside `app/src/main`
- `/res` contains all resources including drawables, menus, layouts, and values
- `/java/cse403/blast` contains all Activities
- `/java/cse403/blast/Model` contains all Java objects for the project
- `/java/cse403/blast/Support` contains all extra Java classes

#Development Style
[Java style guidelines](https://google.github.io/styleguide/javaguide.html) </br>
[Android style guidelines](http://developer.android.com/design/index.html) </br>
- Activities names based on the general function i.e. `CreateEventActivity.java`
- Connected layout files have the same basic name i.e. `activity_create_event.xml` and `content_create_event.xml`
- JavaDocs style comments for method headers, in-line comments for novel additions
- Data structures must have representation invariants that can be tested

#Bug Tracking
Compiled list of bugs, along with open tickets to work on: [Github Issue Tracker](https://github.com/sheend/blast/issues) </br>
Once a ticket has been completed, any team member can close the issue </br>

#Unit Testing
Tests are located in `app/src/androidTest/java/cse403/blast`
To run a unit test in Android Studio:
- Click on Build Variants on the bottom left corner
- Select `Unit Tests` as a Test Artifact
- Navigate to a test file i.e. `ExampleUnitTest.java`
- Right click on the class name and click 'Run ExampleUnitTest'

#Automated Testing
We have set up Jenkins to build the project daily: [Jenkins dashboard](http://10.89.170.125:8080)
- **NOTE: the IP address is subject to change soon**
- It's automated to run every day at 12:00am and email the blast group if a build fails
- To see build history, click the build history tab on the left side bar
- To execute a build, go to the `Blast_App` job and click build now on the left side bar
- To configure Jenkins's settings, from the dashboard go to Manage Jenkins -> Configure System and find `Gradle version 2.11-rc-3` and `JDK version 7u80`

#New Release
For each milestone release, we will merge all branches to the master and then build that for the user. We will also tag the master branch with the following tags so all versions can be reverted to, if necessary.</br>
- Zero Feature: `v0.0`
- Beta: `v1.0`
- Feature Complete: `v1.5`
- Final: `v2.0` </br>

In order to do a testing release, push to the Git `develop` branch. At the end of each day, we will run and test the code currently on `develop`, and then accordingly assign tickets and issue bug reports. This way the software can be tested without potentially breaking the publically released version.

#####Welcome to the team :punch:
