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

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import io.teranga.rpc.GameGrpc.GameStub

import scala.io.StdIn

object GameClient {

  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext(true).build
    val client  = GameGrpc.stub(channel)
    val players = List(Player("Mamadou"), Player("Binta"), Player("Cellou"))

    players.foreach(
      requestObserver(client).onNext(_)
    )
    StdIn.readLine()
  }

  def responseObserver: StreamObserver[Play] = new StreamObserver[Play] {
    def onError(t: Throwable): Unit =
      println(s"ERROR: $t")
    def onCompleted(): Unit =
      println("GAME OVER")
    def onNext(value: Play): Unit =
      println(s"${value.player.map(p => p.name).orNull}\t: ${value.outcome}")
  }
  def requestObserver(client: GameStub): StreamObserver[Player] =
    client.streamPlayFor(responseObserver)
}
