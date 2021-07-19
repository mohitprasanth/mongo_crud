import com.joveo.commons.dao.MongoConfig
import com.joveo.commons.model.JobV2
import com.joveo.commons.utils.BsonUtils
import org.json4s.DefaultFormats
import org.mongodb.scala.{Completed, Document, FindObservable, MongoCollection}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}



object VersioningApplication extends App{

  implicit val formats: DefaultFormats = DefaultFormats

  val jobsCollection: MongoCollection[Document] = MongoConfig.database.getCollection("jobs")

//  insertJob(JobV2(99120,"PLK","GS","AP"),schemaVersion = 3)
  val res: FindObservable[Document] = jobsCollection.find()
  val jobsListFuture: Future[Seq[JobV2]] = res.toFuture().map(x => x.map(y => JobVersionFactory.generateObject(y)))
  println(Await.result(jobsListFuture,50.seconds))
  Thread.sleep(5000)

  def insertJob(job:JobV2,schemaVersion:Int=0) ={
    val jobBson: Document = BsonUtils.toBson(job) ++  Document("schema_version" -> schemaVersion)
    val jf: Future[Completed] = jobsCollection.insertOne(jobBson).toFuture()
    Await.result(jf,2.minute)
  }

}

object JobVersionFactory extends VersionFactory[JobV2] {
  def generateObject(doc: Document): JobV2 = {
    if(doc.contains("schema_version")){
      val schema_version = doc.get("schema_version").get.asNumber().intValue()
      schema_version match {
        case 3 => {
          BsonUtils.toEntity[JobV2](doc)
        }
        case _ => getJob(doc).copy(state = null)
      }
    }else{
      getJob(doc)
    }
  }

  private def getJob(doc: Document) = {
    val jobId: Int = doc.get("jobId").get.asNumber().intValue()
    val city: String = doc.get("city").get.asString().getValue
    val title: String = doc.get("title").get.asString().getValue
    var state: String = null
    if(doc.contains("state")){
      state = doc.get("state").get.asString().getValue
    }
    JobV2(jobId,city,title,state)
  }
}

trait VersionFactory[T]{
  def generateObject(doc : Document) : T
}