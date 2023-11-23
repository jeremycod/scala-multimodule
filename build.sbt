import sbt.Keys.{libraryDependencies, resolvers}
import sbt.Tests.Setup
import sbt.complete.DefaultParsers.*
import sbtbuildinfo.BuildInfoPlugin

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / organization := "com.sample"

val AkkaVersion = "2.6.19"
val AkkaCirceVersion = "1.39.2"
val AkkaHttpVersion = "10.2.9"
val AkkaHttpTestkitVersion = AkkaHttpVersion
val AlpakkaSqsVersion = "3.0.4"
val AwsVersion = "2.17.268"
// used to eject earlier (1.11.251) version some dependencies rely on, to hopefully address vulnerabilities
val AwsV1Version = "1.12.323"
val AwsKinesisClientVersion = "2.3.4"
val CatsVersion = "2.9.0"
val CatsRetryVersion = "3.1.0"
val CatsEffectVersion = "3.3.14"
val ChimneyVersion = "0.6.1"
val CirceVersion = "0.14.3" // 0.13.0 gets ejected for 0.14.1 anyhow, so may as well consistently use 0.14.1
val CommonsCollectionsVersion = "3.2.2"
val CommonsCompressVersion = "1.21"
val CommonsTextVersion = "1.10.0"
val ConfigVersion = "1.4.0"
val cronUtilsVersion = "9.2.0"
val DatadogDdVersion = "1.17.0"
val DatadogJavaClientVersion = "4.2.0"
val DerevoVersion = "0.13.0"
val DiffsonVersion = "4.1.1"
val EstaticoVersion = "0.4.4"
val FicusVersion = "1.5.0"
val Fs2Version = "3.6.1"
val GroovyVersion = "2.4.21"
val GsonVersion = "2.8.9"
val GuavaVersion = "31.0.1-jre"
val GoogleProtoBuffVersion = "3.21.7"
val H2InMemDbVersion = "2.1.212"
val Http4sVersion = "0.23.19-RC1"
val Http4sJwtAuthVersion = "1.2.0"
val JacksonModuleVersion = "2.11.2"
val JacksonDatabindVersion = "2.13.4.1" // "2.11.3"
val JacksonDataformatCborVersion = "2.12.7"
val JaninoVersion = "2.7.8"
val JerseyMediaJaxbVersion = "2.31"
//val IoNettyCodec = "4.1.94.Final"
val JnrPosixVersion = "3.1.8"
val LaunchDarklyVersion = "5.7.0"
val LogbackVersion = "1.4.5"
val LogbackContribVersion = "0.1.5"
val LombokVersion = "1.18.22"
val MariadbJavaClientVersion = "2.7.1"
val MySqlConnectorVersion = "8.0.32"
val NimbusDsVersion = "9.0"
val OfferModelVersion = "6.30.1"
val PostgresqlVersion = "42.4.1"
val QuartzSchedulerVersion = "1.9.2-akka-2.6.x"
val RefinedVersion = "0.9.27"
val SangriaVersion = "2.1.6"
val SangriaCirceVersion = "1.3.2"
val SangriaAkkaHttpVersion = "0.0.3"
val SangriaSlowLogVersion = "2.0.5"
val SchemaRegistryOmpVersion = "1.8.3"
val ScalaCollectionCompatVersion = "2.4.4"
val ScalaJava8CompatVersion = "1.0.0"
val ScalaLoggingVersion = "3.9.2"
val ScalaCacheVersion = "0.28.0"
val ScalacheckVersion = "1.15.4"
val ScalamockVersion = "4.4.0"
val ScalatestVersion = "3.2.8"
val ScalatestScalacheckVersion = "3.2.8.0"
val SchematicCirceVersion = "2.3.3"
val SchematicSdpVersion = "2.3.3"
val SdpProducerSdkVersion = "5.2.6"
val ShapelessVersion = "2.3.7"
val ShedlockVersion = "4.20.0"
val Slf4jVersion = "2.0.5"
val SlickVersion = "3.3.3"
// The last version that's compatible with Slick 3.3.3, not upgrading further because all our slick statements break...
val SlickPgVersion = "0.20.2"
val SnakeYamlVersion = "1.31"
val SnappyJavaVersion = "1.1.10.4" //flagged by snyk
val tapirVersion = "1.9.0"
val TestcontainersVersion = "1.17.6"
val TestcontainersScalaVersion = "0.40.12"
val TypedMetricsClientVersion = "0.0.1"
val TransformServiceVersion = "1.186.0"
val XercesImplVersion = "2.12.2"
val ZioVersion = "2.0.18"
val ZIOInterop = "23.0.0.5"
val ZioLoggingVersion = "2.1.12"
val ZioPreludeVersion = "1.0.0-RC21"
val WeaverVersion = "0.8.1"

