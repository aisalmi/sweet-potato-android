package com.hagergroup.sweetpotato.app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.EscapeToRedirectorAnnotation
import com.hagergroup.sweetpotato.exception.SweetExceptionHandler
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import timber.log.Timber

/**
 * Is responsible for intercepting an activity starting and redirect it to a prerequisite one if necessary, and for handling globally exceptions.
 * <p>
 * Everything described here which involves the [AppCompatActivity], is applicable provided the activity is a [Sweetable].
 * </p>
 * <p>
 * It is also a container for multiple interfaces relative to its architecture.
 * </p>
 *
 * @author Ludovic Roland
 * @since 2018.11.05
 */
object SweetActivityController
{

  /**
   * An interface which is requested when a new [AppCompatActivity] is bound to be started.
   * <p>
   * The redirector acts as a controller over the activities starting phase: if an activity should be started before another one is really
   * active, this is the right place to handle this at runtime.
   * </p>
   * <p>
   * This component is especially useful when ones need to make sure that an [AppCompatActivity] has actually been submitted to the end-user before
   * resuming a workflow. The common cases are the traditional application splash screen, or a signin/signup process.
   * </p>
   *
   * @see registerRedirector
   */
  interface Redirector
  {

    /**
     * Will be invoked by the framework, in order to know whether an [AppCompatActivity] should be
     * started instead of the provided one, which is supposed to have just started, or when the
     * [AppCompatActivity.onNewIntent] method is invoked. However, the method will be not been invoked when those methods are invoked due to a
     * configuration change.
     * <p>
     * Caution: if an [Exception] is thrown during the method execution, the application will crash!
     * </p>
     *
     * @param activity which is bound to be displayed
     *
     * @return `null` if and only if nothing is to be done, i.e. no activity should be started instead. Otherwise, the given intent will be
     * executed: in that case, the provided activity finishes
     *
     * @see needsRedirection
     */
    fun getRedirection(activity: AppCompatActivity): Intent?

  }

  /**
   * An interface which is queried during the various life cycle events of a [com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle].
   * <p>
   * An interceptor is the ideal place for centralizing in one place many of the [AppCompatActivity]/[Fragment] entity life cycle
   * events.
   * </p>
   *
   * @see registerRedirector
   */
  interface Interceptor
  {

