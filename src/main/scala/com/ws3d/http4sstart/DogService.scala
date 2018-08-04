package com.ws3d.http4sstart

import cats.effect.Effect
import cats.implicits._
import com.ws3d.http4sstart.entities.{Dog, DogWithId}
import com.ws3d.http4sstart.entities.Dog._
import io.circe.syntax._
import org.http4s.{HttpService, _}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class DogService[F[_]: Effect](dogRepository: DogRepository[F]) extends Http4sDsl[F] {

  val DOGS = "dogs"

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "hello" / name =>
        Ok(Dog(name).asJson)

      // CREATE
      case req @ POST -> Root / DOGS =>
        req.decodeJson[Dog]
          .flatMap((dog: Dog) => dogRepository.addDog(dog))
          .flatMap(uuid => Created(uuid))

      // READ
      case GET -> Root / DOGS / id =>
        dogRepository
          .getDog(id)
          .flatMap {
            case Some(dogWithId) => Ok(dogWithId.asJson)
            case None => NotFound()
          }

      // UPDATE
      case req @ PUT -> Root / DOGS =>
        req.decodeJson[DogWithId]
          .flatMap((dogWithId: DogWithId) => dogRepository.updateDog(dogWithId))
          .flatMap(_ => Ok())

      // DELETE
      case DELETE -> Root / DOGS / id =>
        dogRepository.deleteDog(id)
          .flatMap(_ => Ok())
    }
  }
}
