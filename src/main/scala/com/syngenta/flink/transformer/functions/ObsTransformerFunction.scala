package com.syngenta.flink.transformer.functions

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.syngenta.flink.transformer.domain.{ComponentType, ContextItems, ObsCollectionModel, ObsData}
import com.syngenta.flink.transformer.task.ObsDataTransformerConfig
import org.apache.commons.text.CaseUtils
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.json.JSONObject
import scala.concurrent.duration._
import com.github.blemale.scaffeine.{Cache, Scaffeine}


import scala.collection.mutable.HashMap
class ObsTransformerFunction(config: ObsDataTransformerConfig) extends ProcessFunction[String, String] {

  lazy val state: ValueState[ObsData] = getRuntimeContext.getState(new ValueStateDescriptor[ObsData]("myState", classOf[ObsData]))


  override def processElement(value: String,
                              ctx: ProcessFunction[String, String]#Context,
                              out: Collector[String]): Unit = {



    ctx.output(config.transformedOutputTag, String.valueOf(Transform(value)))


  }

  def Transform(value: String): String = {
    val valueJsonObject: JSONObject = new JSONObject(value)
    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val obsCollectionModel: ObsCollectionModel = objectMapper.readValue[ObsCollectionModel](value)

    val cache: Cache[String, List[ContextItems]] =
      Scaffeine()
        .recordStats()
        .expireAfterWrite(1.hours)
        .maximumSize(500)
        .build[String, List[ContextItems]]()
    if (valueJsonObject.has("ObsCollection")){
      obsCollectionModel.ObsCollection.map(data => cache.put(data.id, data.contextItems))

      ""}

    else {
      val obsData: ObsData = objectMapper.readValue[ObsData](value)
      val spatialExtentJsnObj: JSONObject = new JSONObject(obsData.spatialExtent)

      val transformMap: HashMap[String, Any] = new HashMap[String, Any]()
      transformMap.put("obsCode", obsData.obsCode)
      obsData.codeComponents.map(components =>
        transformMap.put(CaseUtils.toCamelCase(components.componentType, false, '_'), ComponentType(components.componentCode, components.selector, components.value, components.valueUoM)))
      transformMap.put("contextItems",cache.getIfPresent("f053617f-0072-11ec-b0d5-5d80d3cc2890"))
      transformMap.put("valueUoM", obsData.valueUoM)
      transformMap.put("value", obsData.value)
      transformMap.put("id", obsData.id)
      transformMap.put("parentCollectionRef", List(obsData.parentCollectionRef))
      transformMap.put("integrationAccountRef", obsData.integrationAccountRef)
      transformMap.put("assetRef", obsData.assetRef)
      transformMap.put("xMin", obsData.xMin)
      transformMap.put("xMax", obsData.xMax)
      transformMap.put("yMin", obsData.yMin)
      transformMap.put("yMax", obsData.yMax)
      transformMap.put("phenTime", obsData.phenTime)
      transformMap.put("spatialExtent", Map("type" -> spatialExtentJsnObj.getString("type"), "latCoordinates" -> spatialExtentJsnObj.getJSONArray("coordinates").get(0), "lonCoordinates" -> spatialExtentJsnObj.getJSONArray("coordinates").get(1)))

      val transformed: String = objectMapper.writeValueAsString(transformMap)

      transformed
    }

  }


}