Compile / run / fork := true

// some of our tests expect AWS_REGION env. variable to be set, so we need to run tests forked & with that set
// Adding comment to force new build
ThisBuild / Test / envVars := Map("AWS_REGION" -> "us-east-1")
ThisBuild / Test / fork := true

ThisBuild / Test / testForkedParallel := true

concurrentRestrictions :=  {
  Seq(Tags.limit(Tags.ForkedTestGroup, java.lang.Runtime.getRuntime.availableProcessors))
}

// force eager initialization of SLF4j LoggerFactory, to eliminate logging race condition during test run,
// resulting in warnings about replays using SubstitutionLogger
ThisBuild / testOptions += Setup(cl =>
  cl.loadClass("org.slf4j.LoggerFactory").getMethod("getLogger", cl.loadClass("java.lang.String")).invoke(null, "ROOT"))

// child / Test / javaOptions += "-Dsystem.property=value"

// setup WartRemover for both src/test
// TODO - investigate increasing wartremover protection, maybe use Warts.allBut(...), maybe make some errors instead of warnings
// TODO - reduce this list! -- fix problems whenever possible. otherwise use SuppressWarnings annotation to selectively allow these narrowly
val almostAllWarts = Warts.allBut(
  Wart.Any,
  Wart.DefaultArguments,
  Wart.Enumeration,
  Wart.Equals,
  Wart.ImplicitParameter,
  Wart.IterableOps,
  Wart.GlobalExecutionContext,
  Wart.Nothing,
  Wart.Null,
  Wart.Throw,
  Wart.ToString
)

// ScalaMock uses Product in its onCall handler, and its matchers DSL violates NonUnitStatements.
// Seems better to disable these warts for tests only, rather than cluttering tests with warning suppressions or other boilerplate to suppress the warnings.
val wartsAllowedInTests = Set(Wart.NonUnitStatements, Wart.Product)
val testWarts = almostAllWarts.filterNot(wartsAllowedInTests)

// This exclussion is to supress incorrect warning of inferred Any type with ZIO 2 schedule https://github.com/zio/zio/issues/6645
val excludeInferAny = { options: Seq[String] => options.filterNot(Set("-Xlint:infer-any")) }

showCurrentGitBranch
git.useGitDescribe := true

