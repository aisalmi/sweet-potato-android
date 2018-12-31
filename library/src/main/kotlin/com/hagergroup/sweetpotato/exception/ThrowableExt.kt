package com.hagergroup.sweetpotato.exception

import java.io.InterruptedIOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlin.reflect.KClass

/**
 * Attempts to find a specific exception in the provided exception by iterating over the causes, starting with the provided exception itself.
 *
 * @param exceptionClass a list of exception classes to look after
 *
 * @return `null` if and only one of the provided exception classes has not been detected ; the matching cause otherwise
 */
fun Throwable?.searchForCause(vararg exceptionClass: KClass<*>): Throwable?
{
  var newThrowable = this
  var cause: Throwable? = this

  // We investigate over the this causes stack
  while (cause != null)
  {
    exceptionClass.forEach {
      val causeClass = cause?.javaClass

      if (causeClass == it.java)
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

/**
 * @return `true` if and only if the exception results from a connectivity issue by inspecting its causes tree
 */
fun Throwable?.isAConnectivityProblem(): Boolean =
    this.searchForCause(UnknownHostException::class, SocketException::class, SocketTimeoutException::class, InterruptedIOException::class, SSLException::class) != null

/**
 * @return `true` if and only if the exception results from a memory saturation issue (i.e. a [OutOfMemoryError] exception) by
 * inspecting its causes tree
 */
fun Throwable?.isAMemoryProblem(): Boolean =
    this.searchForCause(OutOfMemoryError::class) != null
