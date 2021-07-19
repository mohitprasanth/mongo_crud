package com.joveo.commons.utils
import com.joveo.commons.dao.MongoConfig
import com.mongodb.BasicDBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.json.{JsonMode, JsonWriterSettings}
import org.json4s.DefaultFormats
import org.json4s.DefaultFormats.dateFormat
import org.json4s.native.{JsonMethods, Serialization}
import org.mongodb.scala.Document
import salat.global.ctx
import salat.{CaseClass, grater}


object BsonUtils{

  implicit val formats = DefaultFormats

  //TO be applied on case classes
  def toBson[T <: CaseClass : Manifest](entity: T): Document = {
    Document(Serialization.write(entity))
  }

  //TO be applied on case classes
  def toEntity[T <: CaseClass : Manifest](document: Document): T = {
    JsonMethods.parse(document.toJson()).extract[T]
  }

}
