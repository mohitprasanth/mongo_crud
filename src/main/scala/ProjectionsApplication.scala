import com.joveo.commons.dao.MongoConfig
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.model.Projections
import org.mongodb.scala.{FindObservable, MongoCollection, MongoDatabase}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


case class IdClass(jobId:Int)

object ProjectionsApplication extends App {
  val database: MongoDatabase = MongoConfig.getDataBaseWithDefaults(fromRegistries(fromProviders(Macros.createCodecProviderIgnoreNone[IdClass]())))
  val jobIdCollection: MongoCollection[IdClass] = database.getCollection("jobs")
  val res: FindObservable[IdClass] = jobIdCollection.find().projection(
    Projections.fields(
      Projections.include("jobId"),
      Projections.excludeId(),
    )
  )
  val rf: Future[Seq[IdClass]] = res.toFuture()
  println(Await.result(rf,50.seconds))
}
