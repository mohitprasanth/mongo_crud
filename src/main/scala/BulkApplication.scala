import com.joveo.commons.dao.MongoConfig
import com.joveo.commons.model.Job
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.InsertOneModel

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object BulkApplication extends App {
  val jobsCollection: MongoCollection[Job] = MongoConfig.database.getCollection("jobs")

  val res = jobsCollection.bulkWrite(
  List(InsertOneModel[Job](Job(134,"SSE","HYD")),
  InsertOneModel[Job](Job(-2,"SSE","HYD")),
  InsertOneModel[Job](Job(-3,"SSE","HYD"))
  )).toFuture()
  Await.result(res,50.seconds)

  Thread.sleep(5000000)
}
