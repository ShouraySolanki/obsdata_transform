package com.syngenta.flink.st

import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.flink.configuration.Configuration


class TestSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar{

  def testConfiguration(): Configuration = {
    val config = new Configuration()
    config.setString("metrics.reporter", "job_metrics_reporter")
    config.setString("metrics.reporter.job_metrics_reporter.class", classOf[MetricsReporter].getName)

    config
  }

}
