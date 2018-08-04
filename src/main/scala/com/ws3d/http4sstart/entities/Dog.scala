package com.ws3d.http4sstart.entities

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Dog(name: String)

object Dog {
  implicit val dogDecoder: Decoder[Dog] = deriveDecoder
  implicit val dogEncoder: Encoder[Dog] = deriveEncoder
}

case class DogWithId(id: String, name: String)

object DogWithId {
  implicit val dogDecoder: Decoder[DogWithId] = deriveDecoder
  implicit val dogEncoder: Encoder[DogWithId] = deriveEncoder
}
