name := "api-akka"

version := "0.1"

scalaVersion := "2.12.6"

enablePlugins(AkkaGrpcPlugin)
// ALPN agent
enablePlugins(JavaAgent)
javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime;test"

libraryDependencies ++= Seq(
  "com.google.api.grpc" % "proto-google-common-protos" % "1.12.0" % "protobuf",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

akkaGrpcGeneratedSources := Seq(AkkaGrpc.Server)
