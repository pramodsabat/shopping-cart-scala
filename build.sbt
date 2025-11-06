ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.7.2"
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

val version = new {
  val zio = "2.0.18"
  val logback = "1.5.19"
  val sttp = "4.0.12"
  val tapir = "1.11.48"
  val quill = "4.8.6"
}

lazy val root = (project in file(".")).settings(
  name := "shopping-cart-test-main",

  // Other dependent library
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % version.logback,
    "com.softwaremill.sttp.client4" %% "zio-json" % version.sttp,
    "com.softwaremill.sttp.client4" %% "zio" % version.sttp,
    "io.getquill" %% "quill-jdbc-zio" % version.quill,
    "com.softwaremill.sttp.tapir" %% "tapir-core" % version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-netty-server-sync" % version.tapir,
  ),

  // Test scope
  libraryDependencies ++= Seq(
    "dev.zio" %% "zio-test" % version.zio % Test,
    "dev.zio" %% "zio-test-sbt" % version.zio % Test
  ),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
)