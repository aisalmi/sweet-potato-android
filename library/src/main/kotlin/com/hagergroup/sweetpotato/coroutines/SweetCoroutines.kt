package com.hagergroup.sweetpotato.coroutines

import android.content.Context
import com.hagergroup.sweetpotato.app.SweetActivityController
import com.hagergroup.sweetpotato.coroutines.SweetCoroutines.SweetGuardedCoroutine
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A container which encloses the [SweetGuardedCoroutine] type which enable sto run commands in a coroutine.
 *
 * @author Ludovic Roland
 * @since 2019.02.20
 */
class SweetCoroutines
{

  /**
   * An object used as command, which is allowed to throw an exception during its execution.
   * <p>
   * During the command execution, any thrown [Throwable] will be delivered to the exception handler registered with
   * the [SweetActivityController.registerExceptionHandler] the, through its
   * [SweetActivityController.handleException] method, so that it can be controlled in a central way, and not "swallowed".
   * </p>
   * <p>
   * It has been specifically designed for being able to throw exceptions within the Coroutine way.
   * </p>
   *
   * @param context The context which will be used when reporting an exception to the exception handler.
   */
  abstract class SweetGuardedCoroutine(val context: Context?)
  {

    /**
     * The body of the command execution.
     *
     * @throws Exception the method allows an exception to be thrown, and will be appropriately caught by the
     *                   [SweetActivityController.handleException] method.
     */
    @Throws(Exception::class)
    abstract suspend fun run()

    /**
     * A fallback method which will be triggered if a [Throwable] is thrown during the [run] method, so as to let the caller a
     * chance to handle locally the exception.
     * <p>
     * By default, the method does nothing and returns the provided [Throwable].
     * </p>
     *
     * @param throwable the exception that has been thrown during the [run] execution
     * @return `null` if and only if the method has handled the exception and that the [com.hagergroup.sweetpotato.exception.SweetExceptionHandler] should not be
     * invoked ; otherwise, the [Throwable]  that should be submitted to the [com.hagergroup.sweetpotato.exception.SweetExceptionHandler]
     */
    open suspend fun onThrowable(throwable: Throwable): Throwable? =
        throwable

  }

  companion object
  {

    /**
     * Executes the given command.
     *
     * @param guardedCoroutine the command to run
     */
    fun execute(coroutineScope: CoroutineScope, guardedCoroutine: SweetGuardedCoroutine)
    {
      coroutineScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        coroutineScope.launch(Dispatchers.Main)
        {
          Timber.w(throwable, "An error occurred while executing the SweetCoroutine")

          val modifiedThrowable = guardedCoroutine.onThrowable(throwable)

          if (modifiedThrowable != null)
          {
            SweetActivityController.handleException(true, guardedCoroutine.context, null, modifiedThrowable)
          }
        }
      }) {
        guardedCoroutine.run()
      }
    }

  }

}