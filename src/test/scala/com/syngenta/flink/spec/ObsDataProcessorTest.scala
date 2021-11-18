
package com.syngenta.flink.spec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.syngenta.flink.data.TestData
import com.syngenta.flink.st.{MetricsReporter, TestSpec}
import com.syngenta.flink.transformer.domain.{ContextItems, ObsData}
import com.syngenta.flink.transformer.functions.ObsTransformerFunction
import com.syngenta.flink.transformer.task.{ObsDataProcessor, ObsDataTransformerConfig}
import com.syngenta.flink.transformer.util.KafkaConnector
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.mockito.Mockito

import java.io.ByteArrayOutputStream
import java.util
import scala.concurrent.duration._
import org.apache.flink.test.util.MiniClusterWithClientResource

class ObsDataProcessorTest extends TestSpec {

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  val config: Config = ConfigFactory.load("obsconfig.conf")
  val baseConfiguration = new ObsDataTransformerConfig(config)

  val mockKafkaConnector: KafkaConnector = mock[KafkaConnector](Mockito.withSettings().serializable())

  MetricsReporter.gaugeMetrics.clear()


  when(mockKafkaConnector.kafkaConsumer(baseConfiguration.kafkaInputTopic)) thenReturn (new FlinkEventSource)

  when(mockKafkaConnector.kafkaProducer(baseConfiguration.kafkaOutputTopic)) thenReturn (new ObsDataSink)


  " Obs Data Processor " should "process the com.syngenta.flink.data" in {

    val task = new ObsDataProcessor(baseConfiguration, mockKafkaConnector)
    task.process()

    ObsDataSink.values.size() should be(2)
  }
  "Obs Data Processor" should "transform obs com.syngenta.flink.data " in {
    val contextItems = ContextItems("SYN_SYSTEM", "PESSL")
    val cache: Cache[String, List[ContextItems]] =
      Scaffeine()
        .recordStats()
        .expireAfterWrite(1.hour)
        .maximumSize(500)
        .build[String, List[ContextItems]]()

    cache.put("51728580-fac1-11eb-b1d2-df3668d7bed5", List(contextItems))
    val obsDataTransformerConfig = new ObsDataTransformerConfig(config)
    val obsTransformer = new ObsTransformerFunction(obsDataTransformerConfig, cache)
    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)

    val obsData1: ObsData = objectMapper.readValue[ObsData](TestData.Data_1)

    obsTransformer.transform(obsData1, objectMapper) should be(TestData.transformed_Data1)

  }


  "Obs Data Processor" should "process the events" in  {

    MetricsReporter.gaugeMetrics(s"{baseConfiguration.ObsDataTransformationJob}.{baseConfiguration.transformedEventMetricCount}").getValue() should be(2)
  }

}

class FlinkEventSource extends SourceFunction[String] {
  override def run(ctx: SourceFunction.SourceContext[String]): Unit = {


    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)

    val data1: ObsData = objectMapper.readValue[ObsData](TestData.Data_1)
    val testData1 = new ByteArrayOutputStream()
    objectMapper.writeValue(testData1, data1)

    ctx.collect(testData1.toString)

    val data2: ObsData = objectMapper.readValue[ObsData](TestData.Data_2)
    val testData2 = new ByteArrayOutputStream()
    objectMapper.writeValue(testData2, data2)

    ctx.collect(testData2.toString)


  }

  override def cancel(): Unit = {}

}

class ObsDataSink extends SinkFunction[String] {
  override def invoke(value: String, context: SinkFunction.Context): Unit = {
    synchronized {
      ObsDataSink.values.add(value)
    }
  }
}

object ObsDataSink {
  val values: util.List[String] = new util.ArrayList()
}

