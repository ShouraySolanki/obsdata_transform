package com.syngenta.flink.transformer.task

import com.typesafe.config.Config
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.kafka.clients.consumer.ConsumerConfig

import java.util.Properties

class ObsDataTransformerConfig(val config: Config) extends Serializable {

  val kafkaInputTopic: String = config.getString("kafka.inputtopic")
  val kafkaOutputTopic: String = config.getString("kafka.outputtopic")
  val transformedOutputTag = OutputTag[String]("transformed-output")
  val kafkaProducerBrokerServers: String = config.getString("kafka.producer.broker-servers")
  val kafkaConsumerBrokerServers: String = config.getString("kafka.consumer.broker-servers")
  val kafkaProducerZookeeperServers: String = config.getString("kafka.producer.zookeeper-servers")
  val kafkaConsumerZookeeperServers: String = config.getString("kafka.consumer.zookeeper-servers")
  val expiredPeriod: Int = config.getInt("cache.expired.period")
  val maxSize: Int = config.getInt("cache.keys.maxsize")
  val transformedEventMetricCount = "transformed-event-success-count"
  val cacheEventMetricCount = "cache-event-success-count"
  val jobName:String = "ObsDataTransformationJob"


  def flinkKafkaConsumerProperties: Properties = {

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", kafkaConsumerBrokerServers)
    properties.setProperty("zookeeper.connect", kafkaConsumerZookeeperServers)
    properties.setProperty("group.id", "consumerGroup")
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    properties
  }

  def flinkKafkaProducerProperties: Properties = {

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", kafkaProducerBrokerServers)
    properties.setProperty("zookeeper.connect", kafkaProducerZookeeperServers)
    properties.setProperty("group.id", "consumerGroup")
    properties
  }


}
