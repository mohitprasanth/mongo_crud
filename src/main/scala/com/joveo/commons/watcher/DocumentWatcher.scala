package com.joveo.commons.watcher
import com.joveo.commons.common.Constants
import com.joveo.commons.common.Constants.ACTIVITY
import com.joveo.commons.dao.{Activity, MongoConfig, UpdateDetails}
import org.mongodb.scala.model.changestream.ChangeStreamDocument
import org.mongodb.scala.{Document, MongoCollection, Observer}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

case class DocumentWatcher() extends Observer[ChangeStreamDocument[Document]] {
  override def onNext(result: ChangeStreamDocument[Document]): Unit = {

    val dbName = result.getNamespace.getDatabaseName
    val collectionName = result.getNamespace.getCollectionName
    if(collectionName.startsWith(ACTIVITY) || dbName.startsWith(ACTIVITY))
      return
    val activity_collectionName = Constants.ACTIVITY + collectionName

    val database = MongoConfig.client.getDatabase(dbName).withCodecRegistry(MongoConfig.codecRegistry)
    val collection: MongoCollection[Activity] = database.getCollection(activity_collectionName)

    val fullDocument = result.getFullDocument match {
      case null => None
      case d => Option(d.toBsonDocument)
    }

    val updateDescription = result.getUpdateDescription match {
      case null => None
      case ud => Option(UpdateDetails(ud.getRemovedFields.asScala.toList,ud.getUpdatedFields))
    }

    val activity: Activity = Activity(fullDocument = fullDocument,
      documentKey = result.getDocumentKey.get("_id").asObjectId().getValue.toString,
      clusterTime = result.getClusterTime,
      operationType = result.getOperationType.getValue,
      txnNumber = Option(result.getTxnNumber),
      lsid = Option(result.getLsid),
      updateDescription = updateDescription,
      userName = "Mohit")

    val activityFuture = collection.insertOne(activity).toFuture()
    Await.result(activityFuture,5.seconds)
  }

  override def onError(e: Throwable): Unit = {
    println("There is some exception" + e)
  }

  override def onComplete(): Unit = {
    print("This is completed")
  }
}