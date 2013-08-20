Rectangular Antlib - Tools for building Android apps with Apache Ant
(C) Copyright 2013 Rectangular Software Ltd.
(C) Copyright 2008-2013 Daniel W. Dyer

SYNOPSIS

  The Rectangular Antlib provides macro definitions, custom tasks and other
  tools to support the development of native Android apps using Apache Ant.  It
  builds on the concepts, processes and best practices established in the
  development of several Ant-based projects.  It is derived from the Uncommons
  Antlib (http://antlib.uncommons.org).

  The guiding principle behind the design of the Uncommons Antlib is convention
  over configuration. Ant build scripts are greatly simplified by relying on
  consistent directory structures and naming conventions.


LICENCE

  Rectangular Antlib is licensed under the terms of the Apache Software Licence
  version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.txt).


DEPENDENCIES

  Rectangular Antlib requires APACHE ANT VERSION 1.7.1 or later.


CORE MACROS

  Clean Module         - Deletes all build artifacts for a specified module.

  Compile Module       - Compiles the main source tree and test source tree for
                         a specified module (dealing with all necessary
                         classpath configuration).

  JAR Module           - Builds a JAR file for a specified module.  Includes
                         main classes and omits test class. Sets the Class-Path
                         and Main-Class manifest properties as required.

  Test All Modules     - Runs TestNG unit tests, generates reports using
                         ReportNG and measures code coverage with Cobertura.

  Javadoc              - Generates API documentation for all modules.

  Collate Distribution - Copies all build artifacts and dependencies into a
                         single directory to form a self-contained distribution
                         of the project.

  Release              - Builds release archives (ZIP and TGZ) containing
                         distribution, API docs and info files (README.txt,
                         LICENCE.txt, etc.)


ANDROID MACROS

  Android Resources    - Generates the R.java file for an Android project.

  Android Obfuscate    - Specialised version of the core obfuscate macro that
                         preserves Android components that shouldn't be
                         renamed.

  Debug Package        - Create an APK signed with the debug key for testing.

  Release Package      - Create an APK signed with a specified release key.


CUSTOM TASKS
 
  GZip    - The standard Ant GZip task only works with an individually
            identified file.  This task works with FileSets and separately
            compresses each matching file.


CONVENTIONS

  The Uncommons Antlib assumes that the project is divided into modules, each
  with its own directory in the project tree.  A module is a self-contained
  piece of the project that, when built, produces an artifact (typically a JAR
  file, WAR file, or similar). Uncommons Antlib assumes that each module
  directory is arranged according to certain conventions.  This assumption
  greatly simplifies build scripts as it is not necessary to inform the various
  macros where to find the required files.

  The project directory layout is like this:

    <projectdir>
        <module1>
        <module2>
        <module3>
        lib
        docs
            api
            test-results
            coverage

  And each module directory like this:

    <moduledir>
        src
            java
                main
                resources
                test
                test-resources

        build
            classes
            generated
        lib