lazy val commonSettings = Seq(
  Compile / compile / wartremoverErrors := almostAllWarts,
  Test / compile / wartremoverErrors := testWarts,
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-core" % LogbackVersion,
    "ch.qos.logback" % "logback-classic" % LogbackVersion,
    "ch.qos.logback.contrib" % "logback-jackson" % LogbackContribVersion,
    "ch.qos.logback.contrib" % "logback-json-classic" % LogbackContribVersion,
    "dev.zio" %% "zio" % ZioVersion,
    "dev.zio" %% "zio-interop-cats" % ZIOInterop,
    "dev.zio" %% "zio-logging-slf4j2" % ZioLoggingVersion,
    "dev.zio" %% "zio-prelude" % ZioPreludeVersion,
    "tf.tofu" %% s"derevo-core" % DerevoVersion,
    "tf.tofu" %% s"derevo-cats" % DerevoVersion,
    "tf.tofu" %% s"derevo-circe-magnolia" % DerevoVersion,
    "org.codehaus.janino" % "janino" % JaninoVersion,
    "org.codehaus.janino" % "commons-compiler" % JaninoVersion,
    "org.http4s" %% s"http4s-ember-client" % Http4sVersion,
    "org.http4s" %% s"http4s-ember-server" % Http4sVersion,
    "org.http4s" %% s"http4s-circe" % Http4sVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % JacksonDatabindVersion,
    "org.typelevel" %% "cats-effect-std" % CatsEffectVersion,
    "dev.zio" %% "zio-test" % ZioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
  ),
  // Need to nail down specific Jackson databind version. Otherwise it wants to use 2.12.3, which causes incompatibility errors.
  // and other versions of various transitive dependencies that we don't necessarily reference directly, but which have vulnerabilities
  // we want to eject the vulnerable transitive dependencies and instead use these newer, safer versions
  dependencyOverrides ++= Seq(
    "commons-collections" % "commons-collections" % CommonsCollectionsVersion,
    "org.apache.commons" % "commons-compress" % CommonsCompressVersion,
    "org.codehaus.groovy" % "groovy" % GroovyVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % JacksonDatabindVersion,
    "org.glassfish.jersey.media" % "jersey-media-jaxb" % JerseyMediaJaxbVersion,
    "com.github.jnr" % "jnr-posix" % JnrPosixVersion,
    "com.amazonaws" % "aws-java-sdk-core" % AwsV1Version,
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % JacksonDataformatCborVersion,
    "com.google.code.gson" % "gson" % GsonVersion,
    "mysql" % "mysql-connector-java" % MySqlConnectorVersion % Runtime,
    "org.slf4j" % "slf4j-ext" % Slf4jVersion,
    "org.yaml" % "snakeyaml" % SnakeYamlVersion,
    "xerces" % "xercesImpl" % XercesImplVersion,
    "io.circe" %% "circe-core" % CirceVersion,
    "org.typelevel" %% "cats-effect" % CatsEffectVersion
  ),
  resolvers ++= Seq(
    "SDP" at "https://artifactory.prod.hulu.com/artifactory/sdp-maven/",
    "Schema registry" at "https://artifactory.prod.hulu.com/artifactory/schemareg-maven/",
    "Offer Management" at "https://artifactory.prod.hulu.com/artifactory/offermgmt-maven/",
    "ecommerce" at "https://artifactory.prod.hulu.com/artifactory/ecommerce-maven/",
    "io-jitpack" at "https://artifactory.prod.hulu.com/artifactory/io-jitpack",
    "Api Registry" at "https://artifactory.us-east-1.bamgrid.net/artifactory/apiregistry-maven",
    "Hulu Api Registry" at "https://artifactory.us-east-1.bamgrid.net/artifactory/hulu-mvn-releases-local-us-east-1"
  ),
  resolvers += Resolver.mavenLocal
)


lazy val root = (project in file("."))
  .aggregate(child, manager, common)

lazy val common = project
  .in(file("common"))
  .settings(
    commonSettings,
    name := "common",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % PostgresqlVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-generic-extras" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion, // sbt-explicit-dependencies claims we don't need this to compile, but actually we do
      "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
      "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % AlpakkaSqsVersion exclude ("com.typesafe.akka", "akka-http_2.13"),
      "com.enragedginger" %% "akka-quartz-scheduler" % QuartzSchedulerVersion,
      "de.heikoseeberger" %% "akka-http-circe" % AkkaCirceVersion,
      "com.typesafe" % "config" % ConfigVersion,
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "com.typesafe.slick" %% "slick-codegen" % SlickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
      "com.github.tminglei" %% "slick-pg" % SlickPgVersion,
      "mysql" % "mysql-connector-java" % MySqlConnectorVersion % Runtime,
      "com.iheart" %% "ficus" % FicusVersion,
      "com.nimbusds" % "nimbus-jose-jwt" % NimbusDsVersion,
      "software.amazon.kinesis" % "amazon-kinesis-client" % AwsKinesisClientVersion,
      "software.amazon.awssdk" % "aws-core" % AwsVersion,
      "software.amazon.awssdk" % "kinesis" % AwsVersion,
      "software.amazon.awssdk" % "kms" % AwsVersion,
      "software.amazon.awssdk" % "regions" % AwsVersion,
      "software.amazon.awssdk" % "s3" % AwsVersion,
      "software.amazon.awssdk" % "sdk-core" % AwsVersion,
      "software.amazon.awssdk" % "sqs" % AwsVersion,
      "software.amazon.awssdk" % "sts" % AwsVersion,
      "software.amazon.awssdk" % "utils" % AwsVersion,
      "org.apache.commons" % "commons-text" % CommonsTextVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
      "org.scala-lang.modules" %% "scala-collection-compat" % ScalaCollectionCompatVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.slf4j" % "slf4j-api" % Slf4jVersion,
      "io.scalaland" %% "chimney" % ChimneyVersion,
      // Anchoring protobuf version for snyk
      "com.google.protobuf" % "protobuf-java" % GoogleProtoBuffVersion,
      // LaunchDarkly
      "com.launchdarkly" % "launchdarkly-java-server-sdk" % LaunchDarklyVersion,
      "org.xerial.snappy" % "snappy-java" % SnappyJavaVersion,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpTestkitVersion % Test,
      "org.scalatest" %% "scalatest" % ScalatestVersion % Test,
      "org.scalacheck" %% "scalacheck" % ScalacheckVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % ScalatestScalacheckVersion % Test,
      "org.scalamock" %% "scalamock" % ScalamockVersion % Test,
      "org.testcontainers" % "testcontainers" % TestcontainersVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-localstack" % TestcontainersScalaVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-scalatest" % TestcontainersScalaVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % TestcontainersScalaVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-mysql" % TestcontainersScalaVersion % Test,
      "com.h2database" % "h2" % H2InMemDbVersion % Test
    ),
    dependencyOverrides ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "org.xerial.snappy" % "snappy-java" % SnappyJavaVersion
    )
  )

