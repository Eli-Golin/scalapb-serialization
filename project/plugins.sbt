addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")
// For ScalaPB 0.11.x:
libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "compilerplugin"           % "0.11.11",
  "com.thesamet.scalapb" %% "scalapb-validate-codegen" % "0.3.1"
)