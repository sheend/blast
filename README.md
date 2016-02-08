#Developer Documentation
Install Android Studio and run the following from the root directory</br>
- To work on the latest stable release</br>
```
git clone https://github.com/sheend/blast.git
```
- To work on the current release</br>
```
git clone https://github.com/sheend/blast.git
git checkout develop
```

#Directory Breakdown
Inside `app/src/main`
- `/res` contains all resources including drawables, menus, layouts, and values
- `/java/cse403/blast` contains all Activities
- `/java/cse403/blast/Model` contains all Java objects for the project
- `/java/cse403/blast/Support` contains all extra Java classes

#Development Style
Java style guidelines: https://google.github.io/styleguide/javaguide.html </br>
Android style guidelines: http://developer.android.com/design/index.html </br>
- Activities names based on the general function i.e. `CreateEventActivity.java`
- Connected layout files have the same basic name i.e. `activity_create_event.xml` and `content_create_event.xml`
- JavaDocs style comments for method headers, in-line comments for novel additions
