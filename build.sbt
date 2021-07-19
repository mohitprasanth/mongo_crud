name := "mongo_crud_poc"

version := "0.1"

scalaVersion := "2.12.13"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.8"
libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.0"
libraryDependencies += "org.json4s" %% "json4s-ext" % "4.0.0"

//remove these once BsonUtils changes are done.
libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1" exclude ("org.mongodb","mongo-java-driver")
libraryDependencies += "com.github.salat" %% "salat" % "1.11.2" excludeAll(ExclusionRule(organization = "org.mongodb" ), ExclusionRule(organization = "org.json4s"))



//libraryDependencies += "com.github.salat" %% "salat" % "1.11.2" excludeAll(ExclusionRule(organization = "org.mongodb" ))
