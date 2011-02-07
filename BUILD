
1. Requirements
---------------
To build the project you need to install the following:

- Java JDK 1.6 or higher
- Maven 2, version 2.2.0 or higher.

2. Build and deploy locally
---------------------------
At the top level run
   mvn install

This command will go through all the modules and install them in the local
maven repository (at ~/.m2/repository). The install process will download
any required dependencies, compile the code (under src/main/java), compile 
and run the tests (under src/test/java), and package the jar file with
the necessary OSGi headers.

The headers are derived from the pom information and that provided in the
osgi.bnd file

3. Build a single module
------------------------
You can build and deploy locally a single module by issuing a
   mvn install

in a single module

TIP: How to skip the tests
During development we often don't want to run the tests all the time 
You can skip them by issuing:
   mvn -Dmaven.test.skip=true install

4. Integration tests
------------------------
Most tests in the project are unit test but there are some integration tests are identified by classes ending with
IT unlike unit tests that end in Test.

Integration tests are not run by default to make the builds faster and the normal mvn test won't execute them, to
run the unit tests you need to issue:
    mvn test failsafe:integration-test failsafe:verify

5. Use with IntelliJ idea
------------------------------
Idea works best by just importing the pom.xml as a project file definition

6. Generate application
-----------------------
Applications are just other modules that define a list of bundles to
deploy and configuration. They use the assembly plugin and will produce 
a zip file with all the required bundles and configurations.

As an example go to gmp-server and read the instructions on how to build
the application there

7. Pax-construct
----------------
Pax construct is a set of scripts that make it easier to build the pom files, add new bundles and new
dependencies

You can get it at:

Install pax-construct 1.4 or higher and put the scripts on the path.
You can get pax construct from
http://repo1.maven.org/maven2/org/ops4j/pax/construct/scripts/1.4/scripts-1.4.zip
Expand the zip file, for example to ~/Java/pax-construct-1.4 and add the bin dir to
your path

7.1 Add new bundle
------------------
You can use pax-create-bundle at the project level to create a new bundle for example as:

pax-create-bundle -p edu.gemini.aspen.gmp.mypackage -n "mypackage"g edu.gemini.aspen.gmp -v 0.1.0

This will create a dir mypackage with a pom.xml using the given packages and groupId/artifactId/version
and will add the module to the main project's pom.xml

7.2 Add bundle dependency
-------------------------
If you have a dependency in maven which is a OSGi bundle it can be added as

pax-import-bundle -g groupId -a artifactId -v version

And it will be added as a provision dependency under provision/pom.xml

