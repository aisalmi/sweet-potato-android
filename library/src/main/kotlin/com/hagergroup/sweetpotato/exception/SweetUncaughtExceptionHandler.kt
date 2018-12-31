package com.hagergroup.sweetpotato.exception

import android.content.Context
import com.hagergroup.sweetpotato.app.SweetActivityController

/**
 * Defined as a wrapper over the built-in [Thread.UncaughtExceptionHandler].
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
class SweetUncaughtExceptionHandler(private val context: Context, private val builtinUncaughtExceptionHandler: Thread.UncaughtExceptionHandler?)
  : Thread.UncaughtExceptionHandler
{

  override fun uncaughtException(thread: Thread?, throwable: Throwable)
  {
    try
    {
      SweetActivityController.handleException(false, context, null, throwable)
    }
    finally
    {
      builtinUncaughtExceptionHandler?.uncaughtException(thread, throwable)
    }
  }

}