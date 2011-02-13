Gradle Plugin for Eclipse
=========================
cloned by fmjrey 
----------------

This project is a clone of the original project [here on github](/gradle/eclipse-plugin).
It introduces a few changes to the original which are all contained in new branches.

Branches
-------

* [fmjrey](/fmjrey/eclipse-plugin/tree/fmjrey) -- my own master branch, which merges of the other branches below and may contain other commit found elsewhere
* [upgrade](/fmjrey/eclipse-plugin/tree/upgrade) -- upgrades of gradle are made in this branch
* [task-tab-subprojects](/fmjrey/eclipse-plugin/tree/task-tab-subprojects) -- new feature: add subproject tasks in launch configuration

Contributing
------------
If you want to provide a contribution it's best to do so by forking the original project and make your changes inside a new branch of the fork.
Committing changes into a branch that comes from the original project is not recommended, see discussion [here](http://stackoverflow.com/questions/4890432/git-workflow-for-development-on-fork) and [here](http://groups.google.com/group/github/msg/cfeb119173ddc9da)
You also need to create your own "master" branch in which you can merge all your branches and other commits from elsewhere you may want for yourself.
With each change/feature in its own branch it becomes easier for the original project to pull each branch independently, in other words it allows cherry-picking branches instead cherry-picking commits.
To signal your changes you can create issues or pull requests in the original project and link to the corresponding branch in your fork.
When the original projects commits new changes, you need to bring these changes into your fork by rebasing your new branches.

