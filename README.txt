Jillion installation instructions:

======================
| Use Download Jar:  |
======================
Down load the latest Jillion jar file and then put it in your classpath.

 =======================
| To build from Source: |
 =======================
 Jillion has both a Maven POM file as well as an Apache ANT file that can both be used
 to build source and test files. So use which ever is easier for you to (Maven is recommended).
 
 Jillion 5 requires Java 8 or higher to run.

 To Build with Maven
 -------------------
 Once Java  and Maven are installed on your system,
 from the root directory of a Jillion check-out type:
 
 %mvn clean install
 
 This will build jillion, run all the unit and integration tests and installs it in your local repository.
 Jillion is now ready to use.
 
 To Build with Ant
 -----------------
 Once Java  and Ant are installed on your system,
 from the root directory of a Jillion check-out type:
 
 %ant release
 
 This will compile all source files and create a new file in the root directory 
 named "Jillion-${version}.jar"
 
 Then put the build jar in your classpath.
 
 ==============
| Bug Reports: |
 ==============
 
 Please report any bugs to the Bug Tracker on Jillion's sourceforge page:
 
 https://sourceforge.net/p/jillion/bugs/
 
 Please include the version and SVN revision number if you know it in any bug reports.
 
 Thank you,
 
 Danny Katzel
 
