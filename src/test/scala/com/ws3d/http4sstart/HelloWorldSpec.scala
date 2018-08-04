package com.ws3d.http4sstart

import com.ws3d.http4sstart.entities.DogWithId
import com.ws3d.http4sstart.entities.DogWithId._

import cats.effect.IO
import io.circe.syntax._
import io.circe.Json
import org.http4s.circe._
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scala.collection.mutable.ListBuffer

class HelloWorldSpec extends Specification {

  // CREATE
  "Post Dog" >> {
    "return 201" >> {
      postDogReturns201
    }
    "return an ID" >> {
      postDogReturnsId
    }
  }

  // READ
  "Get Dog" >> {
    "return 200" >> {
      getDogReturns200
    }
    "return a dog" >> {
      getDogReturnsDog
    }
  }

  // UPDATE
  "Put Dog" >> {
    "return 200" >> {
      putDogReturns200
    }
    "update dog" >> {
      putDogUpdatesDog
    }
  }

  // DELETE
  "Delete Dog" >> {
    "return 200" >> {
      deleteDogReturns200
    }
    "remove dog" >> {
      deleteDogRemovesDog
    }
  }

  val uuidRegex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".r

  val dogWithId = DogWithId("abc-123", "Burek")
  val dogRepo = DogRepository[IO](ListBuffer(dogWithId))

  def testService() = new DogService[IO](dogRepo).service

  private[this] val retGetDog: Response[IO] = {
    val getDog = Request[IO](Method.GET, Uri.uri("/dogs/abc-123"))
    testService().orNotFound(getDog).unsafeRunSync()
  }

  private[this] def getDogReturns200: MatchResult[Status] =
    retGetDog.status must beEqualTo(Status.Ok)

  private[this] def getDogReturnsDog: MatchResult[String] = {
    val dog = Json.fromString(s"""{"id":"abc-123","name":"Burek"}""")
    retGetDog.as[String].unsafeRunSync() must beEqualTo(dog.asString.get)
  }

  val newDogWithId = DogWithId("def-456", "Szarik")

  private[this] val retPostDog: Response[IO] = {
    Request[IO](Method.POST, Uri.uri("/dogs")).withBody(dogWithId.asJson).flatMap {postDog =>
      testService().orNotFound(postDog)
    }.unsafeRunSync()
  }

  private[this] def postDogReturns201: MatchResult[Status] =
    retPostDog.status must beEqualTo(Status.Created)

  private[this] def postDogReturnsId: MatchResult[String] = {
    val id = retPostDog.as[String].unsafeRunSync()
    id must beMatching(uuidRegex)
  }

  val dogToUpdate = DogWithId("abc-123", "Scooby")

  private[this] val retPutDog: Response[IO] = {
    Request[IO](Method.PUT, Uri.uri("/dogs")).withBody(dogToUpdate.asJson).flatMap {putDog =>
      testService().orNotFound(putDog)
    }.unsafeRunSync()
  }

  private[this] val retGetUpdatedDog: Response[IO] = {
    val getUpdatedDog = Request[IO](Method.GET, Uri.uri("/dogs/abc-123"))
    testService().orNotFound(getUpdatedDog).unsafeRunSync()
  }

  private[this] def putDogReturns200: MatchResult[Status] =
    retPutDog.status must beEqualTo(Status.Ok)

  private[this] def putDogUpdatesDog: MatchResult[Json] = {
    retGetUpdatedDog.as[Json].unsafeRunSync() must beEqualTo(dogToUpdate.asJson)
  }

  private[this] val retDeleteDog: Response[IO] = {
    val deleteDog = Request[IO](Method.DELETE, Uri.uri("/dogs/abc-123"))
    testService().orNotFound(deleteDog).unsafeRunSync()
  }

  private[this] val retGetDeletedDog: Response[IO] = {
    val getDog = Request[IO](Method.GET, Uri.uri("/dogs/abc-123"))
    testService().orNotFound(getDog).unsafeRunSync()
  }

  private[this] def deleteDogReturns200: MatchResult[Status] =
    retDeleteDog.status must beEqualTo(Status.Ok)

  private[this] def deleteDogRemovesDog: MatchResult[Status] =
    retGetDeletedDog.status must beEqualTo(Status.NotFound)
}
