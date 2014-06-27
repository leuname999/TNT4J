Why TNT4J 
=====================================
Several key features make TNT4J a prime logging choice for java applications:
* <b>Performance</b>: No need to concatenate messages before logging. Simply log using message patterns as follows:
	* `logger.info("My message {0}, {1}, {2}", arg1, arg2, arg3);`. 
* <b>Simplicity</b>: No need to check for `isDebugEnabled()` before logging messages. Just register your own `SinkEventFilter` and consolidate all checking into a single listener.
	* `logger.addSinkEventFilter(new MyLogFilter());` All conditional logging can be consolidated into a single listener object. 
* <b>Granular conditional logging</b>: Log only what is matters. Increase performance of your apps by decreasing the amount of logging your app produces and yet increasing relevance and quality of the output.
	*  `logger.isSet(OpLevel.DEBUG, "myapp.mykey", myvalue);` Checking a global debug level is not granular enough for most applications. Many java apps require granular logging to log only what matters.
* <b>Share logging context across apps</b>. Pass logging context across apps programatically or via a shared cache.
	* `logger.set(OpLevel.DEBUG, "myapp.mykey", myvalue);` Imagine writing an application that has to pass logging flag to apps downstream, how would you do that? TNT lets you do that using this method.
* <b>State logging</b>: log application state to improve diagnostics of performance, resource and other problems which are hard to trace using standard event logging techniques. Simply register you dump listener and export state variables specific to you application. Dump listeners can be called on VM shutdown or on demand.

Embed TNT4J into application and realize the benefits in matter if minutes. TNT4J can take advantage of other lower level logging frameworks such as log4j.

About TNT4J
======================================

Track and Trace 4 Java API, Application logging framework for correlation, diagnostics and tracking of application activities within and across <b>multiple applications, runtimes, servers, geo locations. This API is specifically designed to troubleshoot distributed, concurrent, multi-threaded, multi-user applications</b> and includes activity correlation, application state dumps, performance and user defined metrics.

Here is short list of TNT4J features:

* Simple programming model to facilitate fast root-cause, log analysis
* Automated timing of application activities and sub-activities (elapsed, idle time, message age)
* Application state dump framework for reporting internal variables, data structures
* Granular conditional logging based on application tokens, patterns, that can be shared accross applications, runtimes
* Share logging context across application, thread, runtime boundaries
* Inter-log correlation of log entries (correlators and tags) between multiple related applications
* Intra-log correlation of related activities and sub-activities between multiple applications and threads
* Event location tags such as GPS, server etc.
* Message flow direction for composite applications that exchange messages (e.g. SOAP, JMS, and SQL etc.)
* User defined properties such as CPU, memory logging, thread statistics per process/thread
* Extensible activity, sink, error listeners for pre, post event processing
* Granular context such as thread id, process id, server, application name

