package com.ws3d.http4sstart

import java.util.UUID

import cats.effect.{Effect, IO}
import cats.implicits._
import com.ws3d.http4sstart.entities.{Dog, DogWithId}

import scala.collection.mutable.ListBuffer

final case class DogRepository[F[_]](private val dogs: ListBuffer[DogWithId])(implicit e: Effect[F]) {
  val makeId: F[String] = e.delay { UUID.randomUUID().toString }

  // CREATE
  def addDog(dog: Dog) =
    for {
      uuid <- makeId
      _ <- e.delay { dogs += DogWithId(uuid, dog.name)}
    } yield uuid

  // READ
  def getDog(id: String) =
    e.delay {
      dogs.find(_.id == id)
    }

  // UPDATE
  def updateDog(dogWithId: DogWithId) =
    e.delay {
      dogs.find(_.id == dogWithId.id).foreach(dog => dogs -= dog)
      dogs += dogWithId
      ()
    }

  // DELETE
  def deleteDog(dogId: String) =
    e.delay {
      dogs.find(_.id == dogId).foreach(dog => dogs -= dog)
    }
}

object DogRepository {
  def empty[F[_]](implicit m: Effect[F]): IO[DogRepository[F]] = IO { new DogRepository[F](ListBuffer()) }
}
