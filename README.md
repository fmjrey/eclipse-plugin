Gradle Plugin for Eclipse
=========================
cloned by fmjrey 
----------------

This project is a clone of the original project [here on github](/gradle/eclipse-plugin).
It introduces a few changes to the original which are all contained in new branches.

Main Branches
-------------

*REBASE WARNING*
The only branches that will not be rebased are the main branches listed below. If you are cloning this repository you are strongly advised to create your own branches based on these. Other branches like topic branches may be rebased over time in order to keep track with upstream changes.

* [fmjrey](/fmjrey/eclipse-plugin/tree/fmjrey) -- my own master branch, which merges other branches below and may contain other commit found elsewhere

Topic Branches
--------------

* [upgrade](/fmjrey/eclipse-plugin/tree/upgrade) -- upgrades of gradle are made in this branch
* [task-tab-subprojects](/fmjrey/eclipse-plugin/tree/task-tab-subprojects) -- new feature: add subproject tasks in launch configuration

Contributing
------------
If you want to provide a contribution it's best to do so by forking the original project and make your changes inside your own new branch in your fork.
Having each change/feature in its own unique branch makes it easier for the original project to pull each branch independently, thus allowing cherry-picking branches before the need for cherry-picking commits.
Such branches are usually called "topic branches", as described [here](https://github.com/dchelimsky/rspec/wiki/Topic-Branches) and [here](http://stackoverflow.com/questions/284514/what-is-a-git-topic-branch).
You may also want to create your own "master" branch in which you can merge all your branches and other commits from elsewhere you may need for yourself.
To signal your changes you can create issues or pull requests in the original project and link to the corresponding branch in your fork.
When the original projects commits new changes, you need to bring these changes into your fork by rebasing your branches.
Once the topic branch has been merged into the original project, you probably want to delete it and rebase your own master branch.

