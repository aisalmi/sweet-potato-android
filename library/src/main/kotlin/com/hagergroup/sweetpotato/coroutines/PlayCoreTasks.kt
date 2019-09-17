package com.hagergroup.sweetpotato.coroutines

import com.google.android.play.core.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * @author Ludovic Roland
 * @since 2019.05.29
 */

/**
 * Awaits for completion of the task without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException].
 */
public suspend fun <T> Task<T>.await(): T?
{
  if (isComplete == true)
  {
    return if (exception == null)
    {
      result as T
    }
    else
    {
      null
    }
  }

  return suspendCancellableCoroutine { cont ->
    addOnSuccessListener {
      if (exception == null)
      {
        cont.resume(result as T)
      }
      else
      {
        cont.resume(null)
      }
    }
    addOnFailureListener {
      cont.resume(null)
    }
  }
}