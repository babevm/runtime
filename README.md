# Babe VM Runtime Classes

The classes required by a running Babe VM instance + plus a few more.  That is, the 'runtime' class library.

The Babe Virtual Machine can be found here: [https://github.com/babevm/babevm](https://github.com/babevm/babevm).

In many cases the classes here are smaller custom versions of the standard Java SE classes.  This code is not a copy of the Java SE code not it is associated with the Oracle Java classes in any way. 

Consider the classes here as cut down versions with enough functionality to support an implementation of a Java Virtual Machine Specification (JVMS) to Java 1.6 level.  Specifically, to support the Babe VM.  

As with all Java VMs and their runtime libraries, they each know a little about each other.  In a number of the classes, some methods are marked as `native` and therefore have their implementation written in 'C' inside the Babe VM.  Also, the Babe VM sometimes assumes existence and order of some fields in classes.       

Using maven, a `mvn clean install` will build all.  When using these classes with the Babe VM, point the `-Xbootclasspath` command line option to either the jar file produced by the build, or to the expanded `classes` folder that contains the compiled classes.  The VM will work with either. 

