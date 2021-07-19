package com.joveo.commons.dao

import org.bson.{BsonDocument, BsonInt64, BsonTimestamp}

case class Activity(
  fullDocument: Option[BsonDocument],
  documentKey: String,
  clusterTime: BsonTimestamp,
  operationType: String,
  txnNumber: Option[BsonInt64],
  lsid: Option[BsonDocument],
  updateDescription: Option[UpdateDetails],
  userName: String)


case class UpdateDetails(
                        removedFields: List[String],
                        updatedFields: BsonDocument)