/* ===================================================
 * Copyright © 2013 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */
package kamon.vavr.instrumentation

import java.util.concurrent.Executors

import kamon.Kamon
import kamon.testkit.ContextTesting
import org.scalatest.{Matchers, OptionValues, WordSpec}
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures}
import io.vavr.concurrent.Future

import scala.compat.java8.functionConverterImpls._

class FutureInstrumentationSpec extends WordSpec with Matchers with ScalaFutures with PatienceConfiguration
  with OptionValues with ContextTesting {

  implicit val execContext = Executors.newCachedThreadPool()

  "a Vavr Future created when instrumentation is active" should {
    "capture the active span available when created" which {
      "must be available when executing the future's body" in {

        val context = contextWithLocal("in-future-body")
        val baggageInBody: Future[Option[String]] = Kamon.withContext(context) {
          Future.of(() ⇒ {
            println("1")
            Kamon.currentContext().get(StringKey)
          })
        }

        baggageInBody.await().get() should equal(Some("in-future-body"))
      }

      "must be available when executing callbacks on the future" in {
        val context = contextWithLocal("in-future-transformations")
        val baggageAfterTransformations: Future[Option[String]] = Kamon.withContext(context) {
          val f: Future[String] = Future.of(() => {
            "Hello Kamon!"
          })

            f.map(x => x.toUpperCase())
            .flatMap(len => Future.of(()=>len.toString))
            .map(_ => Kamon.currentContext().get(StringKey))
        }

        baggageAfterTransformations.await().get() should equal(Some("in-future-transformations"))

//        whenReady(baggageAfterTransformations)(baggageValue ⇒ baggageValue should equal(Some("in-future-transformations")))
      }

    }
  }
}

