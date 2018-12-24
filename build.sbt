// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `usage-demo` =
  project
    .in(file("usage-demo"))
    .dependsOn(`protocol`)
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
    .settings(settings)
    .settings(resolverSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.monix,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

lazy val `protocol` =
  project
    .in(file("protocol"))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
    .settings(settings)
    .settings(scalaPbSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.grpcNetty,
        library.scalaPbRuntime,
        library.scalaPbRuntimeGrpc,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

lazy val `grpc-playground` =
  project
    .in(file("."))
    .aggregate(`usage-demo`, `protocol`)

// *****************************************************************************
// Library dependencies
// *****************************************************************************

import com.trueaccord.scalapb.compiler.{ Version => VersionPb }
lazy val library =
  new {
    object Version {
      val helloGrpcProto = "1.0.0"
      val monix          = "2.3.0"
      val scalaCheck     = "1.13.5"
      val scalaTest      = "3.0.4"
    }
    val grpcNetty          = "io.grpc"                 % "grpc-netty"                  % VersionPb.grpcJavaVersion
    val monix              = "io.monix"               %% "monix"                       % Version.monix
    val scalaCheck         = "org.scalacheck"         %% "scalacheck"                  % Version.scalaCheck
    val scalaPbRuntime     = "com.trueaccord.scalapb" %% "scalapb-runtime"             % VersionPb.scalapbVersion % "protobuf"
    val scalaPbRuntimeGrpc = "com.trueaccord.scalapb" %% "scalapb-runtime-grpc"        % VersionPb.scalapbVersion
    val scalaTest          = "org.scalatest"          %% "scalatest"                   % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
commonSettings ++
gitSettings ++
scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.4",
    organization := "io.teranga",
    organizationName := "Abdoulaye Diallo",
    startYear := Some(2017),
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ypartial-unification",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-encoding", "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val resolverSettings =
  Seq(
    resolvers += Resolver.bintrayRepo("teranga", "maven")
  )

lazy val scalaPbSettings = Seq(
  PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.3.0"
  )
