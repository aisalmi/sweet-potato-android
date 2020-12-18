package com.hagergroup.sweetpotato.exception

import com.hagergroup.sweetpotato.app.SweetActivityController

/**
 * Defined as a wrapper over the built-in [Thread.UncaughtExceptionHandler].
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
class SweetUncaughtExceptionHandler(private val builtinUncaughtExceptionHandler: Thread.UncaughtExceptionHandler?)
  : Thread.UncaughtExceptionHandler
{

  override fun uncaughtException(thread: Thread?, throwable: Throwable)
  {
    try
    {
      SweetActivityController.handleException(false, throwable)
    }
    finally
    {
      builtinUncaughtExceptionHandler?.uncaughtException(thread, throwable)
    }
  }

}
