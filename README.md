# JDepend

![Java CI with Gradle](https://github.com/nelkinda/jdepend/workflows/Java%20CI%20with%20Gradle/badge.svg)

## History
JDepend was originally developed by Mike Clark of [Clarkware](http://clarkware.com/) / [Pragmatic Studio](https://pragmaticstudio.com/).
Due to lack of maintenance, JDepend no longer works with new Java projects.
Because of that, JDepend has even been removed from Gradle.
This is an attempt to update and revive JDepend.
It is in its early stage.
Contributions are welcome.

## Status/Plan
- [x] Fix compilation issues with JDK9 and newer
- [x] Import project into Gradle
- [x] Get most unit tests to pass
- [x] Add Checkstyle and fix all Checkstyle issues to improve code quality.
- [ ] Add PMD and fix all PMD issues to improve code quality.
- [ ] Add SonarLint and fix all SonarLint issues to improve code quality.
- [ ] Add SonarQube and fix all SonarQube issues to improve code quality.
- [ ] Update Unit Tests to JUnit 5.
- [ ] Increase type safety (generics etc)
- [ ] Remove dependencies on deprecated classes
- [ ] Use Java Modules
- [ ] Remove unnecessary dependencies on modules, for example, the tests should not depend on `java.desktop`
- [ ] Increase unit test branch coverage to 100%
- [ ] Add PiTest and increase unit test mutation coverage to 100%
- [ ] Build API documentation with Gradle
- [ ] Add annotations to allow POJOs and enums to be declared abstract for the purpose of dependency analysis.
- [ ] Split into multi-module project for library, Swing UI, Text UI, and XML UI.
- [ ] Create Maven plugin
- [ ] Create Gradle plugin
- [ ] Create Ant task

## What Is It?

JDepend traverses Java class and source file directories and generates design quality metrics for each Java package.
JDepend allows you to automatically measure the quality of a design in terms of its extensibility, reusability, and maintainability to effectively manage and control package dependencies.

## Installation and Documentation

Documentation is available in HTML format, in the [docs/](docs/) directory.
For the installation and user manual, see [docs/JDepend.html](docs/JDepend.html).
For the API documentation, see [docs/api/index.html](docs/api/index.html).

Thanks for using JDepend!
