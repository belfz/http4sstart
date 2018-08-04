package com.ws3d.http4sstart

import cats.effect.IO
import fs2.StreamApp
import fs2.Stream
import org.http4s.server.blaze.BlazeBuilder

object DogServer extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream(args, requestShutdown)
}

object ServerStream {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    Stream.eval(DogRepository.empty[IO]).flatMap { dogRepo =>
      BlazeBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .mountService(new DogService[IO](dogRepo).service, "/")
        .serve
    }
}
