package com.syngenta.flink.transformer.task

import com.syngenta.flink.transformer.functions.ObsTransformerFunction
import com.syngenta.flink.transformer.util.KafkaConnector
import com.typesafe.config.ConfigFactory
//import org.apache.flink.metrics.Meter
import org.apache.flink.streaming.api.scala._

class ObsDataProcessor(config: ObsDataTransformerConfig, kafkaConnector: KafkaConnector) {
  def process(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment


    val obsTransformerFunction = new ObsTransformerFunction(config)


    val stream = env.addSource(kafkaConnector.kafkaConsumer(config.kafkaInputTopic))
      .setParallelism(1).rebalance
      .process(obsTransformerFunction)
      .setParallelism(1)

      


    stream.getSideOutput(config.transformedOutputTag).addSink(kafkaConnector.kafkaProducer(config.kafkaOutputTopic)).setParallelism(1)

    env.execute()

  }

}

object ObsDataProcessor {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("obsconfig.conf")
    val baseConfiguration = new ObsDataTransformerConfig(config)
    val kafkaConnector = new KafkaConnector(baseConfiguration)
    val task = new ObsDataProcessor(baseConfiguration, kafkaConnector)
    task.process()
  }
}