    /**
     * Invoked every time a new event occurs on the provided [AppCompatActivity]/[Fragment]. For instance, this is an ideal for logging
     * application usage analytics.
     * <p>
     * The framework ensures that this method will be invoked from the UI thread, hence the method implementation should last a very short time!
     * </p>
     * <p>
     * Caution: if an exception is thrown during the method execution, the application will crash!
     * </p>
     *
     * @param activity  the [AppCompatActivity] on which a life cycle event occurs
     * @param fragment  the [Fragment] on which the life cycle event occurs
     * @param event     the [Lifecycle.Event] that has just happened
     */
    fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)

  }

  /**
   * When a new activity is started because of a redirection, the newly started activity will receive the
   * initial activity [Intent] through this key.
   *
   * @see needsRedirection
   * @see registerRedirector
   */
  const val CALLING_INTENT_EXTRA = "callingIntentExtra"

  private var redirector: SweetActivityController.Redirector? = null

  private var interceptor: SweetActivityController.Interceptor? = null

  var exceptionHandler: SweetExceptionHandler? = null
    private set

  /**
   * Attempts to decode from the provided [AppCompatActivity] the original [Intent] that was
   *
   * @param activity the Activity whose Intent will be analyzed
   *
   * @return an [Intent] that may be started if the provided [AppCompatActivity] actually contains a reference to
   * another [AppCompatActivity] ; `null` otherwise
   *
   * @see CALLING_INTENT_EXTRA
   * @see needsRedirection
   * @see registerInterceptor
   */
  fun extractCallingIntent(activity: AppCompatActivity): Intent? =
      activity.intent.getParcelableExtra(SweetActivityController.CALLING_INTENT_EXTRA)

  /**
   * Remembers the activity redirector that will be used by the framework, before starting a new [AppCompatActivity]
   *
   * @param redirector the redirector that will be requested at runtime, when a new activity is being started; if `null`, no redirection mechanism will be set up
   */
  fun registerRedirector(redirector: SweetActivityController.Redirector?)
  {
    this.redirector = redirector
  }

  /**
   * Remembers the activity interceptor that will be used by the framework, on every [Lifecycle.Event]
   * during the underlying [AppCompatActivity].
   *
   * @param interceptor the interceptor that will be invoked at runtime, on every event; if `null`, no interception mechanism will be used
   */
  fun registerInterceptor(interceptor: SweetActivityController.Interceptor?)
  {
    this.interceptor = interceptor
  }

  /**
   * Remembers the exception handler that will be used by the framework.
   *
   * @param exceptionHandler the handler that will be invoked in case of exception; if `null`, no exception handler will be used
   */
  @Synchronized
  fun registerExceptionHandler(exceptionHandler: SweetExceptionHandler?)
  {
    this.exceptionHandler = exceptionHandler
  }

  /**
   * Is invoked by the framework every time a [Lifecycle.Event] occurs for the provided activity. You should not invoke that method yourself!
   * <p>
   * Note that the method is synchronized, which means that the previous call will block the next one, if no thread is spawn.
   * </p>
   *
   * @param activity  the [AppCompatActivity] which is involved with the event
   * @param event     the [Lifecycle.Event] that has just happened for that activity
   */
  @Synchronized
  fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)
  {
    interceptor?.onLifeCycleEvent(activity, fragment, event)
  }

  /**
   * Dispatches the exception to the [SweetExceptionHandler], and invokes the right method depending on its nature.
   * <p>
   * The framework is responsible for invoking that method every time an unhandled exception is thrown. If no
   * [SweetExceptionHandler]is registered, the exception will be only logged, and the method will return `false`.
   * </p>
   * <p>
   * Note that this method is `synchronized`, which prevents it from being invoking while it is already being executed, and which involves that
   * only one [Throwable] may be handled at the same time.
   * </p>
   *
   * @param isRecoverable indicates whether the application is about to crash when the exception has been triggered
   * @param context       the context that originated the exception
   * @param fragment      when not `null`, the exception has been thrown from the ìt
   * @param throwable     the reported exception
   *
   * @return `true` if the exception has been handled, `else` otherwise
   *
   * @see registerExceptionHandler
   */
  @Synchronized
  fun handleException(isRecoverable: Boolean, context: Context?, fragment: Fragment?, throwable: Throwable): Boolean
  {
    if (exceptionHandler == null)
    {
      Timber.w(throwable, "Detected an exception which will not be handled during the processing of the context with name '${if (context != null) context::class.qualifiedName else null}'")
      return false
    }

    val activity = if (context is AppCompatActivity)
    {
      context
    }
    else
    {
      null
    }

    try
    {
      if (activity != null && throwable is ModelUnavailableException)
      {
        Timber.w(throwable, "Caught an exception during the retrieval of the business objects from the activity from class with name '${activity::class.qualifiedName}'")

        // We do nothing if the activity is dying
        return if (activity.isFinishing == true)
        {
          true
        }
        else
        {
          exceptionHandler?.onModelUnavailableException(activity, fragment, throwable) ?: false
        }
      }
      else
      {
        Timber.w(throwable, "Caught an exception during the processing of the Context from class with name '${if (context != null) context::class.qualifiedName else null}'")

        // For this special case, we ignore the case when the activity is dying
        return if (activity != null)
        {
          exceptionHandler?.onActivityException(activity, fragment, throwable) ?: false
        }
        else if (context != null)
        {
          exceptionHandler?.onContextException(isRecoverable, context, throwable) ?: false
        }
        else
        {
          exceptionHandler?.onException(isRecoverable, throwable) ?: false
        }
      }
    }
    catch (otherThrowable: Throwable)
    {
      // Just to make sure that handled exceptions do not trigger un-handled exceptions on their turn ;(
      Timber.e(otherThrowable, "An error occurred while attempting to handle an exception coming from the Context from class with name '${context?.javaClass?.name}'")

      return false
    }

  }

  /**
   * Indicates whether a redirection is required before letting the activity continue its life cycle. It launches the redirected [AppCompatActivity] if a
   * redirection is needed, and provide to its [Intent] the initial activity [Intent] trough the extra [CALLING_INTENT_EXTRA] key.
   * <p>
   * If the provided activity exposes the [EscapeToRedirectorAnnotation] annotation, the method returns `false`.
   * </p>
   * <p>
   * Note that this method does not need to be marked as `synchronized`, because it is supposed to be invoked systematically from the UI thread.
   * </p>
   *
   * @param activity the activity which is being proved against the [Redirector]
   *
   * @return `true` if and only if the given activity should be paused (or ended) and if another activity should be launched instead through the
   * [AppCompatActivity.startActivity] method
   *
   * @see extractCallingIntent
   * @see Redirector.getRedirection
   * @see EscapeToRedirectorAnnotation
   */
  fun needsRedirection(activity: AppCompatActivity): Boolean
  {
    if (redirector == null)
    {
      return false
    }

    Timber.d("Check for annotation")

    if (activity::class.java.getAnnotation(EscapeToRedirectorAnnotation::class.java) != null)
    {
      Timber.d("The Activity with class '${activity::class.qualifiedName}' is escaped regarding the Redirector")

      return false
    }

    Timber.d("Check for redirection")

    val intent = redirector?.getRedirection(activity) ?: return false

    Timber.d("A redirection is needed")

    // We redirect to the right Activity
    // We consider the parent activity in case it is embedded (like in an ActivityGroup)
    val formerIntent = if (activity.parent != null) activity.parent.intent else activity.intent
    intent.putExtra(SweetActivityController.CALLING_INTENT_EXTRA, formerIntent)

    // Disables the fact that the new started activity should belong to the tasks history and from the recent tasks
    activity.startActivity(intent)

    // We now finish the redirected Activity
    activity.finish()

    return true
  }

}