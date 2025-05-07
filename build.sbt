val chisel6Version = "6.6.0"
val chisel3Version = "3.6.1"
val scalaVersionFromChisel = "2.13.12"

lazy val mosaicRoot = Project("mosaicRoot", file("."))

lazy val commonSettings = Seq(
  organization := "cag.amcr.lbl.gov",
  version := "0.1.0",
  scalaVersion := scalaVersionFromChisel,
      scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations",
    ),
)

lazy val firesimDir = file("$FIRESIM_ROOT")
lazy val toolsDir = file("./tools")
lazy val firrtlDir = toolsDir / "firrtl2"
lazy val cdeDir = toolsDir / "cde"

/**
  * It has been a struggle for us to override settings in subprojects.
  * An example would be adding a dependency to rocketchip on midas's targetutils library,
  * or replacing dsptools's maven dependency on chisel with the local chisel project.
  *
  * This function works around this by specifying the project's root at src/ and overriding
  * scalaSource and resourceDirectory.
  */
def freshProject(name: String, dir: File): Project = {
  Project(id = name, base = dir / "src")
    .settings(
      Compile / scalaSource := baseDirectory.value / "main" / "scala",
      Compile / resourceDirectory := baseDirectory.value / "main" / "resources"
    )
}

lazy val chisel6Settings = Seq(
  libraryDependencies ++= Seq("org.chipsalliance" %% "chisel" % chisel6Version),
  addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chisel6Version cross CrossVersion.full)
)

lazy val chisel3Settings = Seq(
  libraryDependencies ++= Seq("edu.berkeley.cs" %% "chisel3" % chisel3Version),
  addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chisel3Version cross CrossVersion.full)
)

lazy val chiselSettings = chisel6Settings ++ Seq(
  libraryDependencies ++= Seq(
    "org.apache.commons" % "commons-lang3" % "3.12.0",
    "org.apache.commons" % "commons-text" % "1.9"
  )
)

lazy val scalaTestSettings =  Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.+" % "test"
  )
)

lazy val firrtl2 = freshProject("firrtl2", firrtlDir)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(Antlr4Plugin)
  .settings(commonSettings)
  .settings(
    sourceDirectory := firrtlDir / "src",
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-language:existentials",
      "-language:implicitConversions"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % "test",
      "com.github.scopt" %% "scopt" % "4.1.0",
      "org.json4s" %% "json4s-native" % "4.1.0-M4",
      "org.apache.commons" % "commons-text" % "1.10.0",
      "com.lihaoyi" %% "os-lib" % "0.8.1",
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"),
    Antlr4 / antlr4GenVisitor := true,
    Antlr4 / antlr4GenListener := true,
    Antlr4 / antlr4PackageName := Option("firrtl2.antlr"),
    Antlr4 / antlr4Version := "4.9.3",
    Antlr4 / javaSource := (Compile / sourceManaged).value,
    buildInfoPackage := "firrtl2",
    buildInfoUsePackageAsPath := true,
    buildInfoKeys := Seq[BuildInfoKey](buildInfoPackage, version, scalaVersion, sbtVersion)
  )

lazy val firrtl2_bridge = freshProject("firrtl2_bridge", firrtlDir / "bridge")
  .dependsOn(firrtl2)
  .settings(commonSettings)
  .settings(chiselSettings)

lazy val cde = (project in cdeDir)
  .settings(commonSettings)
  .settings(Compile / scalaSource := baseDirectory.value / "cde/src/chipsalliance/rocketchip")

// Contains annotations & firrtl passes you may wish to use in rocket-chip without
// introducing a circular dependency between RC and MIDAS.
// Minimal in scope (should only depend on Chisel/Firrtl that is
// cross-compilable between FireSim Chisel 3.* and MoSAIC Chisel 6+)
lazy val midas_target_utils = (project in firesimDir / "sim/midas/targetutils")
  .settings(commonSettings)
  .settings(chiselSettings)

// Provides API for bridges to be created in the target.
// Includes target-side of FireSim-provided bridges and their interfaces that are shared
// between FireSim and the target. Minimal in scope (should only depend on Chisel/Firrtl that is
// cross-compilable between FireSim Chisel 3.* and MoSAIC Chisel 6+)
lazy val firesim_lib = (project in firesimDir / "sim/firesim-lib")
  .dependsOn(midas_target_utils)
  .settings(commonSettings)
  .settings(chiselSettings)
  .settings(scalaTestSettings)

lazy val mosaic = Project("mosaic", file("."))
  .dependsOn(cde, firrtl2_bridge, midas_target_utils, firesim_lib)
  .settings(commonSettings)
  .settings(chiselSettings)
