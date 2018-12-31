package com.hagergroup.sweetpotato.exception

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * Defines and splits the handling of various exceptions in a single place. This handler will be invoked once it has been
 * [com.hagergroup.sweetpotato.app.SweetActivityController.registerExceptionHandler] registered.
 * <p>
 * The exception handler will be invoked at runtime when an exception is thrown and is not handled. You do not need to log the exception, because
 * the [com.hagergroup.sweetpotato.app.SweetActivityController] already takes care of logging it, before invoking the current interface methods.
 * </p>
 *
 * @see com.hagergroup.sweetpotato.app.SweetActivityController.registerExceptionHandler
 *
 * @author Ludovic Roland
 * @since 2018.11.05
 */
interface SweetExceptionHandler
{

  /**
   * Is invoked whenever the [com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle.onRetrieveModel] throws an exception.
   * <p>
   * Warning, it is not ensured that this method will be invoked from the UI thread!
   * </p>
   *
   * @param activity  the [AppCompatActivity] that issued the exception, and which is ensured not to be finishing
   * @param fragment the [Fragment] that issued the exception
   * @param exception the exception that has been thrown
   *
   * @return `true` if the handler has actually handled the exception: this indicates to the framework that it does not need to investigate
   * for a further exception handler anymore
   */
  fun onModelUnavailableException(activity: AppCompatActivity, fragment: Fragment?, exception: ModelUnavailableException): Boolean

  /**
   * Is invoked whenever an activity implementing [com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle] throws an unexpected exception outside from the
   * [com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle.onRetrieveModel] method.
   * <p>
   * This method serves as a fallback on the framework, in order to handle gracefully exceptions and prevent the application from crashing.
   * </p>
   * <p>
   * Warning, it is not ensured that this method will be invoked from the UI thread!
   * </p>
   *
   * @param activity  the [AppCompatActivity] that issued the exception
   * @param fragment the [Fragment] that issued the exception
   * @param throwable the exception that has been triggered
   *
   * @return `true` if the handler has actually handled the exception: this indicates to the framework that it does not need to investigate
   * for a further exception handler anymore
   */
  fun onActivityException(activity: AppCompatActivity, fragment: Fragment?, throwable: Throwable): Boolean

  /**
   * Is invoked whenever a handled exception is thrown with a non-context.
   * <p>
   * This method serves as a fallback on the framework, in order to handle gracefully exceptions and prevent the application from crashing.
   * </p>
   * <p>
   * Warning, it is not ensured that this method will be invoked from the UI thread!
   * </p>
   *
   * @param isRecoverable indicates whether the application is about to crash when the exception has been triggered
   * @param context       the context that issued the exception
   * @param throwable     the exception that has been triggered
   *
   * @return `true` if the handler has actually handled the exception: this indicates to the framework that it does not need to investigate
   * for a further exception handler anymore
   */
  fun onContextException(isRecoverable: Boolean, context: Context?, throwable: Throwable): Boolean

  /**
   * Is invoked whenever a handled exception is thrown outside from an available [Context].
   * <p>
   * This method serves as a fallback on the framework, in order to handle gracefully exceptions and prevent the application from crashing.
   * </p>
   * <p>
   * Warning, it is not ensured that this method will be invoked from the UI thread!
   * </p>
   *
   * @param isRecoverable indicates whether the application is about to crash when the exception has been triggered
   * @param throwable     the exception that has been triggered
   *
   * @return `true` if the handler has actually handled the exception: this indicates to the framework that it does not need to investigate
   * for a further exception handler anymore
   */
  fun onException(isRecoverable: Boolean, throwable: Throwable): Boolean

  fun reportIssueIfNecessary(isRecoverable: Boolean, throwable: Throwable)

}