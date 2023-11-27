# scala-multimodule

To reproduce the problem with ZIO logging provided through ZLayer not working when fork is used, run application using IntelliJ run Configuration
* as a tasks set "manager/run"
* uncheck "Use sbt shell"

* in build.sbt on line 379 switch "Compile / run / fork" between true and false and run application each time to see difference in console logs

When "Compile / run / fork" is set to true, ZLayer won't take affect and ZIO logging settings will not apply to console logging. 
