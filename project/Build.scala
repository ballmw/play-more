import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "play-more"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
      "org.fusesource.scalate" % "scalate-core" % "1.5.3"
    )

    val secureSocial = PlayProject(
      appName + "-securesocial", appVersion, mainLang = SCALA, path = file("modules/securesocial")
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
    )

}
