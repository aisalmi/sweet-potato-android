package com.hagergroup.sweetpotato.exception

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle
import org.jetbrains.anko.toast
import timber.log.Timber

/**
 * A wrapper class which holds exception handlers implementations.
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
sealed class ExceptionHandlers
{

  /**
   * A simple implementation of [DefaultExceptionHandler] which pops-up error dialog boxes and toasts.
   * <p>
   * The labels of the dialogs and the toasts are i18ned though the provided [SweetApplication.I18N] provided instance.
   * </p>
   **/
  //TODO : rework the I18N and the "showdialog" part
  open class AbstractExceptionHandler(protected val i18n: SweetApplication.I18N, protected val issueAnalyzer: SweetIssueAnalyzer)
    : SweetExceptionHandler
  {

    /**
     * Defines how the framework should behave when an Internet connectivity problem has been detected.
     */
    enum class ConnectivityUIExperience
    {

      /**
       * Should open an [AlertDialog] with a "retry"/"ok" button.
       */
      DialogRetry,

      /**
       * Should open an [AlertDialog] with a single "ok" button.
       */
      Dialog,

      /**
       * Should issue an Android [android.widget.Toast].
       */
      Toast
    }

    override fun onModelUnavailableException(activity: AppCompatActivity, fragment: Fragment?, exception: ModelUnavailableException): Boolean
    {
      return if (handleCommonCauses(activity, fragment, exception, ConnectivityUIExperience.DialogRetry) == true)
      {
        true
      }
      else if (handleOtherCauses(activity, fragment, exception) == true)
      {
        true
      }
      else
      {
        return onModelUnavailableExceptionFallback(activity, fragment, exception)
      }
    }

    override fun onActivityException(activity: AppCompatActivity, fragment: Fragment?, throwable: Throwable): Boolean
    {
      return if (handleCommonCauses(activity, fragment, throwable, ConnectivityUIExperience.Toast) == true)
      {
        true
      }
      else if (handleOtherCauses(activity, fragment, throwable) == true)
      {
        true
      }
      else
      {
        onActivityExceptionFallback(activity, fragment, throwable)
      }
    }

    override fun onContextException(isRecoverable: Boolean, context: Context?, throwable: Throwable): Boolean
    {
      return if (handleCommonCauses(null, null, throwable, null) == true)
      {
        true
      }
      else
      {
        onContextExceptionFallback(isRecoverable, context, throwable)
      }
    }

    override fun onException(isRecoverable: Boolean, throwable: Throwable): Boolean
    {
      return if (handleCommonCauses(null, null, throwable, null) == true)
      {
        true
      }
      else
      {
        onExceptionFallback(isRecoverable, throwable)
      }
    }

    override fun reportIssueIfNecessary(isRecoverable: Boolean, throwable: Throwable)
    {

    }

    /**
     * This method will be invoked by the [onModelUnavailableException] method, as a fallback if the provided exception has not been
     * handled neither by the [handleCommonCauses] nor the [handleOtherCauses] methods.
     * <p>
     * A dialog box which reports the problem will be popped up by the current implementation: when the end-user acknowledges the issue reported by
     * the dialog box, the Activity will be finished}.
     * </p>
     *
     * @param activity  the activity from which the exception has been thrown
     * @param fragment  the fragment responsible for having thrown the exception
     * @param exception the exception that has been thrown
     * @return `true` if and only if the exception has been handled
     *
     * @see onModelUnavailableException
     */
    protected open fun onModelUnavailableExceptionFallback(activity: AppCompatActivity?, fragment: Fragment?, exception: ModelUnavailableException): Boolean
    {
      showDialog(activity, i18n.dialogBoxErrorTitle, i18n.businessObjectAvailabilityProblemHint, activity?.getString(android.R.string.ok) ?: "OK", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()

        // We leave the activity, because we cannot go any further
        activity?.finish()
      }, null, null, null)
      return if (issueAnalyzer.handleIssue(exception) == true)
      {
        true
      }
      else
      {
        true
      }
    }

    /**
     * This method will be invoked by the [onActivityException] method, as a fallback if the provided exception has not been handled neither
     * by the  [handleCommonCauses] nor the [handleOtherCauses] methods.
     * <p>
     * A dialog box which reports the problem will be popped up by the current implementation: when the end-user acknowledges the issue reported by
     * the dialog box, the Activity will be finished}.
     * </p>
     *
     * @param activity  the activity from which the exception has been thrown
     * @param fragment the fragment responsible for having thrown the exception
     * @param throwable the exception that has been thrown
     *
     * @return `true` if and only if the exception has been handled
     *
     * @see onActivityException
     */
    protected open fun onActivityExceptionFallback(activity: AppCompatActivity, fragment: Fragment?, throwable: Throwable): Boolean
    {
      showDialog(activity, i18n.dialogBoxErrorTitle, i18n.otherProblemHint, activity.getString(android.R.string.ok), DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()

        // We leave the activity, because we cannot go any further
        activity.finish()
      }, null, null, null)

      submitToIssueAnalyzer(throwable)
      return true
    }

    /**
     * This method will be invoked by the [onContextException] method, as a fallback if the provided exception has not been handled neither
     * by the [handleCommonCauses] method.
     *
     * @param isRecoverable indicates whether the application is about to crash when the exception has been triggered
     * @param context       the context that issued the exception
     * @param throwable     the exception that has been triggered
     *
     * @return `true` if and only if the exception has been handled
     *
     * @see onContextException
     */
    protected open fun onContextExceptionFallback(isRecoverable: Boolean, context: Context?, throwable: Throwable): Boolean
    {
      submitToIssueAnalyzer(throwable)
      return false
    }

    /**
     * This method will be invoked by the [onException] method, as a fallback if the provided exception has not been handled neither by the
     * [handleCommonCauses] method.
     *
     * @param isRecoverable indicates whether the application is about to crash when the exception has been triggered
     * @param throwable     the exception that has been triggered
     *
     * @return `true` if and only if the exception has been handled
     *
     * @see onException
     */
    protected open fun onExceptionFallback(isRecoverable: Boolean, throwable: Throwable): Boolean
    {
      submitToIssueAnalyzer(throwable)
      return false
    }

    /**
     * Checks for an Internet connectivity issue or a memory saturation issue inside the provided exception root causes.
     * <p>
     * This method is especially useful when overriding the [AbstractExceptionHandler], in order to let the framework hunt for
     * common troubles.
     * </p>
     *
     * @param activity                 the activity which has triggered the exception
     * @param fragment                 the fragment which has triggered the exception
     * @param throwable                the exception to analyze
     * @param connectivityUIExperience indicates what end-user experience to deliver if the problem is an Internet connectivity issue
     *
     * @return `true` if and only if the exception has been handled
     *
     * @see handleConnectivityProblemInCause
     * @see handleMemoryProblemInCause
     */
    protected open fun handleCommonCauses(activity: AppCompatActivity?, fragment: Fragment?, throwable: Throwable, connectivityUIExperience: ConnectivityUIExperience?): Boolean
    {
      return if (throwable.isAConnectivityProblem() == true && handleConnectivityProblemInCause(activity, fragment, throwable, connectivityUIExperience) == true)
      {
        true
      }
      else
      {
        throwable.isAMemoryProblem() == true && handleMemoryProblemInCause(throwable) == true
      }
    }

    /**
     * If an [SweetIssueAnalyzer] is registered, this will invoke the [SweetIssueAnalyzer.handleIssue] method.
     *
     * @param throwable the exception that has previously been thrown
     */
    protected open fun submitToIssueAnalyzer(throwable: Throwable)
    {
      if (issueAnalyzer.handleIssue(throwable) == true)
      {
        Timber.i("The exception belonging to the class '$throwable' could be analyzed")
      }
      else
      {
        Timber.w("The exception belonging to the class '$throwable' could not be analyzed")
      }
    }

    /**
     * The method which should be invoked internally when reporting an error dialog box. The parameters are the same as for the
     * [onShowDialog] method.
     * <p>
     * It is possible to invoke that method from any thread.
     * </p>
     */
    protected open fun showDialog(activity: AppCompatActivity?, dialogTitle: CharSequence, dialogMessage: CharSequence, positiveButton: CharSequence,
                                  positiveClickListener: DialogInterface.OnClickListener, negativeButton: CharSequence?, negativeClickListener: DialogInterface.OnClickListener?,
                                  onCancelListener: DialogInterface.OnCancelListener?)
    {
      if (activity?.isFinishing == true)
      {
        // We do nothing, because there is no user interface any more!
        return
      }

      // We make sure that the dialog is popped from the UI thread
      activity?.runOnUiThread(Runnable {
        if (activity.isFinishing == true)
        {
          // We do nothing, because there is no user interface any more!
          return@Runnable
        }

        try
        {
          onShowDialog(activity, dialogTitle, dialogMessage, positiveButton, positiveClickListener, negativeButton, negativeClickListener, onCancelListener)
        }
        catch (throwable: Throwable)
        {
          if (activity.isFinishing == false)
          {
            Timber.e(throwable, "Could not open an error dialog box, because an exceptoin occurred while displaying it!")
          }
          else
          {
            // It is very likely that the activity has been finished in the meantime, hence we do not log anything
          }
        }
      })
    }

    /**
     * A place holder for handling in a centralized way all kinds of exceptions.
     * <p>
     * When deriving from the [AbstractExceptionHandler] class, this method should be overridden, so as to handle all
     * application specific exceptions.
     * </p>
     *
     * @param activity  the activity which has triggered the exception
     * @param fragment  the fragment which has triggered the exception
     * @param throwable the exception to analyze
     *
     * @return `true` if and only if the exception has been handled; the current implementation returns `false`
     */
    protected open fun handleOtherCauses(activity: AppCompatActivity?, fragment: Fragment?, throwable: Throwable): Boolean =
        false

    /**
     * Is invoked when a connectivity issue has been detected, and display a dialog box if any.
     * <p>
     * If such Internet connectivity issue is detected, a [android.widget.Toast] will be displayed if the [ConnectivityUIExperience] parameter is set to
     * [ConnectivityUIExperience.Toast] ; otherwise, a dialog box will be popped up: if the end-user does not hit the button which proposes to
     * retry, the Activity will be finished.
     * </p>
     *
     * @param activity                 the activity which has triggered the exception ; may be `null`, and in that case, the hereby implementation does nothing
     * @param fragment                 the fragment which has triggered the exception
     * @param throwable                the exception which is supposed to be related to an Internet issue
     * @param connectivityUIExperience indicates the end-user experience to provide if a connectivity problem has been detected
     *
     * @return `true` if and only a connection issue has been detected
     */
    protected open fun handleConnectivityProblemInCause(activity: AppCompatActivity?, fragment: Fragment?, throwable: Throwable, connectivityUIExperience: ConnectivityUIExperience?): Boolean
    {
      if (activity == null)
      {
        // In that case, we do nothing!
        return true
      }

      val lifeCycle: SweetLifeCycle? = when
      {
        fragment is SweetLifeCycle -> fragment
        activity is SweetLifeCycle -> activity
        else                       -> null
      }

      activity.runOnUiThread {
        if (lifeCycle == null || connectivityUIExperience == ConnectivityUIExperience.Toast)
        {
          activity.toast(i18n.connectivityProblemHint)
        }
        else
        {
          val retry = connectivityUIExperience == ConnectivityUIExperience.DialogRetry && activity is SweetLifeCycle
          showDialog(activity, i18n.dialogBoxErrorTitle, i18n.connectivityProblemHint, activity.getString(android.R.string.ok), DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
            if (retry == true)
            {
              lifeCycle.refreshModelAndBind(null)
            }
            else
            {
              activity.finish()
            }
          }, activity.getString(android.R.string.no), if (retry == false)
            null
          else
            DialogInterface.OnClickListener { dialog, _ ->
              dialog.cancel()
              activity.finish()
            }, if (retry == false)
            null
          else
            DialogInterface.OnCancelListener { dialog ->
              dialog.dismiss()
              activity.finish()
            })

        }
      }
      return true
    }

    /**
     * Is invoked when a memory saturation issue has been detected, and display a dialog box if any.
     *
     * @param throwable the exception which is supposed to be related to an out-of-memory
     *
     * @return `true` if and only a memory saturation issue has been detected
     *
     * @see [SweetIssueAnalyzer.handleIssue]
     */
    protected open fun handleMemoryProblemInCause(throwable: Throwable): Boolean
    {
      submitToIssueAnalyzer(throwable)
      return false
    }

    /**
     * Is responsible for displaying a dialog box. This enables to customize in a centralized way the dialog boxes look &amp; feel.
     * <p>
     * It is ensured that the framework will invoke that method from the UI thread, and when invoking that method directly, this must be done from the
     * UI thread as well.
     * </p>
     *
     * @param activity              the activity which is bound to pop up the dialog box
     * @param dialogTitle           the dialog box title
     * @param dialogMessage         the dialog box message
     * @param positiveButton        the label to display for the dialog box positive button ; may be `null` if `positiveClickListener` is also `null`
     * @param positiveClickListener the callback which will be invoked from the UI thread when the end-user hits the positive button
     * @param negativeButton        the label to display for the dialog box positive button ; may be `null` if `negativeClickListener` is also `null`
     * @param negativeClickListener the callback which will be invoked from the UI thread when the end-user hits the negative button ; may be `null`, and in that
     *                              case, the "No" button is hidden
     * @param onCancelListener      the callback which will be invoked from the UI thread when the end-user hits the "back" button ; may be `null`, and in that
     *                              case, the dialog box will not be cancelable}
     *
     * @see showDialog
     */
    protected open fun onShowDialog(activity: AppCompatActivity, dialogTitle: CharSequence, dialogMessage: CharSequence, positiveButton: CharSequence?, positiveClickListener: DialogInterface.OnClickListener?, negativeButton: CharSequence?,
                                    negativeClickListener: DialogInterface.OnClickListener?, onCancelListener: DialogInterface.OnCancelListener?)
    {
      val builder = AlertDialog.Builder(activity).setTitle(dialogTitle).setIcon(android.R.drawable.ic_dialog_alert).setMessage(dialogMessage)

      positiveClickListener?.let {
        builder.setPositiveButton(positiveButton, positiveClickListener)
      }

      builder.setCancelable(if (onCancelListener == null) false else true)

      if (onCancelListener != null)
      {
        builder.setOnCancelListener(onCancelListener)
      }

      if (negativeClickListener != null)
      {
        builder.setNegativeButton(negativeButton, negativeClickListener)
      }

      builder.show()
    }

  }

  /**
   * A default implementation.
   */
  open class DefaultExceptionHandler(i18n: SweetApplication.I18N, issueAnalyzer: SweetIssueAnalyzer)
    : AbstractExceptionHandler(i18n, issueAnalyzer)

}