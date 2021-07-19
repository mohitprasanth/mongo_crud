import com.joveo.commons.dao.MongoConfig
import com.joveo.commons.model.Job
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{Filters, Updates}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

object AuditApplication extends App{
  val jobsCollection: MongoCollection[Job] = MongoConfig.database.getCollection("jobs")

  for {jobId <- (1 to 10)}{
    println("inserting job")
    val newJob = Job(jobId,"SSE","HYD")
    val jf = jobsCollection.insertOne(newJob).toFuture()
    Await.result(jf,50.seconds)
  }

  for {jobId <- (1 to 10).filter(x=>x%2==0)}{
    println("Updating the job")
    val editJob = Job(jobId,"SSE","BANG")
    val uf = jobsCollection.updateOne(equal("jobId", jobId), Updates.set("city", "Banglore")).toFuture()
    Await.result(uf,50.seconds)
  }

  for {jobId <- (1 to 5).filter(x=>x%2==1)}{
    println("Deleting the job")
    val df = jobsCollection.deleteOne(equal("jobId",jobId)).toFuture()
    Await.result(df,50.seconds)
  }

  Thread.sleep(5000)
}