See TNT4J tutorial (http://www.slideshare.net/AlbertMavashev/open-source-application-behavior-tnt4j-tutorial)
See Getting Started (https://github.com/Nastel/TNT4J/wiki/Getting-Started) for quick reference on TNT4J.
Wiki is available at https://github.com/Nastel/TNT4J/wiki

TNT4J Mission
=======================================
* Standard way to track application behavior, activities accross users, apps, servers, devices, threads
* Dramatically reduce time it takes to troubleshoot application behavior using logging paradigm
* Performance metrics and application state to reduce diagnostic time
* Simple programming model for ease of use
* Improve quality and readability of logs to accelerate diagnostics
* Enrich log entries for automated analysis. Manual analysis is just painfully long
* Decrease or eliminate development of custom code required to track behavior and activities
* Independent of the underlying storage, formats

TNT4J Concepts
========================================
TNT4J is fully plug-in and play tracking, tracing and logging framework that consits of the folliwng basic constructs:

* <b>Tracker</b> -- high level object that allows developer to track, trace and log application activities
* <b>Actvity</b> -- a collection of related tracking events (TrackingEvent) and other sub-activities, relation is established via a grouping specified by a developer or set of correlators (across thread, application boundaries). Activities may have a set of uder defined properties which are grouped into property snapshots (PropertySnapshot).
* <b>Tracking Event</b> -- a message with associated start/stop time stamps, severity, user defined message, correlator, tag, location (such as GPS, server etc) and other event properties.
* <b>Property</b> -- key, value pair
* <b>Property snapshot</b> -- a collection of properties with category, name and a timestamp associated with when snapshot is taked. Actvities may have one or more property snapshots.
* <b>Formatter</b> -- an object responsible for formatting underlying TNT4J objects such as Activity, Tracking Event and convert into a formatted string.
* <b>Tracking Selector</b> -- an object associated with a Tracker that allows developers to perform conditional logging based on a given set of severity, key, value combination. Such combinations are stored in token repository.
* <b>Token Repository</b> -- an underlying storage used by tracking selector that actually stores and maintains severity, key, value combinations. Such repository can be backed by a file, cache, memory or any other desired medium. Token repositories can be shared accross application boundaries and therefore conditional logging can span multiple applications, runtimes, geo locations.
* <b>Sink</b> -- sink is a basic destination where obejcts can be written.
* <b>Event Sink</b> -- destination where events, activities and messages are recorded. Such destination can be file, socket, etc.
Sinks are usually associated with formatters which are called to format objects before writting to the sink.
* <b>Dump Sink</b> -- sink where application dumps are recorded.
* <b>Dump</b> -- a property snapshot that deals with application state (name, value pairs). Application can generate user defined dumps to report application specific metrics during diagnostics, on demand or VM shutdown.
* <b>Dump Provider</b> -- user defined implmenetation that actually generated application Dumps.


How to Build TNT4J
=========================================

Requirements
* JDK 1.6
* ANT (http://ant.apache.org/)

TNT4J depends on the following external packages:
* Apache commons configuration 1.10 (http://commons.apache.org/proper/commons-configuration/)
* Apache commons lang 2.6 (http://commons.apache.org/proper/commons-lang/)
* Apache commons lang3 3.0.1 (http://commons.apache.org/proper/commons-lang/)
* Apache commons logging 1.2.17 (http://commons.apache.org/proper/commons-logging/)
* Apache Log4J 1.2.17 (http://logging.apache.org/log4j/1.2/)

To build TNT4J:
* Download the above libraries and place into the tnt4j-master/lib folder
* Compile and build using ANT: 
	* ant all (run "ant clean" for clean builds)
	* Check ../build/tnt4j for output
	* JavaDoc will be located under ../build/tnt4j/doc
	

Verify TNT4J
===============================================
* Run a test program (com.nastel.jkool.tnt4j.examples.TNT4JTest) using the following:
	* CD to ../build/tnt4j
	* `java -javaagent:tnt4j-api.jar -Dlog4j.configuration=file:log4j.properties -Dtnt4j.dump.on.vm.shutdown=true
	-Dtnt4j.dump.provider.default=true -Dtnt4j.formatter.json.newline=true -classpath tnt4j-api-final-all.jar
	com.nastel.jkool.tnt4j.examples.TNT4JTest com.myco.TestApp MYSERVER "Test log message" correlator1 "TestCommand"  TestLocation`

`-javaagent:tnt4j-api.jar` command line option is required by `ObjectDumpProvider` to calculate object deep and shallow memory sizes. Use this only if your application makes use of ObjectDumpProvider to dump object state.

`-Dtnt4j.dump.provider.default=true` java property allows application state dumps generated automatically upon VM shutdown.

`-Dtnt4j.formatter.json.newline=true` java property directs `JSONFormatter` to append new line when formatting log entries.

See `tnt4j-event.log` and vmid.dump files for output produced by `com.nastel.jkool.tnt4j.examples.TNT4JTest` program.

See `tnt4j.properties` for TNT4J configuration: factories, formatters, listeners, etc. See Wiki for more information.
