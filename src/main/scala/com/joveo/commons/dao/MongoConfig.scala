package com.joveo.commons.dao

import com.joveo.commons.model.{Job, JobV2}
import com.joveo.commons.watcher.DocumentWatcher
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.{ChangeStreamObservable, Document, MongoClient, MongoDatabase, ReadConcern, ReadPreference, TransactionOptions, WriteConcern}

object MongoConfig{
  val CUSTOM_CODEC: CodecRegistry = fromProviders(Macros.createCodecProviderIgnoreNone[Job](), Macros.createCodecProviderIgnoreNone[Activity](), Macros.createCodecProviderIgnoreNone[UpdateDetails]())
  val codecRegistry: CodecRegistry = fromRegistries(CUSTOM_CODEC,MongoClient.DEFAULT_CODEC_REGISTRY)

  val client: MongoClient = MongoClient("mongodb://127.0.0.1:27017")
  client.getDatabase("jobs").drop()

  val database: MongoDatabase = client.getDatabase("jobs").withCodecRegistry(codecRegistry)

  val dbObserver: ChangeStreamObservable[Document] = database.watch()
  dbObserver.subscribe(new DocumentWatcher)

  def getDataBaseWithDefaults(customCodecRegistry : CodecRegistry) = {
    client.getDatabase("jobs").withCodecRegistry(fromRegistries(codecRegistry,customCodecRegistry))
  }

  val transactionOptions:TransactionOptions = TransactionOptions.builder()
    .readPreference(ReadPreference.primary())
    .readConcern(ReadConcern.LOCAL)
    .writeConcern(WriteConcern.ACKNOWLEDGED)
    .build()
}
