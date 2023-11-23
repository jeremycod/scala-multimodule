package com.sample.manager

object Main {
  def main(args: Array[String]): Unit = {
    println("Test ZIO logging issue!")
    val result = JobScheduler.runJobs()
    println(result)
  }
}