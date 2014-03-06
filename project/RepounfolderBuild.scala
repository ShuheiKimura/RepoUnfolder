import sbt._
import sbt.Keys._

object RepounfolderBuild extends Build {

  lazy val repounfolder = Project(
    id = "repounfolder",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "RepoUnfolder",
      organization := "sdl",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.3"
      // add other settings here
    )
  )
}
