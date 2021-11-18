package com.syngenta.flink.transformer.util

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

case class Metrics(metrics: ConcurrentHashMap[String, AtomicLong]) {
  def incCounter(metric: String): Unit = {
    metrics.get(metric).getAndIncrement()
  }

  def getAndReset(metric: String): Long = metrics.get(metric).getAndSet(0L)

  def get(metric: String): Long = metrics.get(metric).get()

  def reset(metric: String): Unit = metrics.get(metric).set(0L)
}

trait JobMetrics {
  def registerMetrics(metrics: List[String]): Metrics = {
    val metricMap = new ConcurrentHashMap[String, AtomicLong]()
    metrics.map { metric => metricMap.put(metric, new AtomicLong(0L)) }

    Metrics(metricMap)
  }
}

