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
import io.grpc.stub.StreamObserver
import io.teranga.GrpcServer

import scala.concurrent.ExecutionContext

object TelemetryServer extends GrpcServer {

  class Kepler extends TelemetryGrpc.Telemetry {

    override def report(responseObserver: StreamObserver[TSummary]): StreamObserver[TEvent] =
      new StreamObserver[TEvent] {
        override def onNext(value: TEvent): Unit =
          println(value)

        override def onError(t: Throwable): Unit = {
          println(t)
          responseObserver.onError(t)
        }
        override def onCompleted(): Unit = {
          responseObserver.onNext(TSummary())
          responseObserver.onCompleted()
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val gs = TelemetryGrpc.bindService(new Kepler(), ExecutionContext.global)
    runServer(gs)
  }

}
