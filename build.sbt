name := "spark-2-streaming-nosuchmethod-arrowassoc"

version := "1.1"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val sparkV = "2.0.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkV % "provided",
    "org.apache.spark" %% "spark-streaming" % sparkV % "provided"
  )
}

//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true)