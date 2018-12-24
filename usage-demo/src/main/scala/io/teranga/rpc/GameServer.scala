/*
 * Copyright 2017 Abdoulaye Diallo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.teranga.rpc

import io.grpc.stub.StreamObserver
import io.teranga.GrpcServer
import io.teranga.rpc.Play.OUTCOME
import monix.execution.Scheduler.{ global => scheduler }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object GameServer extends GrpcServer {

  val r = scala.util.Random

  class GameService extends GameGrpc.Game {
    override def streamPlayFor(responseObserver: StreamObserver[Play]): StreamObserver[Player] =
      new StreamObserver[Player] {
        override def onNext(value: Player): Unit = {
          println(s"new player onboarded: $value")
          scheduler.scheduleWithFixedDelay(0.seconds, 1.seconds) {
            responseObserver.onNext(
              Play(Some(value), OUTCOME.values(r.nextInt(OUTCOME.values.size)))
            )
          }
        }

        override def onError(t: Throwable): Unit = {
          println(s"error: $t")
          responseObserver.onError(t)
        }

        override def onCompleted(): Unit = {
          println("game over")
          responseObserver.onCompleted()
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val gs = GameGrpc.bindService(new GameService(), ExecutionContext.global)
    runServer(gs)
  }
}
