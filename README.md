# scala-multimodule

To reproduce the problem run application using IntelliJ run Configuration
* as a tasks set "manager/run"
* uncheck "Use sbt shell"

* in build.sbt on line 379 switch "Compile / run / fork" between true and false and run application each time to see difference in console logs
