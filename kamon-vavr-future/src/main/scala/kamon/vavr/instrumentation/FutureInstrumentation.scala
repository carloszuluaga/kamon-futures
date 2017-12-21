/*
 * =========================================================================================
 * Copyright © 2016 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.vavr.instrumentation

import kamon.Kamon
import kamon.context.HasContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation._

@Aspect
class FutureInstrumentation {

  @DeclareMixin("kamon.vavr.instrumentation..* && kamon.vavr.instrumentation.Test$$Lambda*")
  def mixinTrace: HasContext = {
    println("DeclareMixin")
    HasContext.fromCurrentContext()
  }

  @Pointcut("execution(* kamon.vavr.instrumentation.Test.lambda*(..)) && this(runnable)")
  def sisas(runnable: HasContext) = println("sisas0")

  @After("sisas(runnable)")
  def afterSisas(runnable: HasContext): Unit = {
    println("sisas1" + runnable.context.toString)
  }

  @DeclareMixin("io.vavr.concurrent..* && java.lang.Runnable+")
  def mixinTraceContextAwareToFutureRelatedRunnable: HasContext = {
    println("DeclareMixin")
    HasContext.fromCurrentContext()
  }

  @Pointcut("execution((io.vavr.concurrent..* && java.lang.Runnable+).new(..)) && this(runnable)")
  def futureRelatedRunnableCreation(runnable: HasContext): Unit = {}

  @After("futureRelatedRunnableCreation(runnable)")
  def afterCreation(runnable: HasContext): Unit = {
    // Force traceContext initialization.
    runnable.context
  }

  @Pointcut("execution(* (io.vavr.concurrent..* && java.lang.Runnable+).run()) && this(runnable)")
  def futureRelatedRunnableExecution(runnable: HasContext) = {}

  @Around("futureRelatedRunnableExecution(runnable)")
  def aroundExecution(pjp: ProceedingJoinPoint, runnable: HasContext): Any = {
    Kamon.withContext(runnable.context) {
      pjp.proceed()
    }
  }

}
