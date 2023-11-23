package com.sample.manager

import zio._
import zio.logging.backend.SLF4J

object JobScheduler {
  def unsafeZioFork(runtime: Runtime[Any], zio: ZIO[Any, Any, Any]): Unit =
    Unsafe.unsafe { implicit unsafe: Unsafe =>
      val _ = runtime.unsafe.fork(zio)
    }

  def test(): IO[Throwable, Int] = for {
    _ <- ZIO.logInfo("scheduled-job-start: Test")
    response <- ZIO.succeed(10)
    _ <- ZIO.logInfo(s"scheduled-job-start: response ${response.toString}")

  } yield (response)

  val loggingLayer: ZLayer[Any, Nothing, Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j(
    format = zio.logging.LogFormat.colored
  )

  def runJobs(): String = {
    println("Logs should be colored")
    val runtime: Runtime.Scoped[Unit] = {
      Unsafe.unsafe { implicit unsafe: Unsafe => Runtime.unsafe.fromLayer(loggingLayer) }
    }
    unsafeZioFork(runtime, test())
    "Test completed"
  }



}
