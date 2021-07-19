package com.joveo.commons.utils

import com.joveo.commons.dao.MongoConfig
import org.mongodb.scala.{ClientSession, Completed, MongoClient, MongoException, Observable, ScalaClientSession, SingleObservable}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

object TransactionalUtils {

  def startSession(client:MongoClient  = MongoConfig.client): ClientSession = {
    val clientSession = Await.result(client.startSession().head(), 2.minutes)
    clientSession.startTransaction(MongoConfig.transactionOptions)
    clientSession
  }

  def commit(session: ClientSession):Future[Completed] = {
    val commitTransactionObservable: SingleObservable[Completed] = session.commitTransaction()
    commitTransactionObservable.head()
  }

  def commitWithRetry(session: ClientSession):Future[Completed] = {
    val commitTransactionObservable: SingleObservable[Completed] = session.commitTransaction()
    val commitAndRetryObservable: SingleObservable[Completed] = commitAndRetry(commitTransactionObservable)
    runTransactionAndRetry(commitAndRetryObservable).head()
  }

  def abort(session:ClientSession):Future[Completed] = {
    session.abortTransaction().head()
  }

  private def commitAndRetry(observable: SingleObservable[Completed]): SingleObservable[Completed] = {
    observable.recoverWith({
      case e: MongoException if e.hasErrorLabel(MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL) => {
        println("UnknownTransactionCommitResult, retrying commit operation ...")
        commitAndRetry(observable)
      }
      case e: Exception => {
        println(s"Exception during commit ...: $e")
        throw e
      }
    })
  }

  private def runTransactionAndRetry(observable: SingleObservable[Completed]): SingleObservable[Completed] = {
    observable.recoverWith({
      case e: MongoException if e.hasErrorLabel(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL) => {
        println("TransientTransactionError, aborting transaction and retrying ...")
        runTransactionAndRetry(observable)
      }
    })
  }
}
