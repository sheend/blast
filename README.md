[Product website](http://blastcse.github.io/) </br>
[Developer website](http://mkhuat.github.io/blast-dev/)

#Feature-Complete Updates
[SRS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwNWpsT1R6UHRMUVE/view) and [SDS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwa2ZFdG41X0Q5djQ/view) updates. </br>

As of the feature-complete release, a user is able to login with Facebook, view events happening within the upcoming 24 hours, attend and leave an event, create an event, and modify or delete an event they have created. </br>
- Note: events that have "expired" will stop appearing in the main view. Additionally, users are allowed to create events that will happen further than 24 hours in the future, but these events will not be displayed in the main view until they are happening within 24 hours.</br>

When a user is creating an event, the location field will give suggestions for real locations that match the typed string. This allows us to extract latitude and longitude coordinates. We also get the latitude and longitude of the user’s device, but currently this is hardcoded to Seattle for convenience. We have refined our UI and added tutorials for each different screen a user will see that appears the first time they login.
 </br>
- Because the app is still in development, users must use the test account `oliver_efugadr_queen@tfbnw.net` with password `blast123` to sign in. </br>
- Because the app is still in development, to activate location services on their device, users must go to the [Blast Developer Console](https://console.developers.google.com/apis/credentials/key/0?project=vivid-art-123423) in Incognito mode (right click on the link and open in incognito window). Sign in using the account `blastcse@gmail.com` with password `blast123`. At the bottom of the page, enter your local IP Addresss and click save. Note: access may take up to 5 minutes to be granted and direct link won't work if you switch Google accounts.</br>

To run our test coverage tool, select all of our tests, right click, and choose "Run with Coverage." At the moment, we have 10% line coverage. This is much lower than we expected due to the fact that the coverage tool does not recognize part of our tests as testing the actual lines of code in our activities, which is where the bulk of our logic lives.

#Beta Updates
[SRS](https://drive.google.com/file/d/0B3PwQkCDyLnwbWJ4SnRZYzhYVzg/view) and [SDS](https://drive.google.com/file/d/0B3PwQkCDyLnwYTZEZTVPOUV4UWs/view) documents have been updated! </br>

We have two use cases functioning at this time: logging in with Facebook and creating an event. After logging in, the user is able to view all the events currently in the database. They can click on an event to view details, but they are not yet able to "attend" an event. They are able to create their own events though, which are sent to the database and thus displayed in their view. </br>
- Because the app is still in development, users must use the test account `oliver_efugadr_queen@tfbnw.net` with password `blast123` to sign in. </br>

Scroll down to the Design Patterns section to view the update to our Developer Documentation.

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

#Design Patterns
With	any	graphical	user	interface,	it	becomes	important	to	separate	out	different	parts	of	the application	logic,	making	the	code	more	understandable	and	easier	to	update.	The	most popular	pattern	used	in	this	type	of	software	design	is	Model-View-Controller. While	MVC	is	a	more	commonly	used	pattern,	it	is	a	little	pedantic	and	forces	a	one-size-fits-all	philosophy	on	the	software. Because	Activities in Android	are	not unambiguously	just	one	of	the	three	parts in MVC, Blast uses	the	**Model-View-Presenter**	pattern.	Following	is	a	small	description	of MVP,	where	the	Activities	can	be	better	described	as	a	middle	man.
- Model: is	a	data	access	layer	with	necessary	information	storage	and	database	requests
- View: is	a	layer	that	displays	data	and	reacts	to	user	actions
- Presenter: is	a	layer	that	provides	the	View	with	data	from	the	Model	and	handles	any	background	tasks

Another design pattern part of this project is the **Singleton** pattern. For the FacebookManager file, we will be using this pattern because it allows for centralized management of resources and as well as a global point of access to itself. This is especially important because of the authentication details that Facebook provides need to be accesible at all points of use and in all Activity files in order for the user to not be logged out every time they switch pages. 

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
We have set up Jenkins to build the project daily: [Jenkins dashboard](http://54.191.131.33:8080/)
- It's automated to run every day at 12:00am and email the blast group if a build fails
- To see build history, click the build history tab on the left side bar
- To execute a build, go to the `Blast_App` job and click build now on the left side bar
- To configure Jenkins's settings, from the dashboard go to Manage Jenkins -> Configure System and find `Gradle version 2.11-rc-3` and `JDK version 7u80`

#New Release
For each milestone release, we will merge all branches to the master and then build that for the user. We will also tag the master branch with the following tags so all versions can be reverted to, if necessary. These tags are the primary method of documenting releases. After every release, we will make a new `APK` that can be downloaded by a user for the latest stable version of the app. The new version will be visible to the world on the master branch, and we will double check by building the repository in a new directory to make sure everything work fine. </br>
- Zero Feature: `v0.0`
- Beta: `v1.0`
- Feature Complete: `v1.5`
- Final: `v2.0` </br>

In order to do a testing release, push to the Git `develop` branch. At the end of each day, we will run and test the code currently on `develop`, and then accordingly assign tickets and issue bug reports. This way the software can be tested without potentially breaking the publically released version.</br></br>


#####Welcome to the team :punch:
