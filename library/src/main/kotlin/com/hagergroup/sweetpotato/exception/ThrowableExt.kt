package com.hagergroup.sweetpotato.exception

import java.io.InterruptedIOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlin.reflect.KClass

fun Throwable?.searchForCause(vararg exceptionClass: KClass<*>): Throwable?
{
  var newThrowable = this
  var cause: Throwable? = this

  // We investigate over the this causes stack
  while (cause != null)
  {
    exceptionClass.forEach {
      val causeClass = cause?.javaClass

      if (causeClass == it)
      {
        return cause
      }

      // We scan the cause class hierarchy
      var superclass: Class<*>? = causeClass?.superclass

      while (superclass != null)
      {
        if (superclass == it)
        {
          return cause
        }

        superclass = superclass.superclass
      }
    }

    // It seems that when there are no more causes, the exception itself is returned as a cause: stupid implementation!
    if (newThrowable?.cause == newThrowable)
    {
      break
    }

    newThrowable = cause
    cause = newThrowable.cause
  }

  return null
}

fun Throwable?.isAConnectivityProblem(): Boolean =
    this.searchForCause(UnknownHostException::class, SocketException::class, SocketTimeoutException::class, InterruptedIOException::class, SSLException::class) != null

fun Throwable?.isAMemoryProblem(): Boolean =
    this.searchForCause(OutOfMemoryError::class) != null