lazy val child = project
  .in(file("child"))
  .dependsOn(common % "test->test;compile->compile")
  .settings(
    commonSettings,
    name := "child",
    Compile / mainClass := Some("com.sample.child.Main"),
    dockerBaseImage := "eclipse-temurin:11-jre",
    dockerExposedPorts += 8080,
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % AlpakkaSqsVersion exclude ("com.typesafe.akka", "akka-http_2.13"),
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % AkkaCirceVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
      "com.typesafe" % "config" % ConfigVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-generic-extras" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.gnieh" %% "diffson-circe" % DiffsonVersion,
      "org.gnieh" %% "diffson-core" % DiffsonVersion,
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "org.typelevel" %% "cats-kernel" % CatsVersion,
      "io.scalaland" %% "chimney" % ChimneyVersion,
      "com.nimbusds" % "nimbus-jose-jwt" % NimbusDsVersion,
      "eu.timepit" %% "refined" % RefinedVersion,
      "org.sangria-graphql" %% "sangria" % SangriaVersion,
      "org.sangria-graphql" %% "sangria-circe" % SangriaCirceVersion,
      "org.sangria-graphql" %% "sangria-akka-http-core" % SangriaAkkaHttpVersion,
      "org.sangria-graphql" %% "sangria-akka-http-circe" % SangriaAkkaHttpVersion,
      "org.sangria-graphql" %% "sangria-slowlog" % SangriaSlowLogVersion,
      "com.enragedginger" %% "akka-quartz-scheduler" % QuartzSchedulerVersion,
      "software.amazon.kinesis" % "amazon-kinesis-client" % AwsKinesisClientVersion,
      "software.amazon.awssdk" % "auth" % AwsVersion,
      "software.amazon.awssdk" % "aws-core" % AwsVersion,
      "software.amazon.awssdk" % "http-client-spi" % AwsVersion,
      "software.amazon.awssdk" % "kinesis" % AwsVersion,
      "software.amazon.awssdk" % "kms" % AwsVersion,
      "software.amazon.awssdk" % "netty-nio-client" % AwsVersion,
      "software.amazon.awssdk" % "regions" % AwsVersion,
      "software.amazon.awssdk" % "s3" % AwsVersion,
      "software.amazon.awssdk" % "sdk-core" % AwsVersion,
      "software.amazon.awssdk" % "sqs" % AwsVersion,
      "software.amazon.awssdk" % "sts" % AwsVersion,
      "software.amazon.awssdk" % "utils" % AwsVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonModuleVersion,
      // Datadog java agent and Datadog statsd client for custom metrics
      "com.datadoghq" % "dd-java-agent" % DatadogDdVersion,
      "com.datadoghq" % "dd-trace-api" % DatadogDdVersion,
      "com.datadoghq" % "java-dogstatsd-client" % DatadogJavaClientVersion,
      "com.amazonaws" % "aws-java-sdk-core" % "1.11.251",
      "mysql" % "mysql-connector-java" % MySqlConnectorVersion % Runtime,
      "com.chuusai" %% "shapeless" % ShapelessVersion,
      "org.scala-lang.modules" %% "scala-collection-compat" % ScalaCollectionCompatVersion,
      "org.scala-lang.modules" %% "scala-java8-compat" % ScalaJava8CompatVersion,
      "com.google.guava" % "guava" % GuavaVersion,
      // Anchoring protobuf version for snyk
      "com.google.protobuf" % "protobuf-java" % GoogleProtoBuffVersion,
      "org.slf4j" % "slf4j-api" % Slf4jVersion,
      // TEST
      "org.scalatest" %% "scalatest" % ScalatestVersion % Test,
      "org.scalacheck" %% "scalacheck" % ScalacheckVersion % Test,
      "org.scalamock" %% "scalamock" % ScalamockVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % ScalatestScalacheckVersion % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpTestkitVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
      "org.testcontainers" % "testcontainers" % TestcontainersVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-scalatest" % TestcontainersScalaVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-localstack" % TestcontainersScalaVersion % Test,
      "com.h2database" % "h2" % H2InMemDbVersion % Test,
      "org.mockito" % "mockito-core" % "3.6.28" % Test
    )
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
  buildInfoPackage := "buildutil",
  buildInfoKeys ++= Seq[BuildInfoKey](
    "gitHash" -> git.gitHeadCommit.value.getOrElse("N/A"),
    "gitMessage" -> git.gitHeadMessage.value.getOrElse("N/A"),
    "gitBranch" -> git.gitCurrentBranch.value,
    "gitDirty" -> git.gitUncommittedChanges.value,
    "nameWithVersion" -> s"${name.value} ${version.value}"
  )
)
lazy val manager = project
  .in(file("manager"))
  .dependsOn(common % "test->test;compile->compile")
  .enablePlugins(JavaAppPackaging, DockerPlugin, BuildInfoPlugin)
  .settings(buildInfoSettings)
  .settings(
    commonSettings,
    name := "manager",
    scalacOptions ++= List("-Ymacro-annotations"),
    // TODO: Switch compile run fork between true and false
    Compile / run / fork := true,
    Compile / mainClass := Some("com.sample.manager.Main"),
    Compile / scalacOptions ~= excludeInferAny,
    Test / scalacOptions ~= excludeInferAny,
    dockerBaseImage := "eclipse-temurin:17-jre",
    dockerExposedPorts += 8080,
    testFrameworks ++= List(
      new TestFramework("weaver.framework.CatsEffect"),
      new TestFramework("zio.test.sbt.ZTestFramework")),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      "com.cronutils" % "cron-utils" % cronUtilsVersion,
      "com.github.cb372" %% "cats-retry" % CatsRetryVersion,
      "com.github.cb372" %% "scalacache-caffeine" % ScalaCacheVersion,
      "com.github.cb372" %% "scalacache-cats-effect" % ScalaCacheVersion,
      "io.estatico" %% "newtype" % EstaticoVersion,
      "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % AlpakkaSqsVersion exclude ("com.typesafe.akka", "akka-http_2.13"),
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion,
      "de.heikoseeberger" %% "akka-http-circe" % AkkaCirceVersion,
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
      // Datadog java agent and Datadog statsd client for custom metrics
      "com.datadoghq" % "dd-java-agent" % DatadogDdVersion,
      "com.datadoghq" % "dd-trace-api" % DatadogDdVersion,
      "com.datadoghq" % "java-dogstatsd-client" % DatadogJavaClientVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "com.typesafe.slick" %% "slick-codegen" % SlickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
      "dev.profunktor" %% "http4s-jwt-auth" % Http4sJwtAuthVersion,
      "com.hulu.pepc" % "cfgsvcclient" % "1.0.12",
      "mysql" % "mysql-connector-java" % MySqlConnectorVersion % Runtime,
      "org.slf4j" % "slf4j-api" % Slf4jVersion,

      // TEST
      "org.scalatest" %% "scalatest" % ScalatestVersion % Test,
      "org.scalamock" %% "scalamock" % ScalamockVersion % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpTestkitVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
    ),
    dependencyOverrides ++= Seq(
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-core" % Http4sVersion,
      "org.http4s" %% "http4s-server" % Http4sVersion,
      "co.fs2" %% "fs2-core" % Fs2Version,
      "co.fs2" %% "fs2-io" % Fs2Version,
      "org.typelevel" %% "cats-effect_2.13" % CatsEffectVersion,
      "org.typelevel" %% "cats-effect-kernel_2.13" % CatsEffectVersion,
      "io.circe" %% "circe-core" % CirceVersion
    )
  )





// Custom tasks
def forkJava(
              classpath: Seq[File],
              mainClass: String,
              options: ForkOptions = ForkOptions(),
              arguments: Seq[String] = Seq.empty
            ) = {
  val fullArguments = Seq("-classpath", Path.makeString(classpath), mainClass) ++ arguments
  Fork.java(options, fullArguments)
}

lazy val build = taskKey[Unit]("build app and run tests")
build := {
  val _ = (root / Test / test).value
  (Compile / scalafmtCheck).value
}

