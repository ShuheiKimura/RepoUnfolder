libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest" % "[1.3,)",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "[3.0.0,)",
  "commons-io" % "commons-io" % "2.4"
)

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature")

assemblySettings
