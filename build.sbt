name := "SyngentaAssignment"

version := "0.1"

scalaVersion := "2.11.12"
val flinkVersion = "1.14.0"

libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % flinkVersion
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka" % flinkVersion
libraryDependencies += "org.apache.flink" %% "flink-scala" % flinkVersion
libraryDependencies += "org.apache.flink" %% "flink-clients" % flinkVersion
libraryDependencies += "org.apache.flink" % "flink-core" % flinkVersion
libraryDependencies += "org.apache.flink" % "flink-json" % flinkVersion
libraryDependencies += "com.typesafe" % "config" % "1.4.1"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.4"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.2"
libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.9.1"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.3"
libraryDependencies += "net.liftweb" %% "lift-json" % "3.5.0"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.3.0-SNAP3" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.3.0-SNAP3" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.16.46" % Test
libraryDependencies += "org.json" % "json" % "20210307"
libraryDependencies += "org.apache.commons" % "commons-text" % "1.9"
libraryDependencies += "org.apache.flink" % "flink-metrics-dropwizard" % "1.14.0"
libraryDependencies += "com.github.blemale" %% "scaffeine" % "4.1.0"
libraryDependencies += "org.apache.flink" %% "flink-test-utils" % "1.14.0" % Test



 assemblyMergeStrategy in assembly := {
   case PathList("META-INF", xs @ _*) => MergeStrategy.discard
   case x => MergeStrategy.first
}

