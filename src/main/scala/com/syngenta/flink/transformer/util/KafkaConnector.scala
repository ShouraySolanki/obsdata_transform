package com.syngenta.flink.transformer.util

import com.syngenta.flink.transformer.task.ObsDataTransformerConfig
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

class KafkaConnector(config: ObsDataTransformerConfig) {

  def kafkaProducer(topic: String): SinkFunction[String] = {
    new FlinkKafkaProducer[String](topic, new SimpleStringSchema(), config.flinkKafkaProducerProperties)
  }

  def kafkaConsumer(topic: String): SourceFunction[String] = {
    new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), config.flinkKafkaConsumerProperties).setStartFromEarliest()
  }


}
