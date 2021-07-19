import com.joveo.commons.dao.MongoConfig
import com.joveo.commons.model.Job
import com.joveo.commons.utils.TransactionalUtils
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}
//need to have dbs and collections before hand
object TransactionalApplication extends App {
  val clientSession = TransactionalUtils.startSession()
  val jobsCollection: MongoCollection[Job] = MongoConfig.database.getCollection("jobs")

  val res: Try[Unit] = Try {
      for {jobId <- (1 to 10)} {
        println("inserting job")
        val newJob = Job(jobId, "SSE", "HYD")
        val jf = jobsCollection.insertOne(clientSession, newJob).toFuture()
        Await.result(jf,50.seconds)
      }

      for {jobId <- (1 to 10).filter(x => x % 2 == 0)} {
        println("Updating the job")
        val uf = jobsCollection.updateOne(clientSession, equal("jobId", jobId), Updates.set("city", "Banglore")).toFuture()
        Await.result(uf,50.seconds)
      }

      for {jobId <- (1 to 5).filter(x => x % 2 == 1)} {
        println("Deleting the job")
        val df = jobsCollection.deleteOne(clientSession, equal("jobId", jobId)).toFuture()
        Await.result(df,50.seconds)
      }
    }

    res match {
      case Success(_) => TransactionalUtils.commit(clientSession)
      case Failure(_) => TransactionalUtils.abort(clientSession)
    }

  Thread.sleep(5000)
}
