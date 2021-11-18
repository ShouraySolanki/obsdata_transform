package com.syngenta.flink.transformer.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName


case class ObsData(obsCode: String,
                   @(SerializedName@scala.annotation.meta.field)("agg_time_window") codeComponents: List[CodeComponents],
                   valueUoM: String,
                   value: String,
                   id: String,
                   parentCollectionRef: String,
                   integrationAccountRef: String,
                   assetRef: String,
                   xMin: Double,
                   xMax: Double,
                   yMin: Double,
                   yMax: Double,
                   phenTime: String,
                   spatialExtent: String
                  )

case class CodeComponents(componentCode: String,
                          componentType: String,
                          selector: String,
                          value: Option[String] = None,
                          valueUoM: Option[String] = None
                         )

case class ComponentType(@JsonProperty("componentCode") componentCode: String,
                         @JsonProperty("selector") selector: String,
                         @JsonProperty("value") value: Option[String] = None,
                         @JsonProperty("valueUoM") valueUoM: Option[String] = None
                        )
case class ObsCollectionModel(ObsCollection: List[observations])

case class observations(
                         id: String,
                         contextItems: List[ContextItems]
                       )
case class ContextItems(
                         code: String,
                         value: String
                       )

