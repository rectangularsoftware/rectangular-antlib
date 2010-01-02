Uncommons Antlib - Tools for building modular software with Apache Ant
(C) Copyright 2008-2010 Daniel W. Dyer

SYNOPSIS

  The Uncommons Antlib provides macro definitions, custom tasks and other tools
  to support the development of modular Java software using Apache Ant.  It
  builds on the concepts, processes and best practices established in the
  development of several Ant-based projects, particularly the Uncommons.org
  open source projects.

  The guiding principle behind the design of the Uncommons Antlib is convention
  over configuration. Ant build scripts are greatly simplified by relying on
  consistent directory structures and naming conventions.


LICENCE

  Uncommons Antlib is licensed under the terms of the Apache Software Licence
  version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.txt).


DEPENDENCIES

  Uncommons Antlib requires APACHE ANT VERSION 1.7.1 or later.


MACROS

  Clean Module        - Deletes all build artifacts for a specified module.

  Compile Module      - Compiles the main source tree and test source tree for
                        a specified module (dealing with all necessary
                        classpath configuration).

  JAR Module          - Builds a JAR file for a specified module.  Includes
                        main classes and omits test class. Sets the Class-Path
                        and Main-Class manifest properties as required.

  Test All Modules    - Runs TestNG unit tests, generates reports using
                        ReportNG and measures code coverage with Cobertura.

  Deploy Maven Module - Constructs a Maven POM and uploads the module JAR file
                        to a Maven repository with source JAR and test source
                        JAR attached.


CUSTOM TASKS
 
  DocBook - Generates formatted documentation from DocBook XML files using
            Saxon and Apache FOP.

            Usage looks something like this:
                <taskdef uri="antlib:org.uncommons.antlib"
                         resource="org/uncommons/antlib/antlib.xml"
                         classpathref="antlib.classpath"/>

                <uncommons:docbook classpathref="antlib.classpath"
                                   source="book/src/book.xml"
                                   format="pdf"
                                   outputDir="book/output">
                  <parameter name="paper.type" value="A4"/>
                </uncommons:docbook>


            The task supports PDF, RTF and HTML output.  Output can be
            customised with nested parameters that are passed to the bundled
            DocBook 5.0 XSL stylesheets.

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
                test
            resources
        build
            classes
            generated
        lib
