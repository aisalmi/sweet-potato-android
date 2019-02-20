package com.hagergroup.sweetpotato.coroutines

import android.content.Context
import com.hagergroup.sweetpotato.app.SweetActivityController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2019.02.20
 */
class SweetCoroutines
{

  abstract class SweetGuardedCoroutine(val context: Context?)
  {

    @Throws(Throwable::class)
    abstract suspend fun run()

    open fun onThrowable(throwable: Throwable): Throwable? =
        throwable

  }

  companion object
  {

    fun execute(guardedCoroutine: SweetGuardedCoroutine)
    {
      GlobalScope.launch(Dispatchers.Default + CoroutineExceptionHandler { _, throwable ->
        Timber.w(throwable, "An error occurred while executing the SweetCoroutine")

        val modifiedThrowable = guardedCoroutine.onThrowable(throwable)

        if (modifiedThrowable != null)
        {
          SweetActivityController.handleException(true, guardedCoroutine.context, null, modifiedThrowable)
        }
      }) {
        guardedCoroutine.run()
      }
    }

  }

}