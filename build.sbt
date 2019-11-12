name := "kafka-rest-proxy-wrapper"

version := "0.1"

scalaVersion := "2.12.1"

organization := "com.bounce"

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq("org.apache.kafka" % "kafka-clients" % "2.2.1",
  "com.twitter" %% "bijection-avro" % "0.9.6",
  "org.apache.avro" % "avro" % "1.9.1",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "com.typesafe" % "config" % "1.4.0"
)