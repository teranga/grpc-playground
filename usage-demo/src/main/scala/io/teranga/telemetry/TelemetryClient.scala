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

package io.teranga.telemetry
import java.util.concurrent.TimeUnit

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import monix.execution.Scheduler.{ global => scheduler }

import scala.io.StdIn
import scala.util.Random

object TelemetryClient {

  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext(true).build
    val client  = TelemetryGrpc.stub(channel)

    val requestObserver = client.report(responseObserver)

    scheduler.scheduleWithFixedDelay(0l, 1l, TimeUnit.SECONDS, () => {
      requestObserver.onNext(newEvent)
    })

    StdIn.readLine
    responseObserver.onCompleted()

  }

  def responseObserver: StreamObserver[TSummary] = new StreamObserver[TSummary] {
    def onError(t: Throwable): Unit =
      println(s"ERROR: $t")
    def onCompleted(): Unit =
      println("GAME OVER")
    def onNext(value: TSummary): Unit =
      println(s"summary: $value")
  }

  def newEvent =
    TEvent(Option.empty, Random.nextInt, Random.nextFloat())

}
