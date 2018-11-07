package com.hagergroup.sweetpotato.app

import android.app.Application
import com.hagergroup.sweetpotato.exception.ExceptionHandler
import com.hagergroup.sweetpotato.exception.ExceptionHandlers
import com.hagergroup.sweetpotato.exception.SweetIssueAnalyzer
import com.hagergroup.sweetpotato.exception.SweetUncaughtExceptionHandler
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
abstract class SweetApplication
  : Application()
{

  class I18N(val dialogBoxErrorTitle: String,
             val businessObjectAvailabilityProblemHint: String,
             val serviceProblemHint: String,
             val connectivityProblemHint: String,
             val connectivityProblemRetryHint: String,
             val otherProblemHint: String,
             val applicationName: String,
             val reportButtonLabel: String,
             val retrievingLogProgressMessage: String)

  companion object
  {

    var isOnCreatedDone = false
      private set

  }

  private var onCreateInvoked = false

  protected abstract fun getI18N(): SweetApplication.I18N


  @Synchronized
  override fun onCreate()
  {
    if (onCreateInvoked == true)
    {
      Timber.e("The 'Application.onCreate()' method has already been invoked!")
      return
    }

    onCreateInvoked = true

    try
    {
      val start = System.currentTimeMillis()

      if (shouldBeSilent() == true)
      {
        Timber.d("Application starting in silent mode")

        super.onCreate()

        onCreateCustomSilent()

        return
      }

      Timber.d("Application starting")

      super.onCreate()

      // We register the application exception handler as soon as possible, in order to be able to handle exceptions
      setupDefaultExceptionHandlers()

      // We register the Activity redirector
      getActivityRedirector()?.let {
        SweetActivityController.registerRedirector(it)
      }

      // We register the entities interceptor
      getInterceptor()?.let {
        SweetActivityController.registerInterceptor(it)
      }

      onCreateCustom()

      Timber.d("The application with package name '$packageName' has started in ${System.currentTimeMillis() - start} ms")
    }
    finally
    {
      SweetApplication.isOnCreatedDone = true
    }
  }

  protected open fun shouldBeSilent(): Boolean
  {
    return false
  }

  protected open fun getActivityRedirector(): SweetActivityController.SweetRedirector?
  {
    return null
  }

  protected open fun getInterceptor(): SweetActivityController.SweetInterceptor?
  {
    return null
  }

  protected open fun getExceptionHandler(): ExceptionHandler =
      ExceptionHandlers.DefaultExceptionHandler(getI18N(), SweetIssueAnalyzer.DefaultIssueAnalyzer(this))

  protected open fun onSetupExceptionHandlers()
  {
  }

  protected open fun onCreateCustomSilent()
  {
  }

  protected open fun onCreateCustom()
  {
  }

  private fun setupDefaultExceptionHandlers()
  {
    // We let the overidding application register its exception handlers
    onSetupExceptionHandlers()

    SweetActivityController.registerExceptionHandler(getExceptionHandler())

    // We make sure that all uncaught exceptions will be intercepted and handled
    val builtinUuncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    val uncaughtExceptionHandler = SweetUncaughtExceptionHandler(applicationContext, builtinUuncaughtExceptionHandler)

    Timber.d("The application with package name '$packageName' " + (if (builtinUuncaughtExceptionHandler == null) "does not have" else "has") + " a built-in default uncaught exception handler")
    Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
  }

}