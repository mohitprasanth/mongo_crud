import com.joveo.commons.dao.MongoConfig
import com.joveo.commons.model.Job
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Sorts._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

//https://github.com/mongodb/mongo-scala-driver/blob/master/docs/reference/content/builders/sorts.md
object SortingApplication extends App {
  val jobsCollection: MongoCollection[Job] = MongoConfig.database.getCollection("jobs")
  val res: Future[Seq[Job]] = jobsCollection.find().sort(orderBy(descending("jobId"),ascending("city"))).toFuture()
  println(Await.result(res,5.seconds))
}
