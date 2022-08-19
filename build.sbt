ThisBuild / resolvers ++= Seq(
    "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
    Resolver.mavenLocal
)

name := "scalapb-serialization"

version := "0.1-SNAPSHOT"

organization := "com.eligolin"

ThisBuild / scalaVersion := "2.12.12"

val flinkVersion = "1.14.5"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-clients" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" % "flink-file-sink-common" % "1.14.5")


lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies,
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-validate-core" % scalapb.validate.compiler.BuildInfo.version % "protobuf"
    ),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb",
      scalapb.validate.gen() -> (Compile / sourceManaged).value / "scalapb"
    ),
    assembly / mainClass := Some("com.eligolin.Job"),

    // make run command include the provided dependencies
    Compile / run := Defaults.runTask(Compile / fullClasspath,
        Compile / run / mainClass,
        Compile / run / runner
      ).evaluated,
    // stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
    Compile / run / fork := true,
    Global / cancelable := true ,

    // exclude Scala library from assembly
    assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false)
  )
