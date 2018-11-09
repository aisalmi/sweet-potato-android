package com.hagergroup.sweetpotato.exception

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle
import org.jetbrains.anko.toast
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
class ExceptionHandlers private constructor()
{

  //TODO : rework the I18N and the "showdialog" part
  open class AbstractExceptionHandler(protected val i18n: SweetApplication.I18N, protected val issueAnalyzer: SweetIssueAnalyzer)
    : SweetExceptionHandler
  {

    enum class ConnectivityUIExperience
    {

      DialogRetry, Dialog, Toast
    }

    override fun onModelUnavailableException(activity: FragmentActivity?, fragment: Fragment?, exception: ModelUnavailableException): Boolean
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

    override fun onActivityException(activity: FragmentActivity?, fragment: Fragment?, throwable: Throwable): Boolean
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

    protected open fun onModelUnavailableExceptionFallback(activity: FragmentActivity?, fragment: Fragment?, exception: ModelUnavailableException): Boolean
    {
      showDialog(activity, i18n.dialogBoxErrorTitle, i18n.businessObjectAvailabilityProblemHint, activity?.getString(android.R.string.ok) ?: "ok", DialogInterface.OnClickListener { dialog, _ ->
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

    protected open fun onActivityExceptionFallback(activity: FragmentActivity?, fragment: Fragment?, throwable: Throwable): Boolean
    {
      showDialog(activity, i18n.dialogBoxErrorTitle, i18n.otherProblemHint, activity?.getString(android.R.string.ok) ?: "ok", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()

        // We leave the activity, because we cannot go any further
        activity?.finish()
      }, null, null, null)
      submitToIssueAnalyzer(throwable)
      return true
    }

    protected open fun onContextExceptionFallback(isRecoverable: Boolean, context: Context?, throwable: Throwable): Boolean
    {
      submitToIssueAnalyzer(throwable)
      return false
    }

    protected open fun onExceptionFallback(isRecoverable: Boolean, throwable: Throwable): Boolean
    {
      submitToIssueAnalyzer(throwable)
      return false
    }

    protected open fun handleCommonCauses(activity: FragmentActivity?, fragment: Fragment?, throwable: Throwable, connectivityUIExperience: ConnectivityUIExperience?): Boolean
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

    protected open fun showDialog(activity: FragmentActivity?, dialogTitle: CharSequence, dialogMessage: CharSequence, positiveButton: CharSequence,
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

    protected open fun handleOtherCauses(activity: FragmentActivity?, fragment: Fragment?, throwable: Throwable): Boolean =
        false

    protected open fun handleConnectivityProblemInCause(activity: FragmentActivity?, fragment: Fragment?, throwable: Throwable, connectivityUIExperience: ConnectivityUIExperience?): Boolean
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
          showDialog(activity, i18n.dialogBoxErrorTitle, if (retry == true) i18n.connectivityProblemRetryHint else i18n.connectivityProblemHint, activity.getString(android.R.string.ok), DialogInterface.OnClickListener { dialog, _ ->
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

    protected open fun handleMemoryProblemInCause(throwable: Throwable): Boolean
    {
      submitToIssueAnalyzer(throwable)
      return false
    }

    protected open fun onShowDialog(activity: FragmentActivity, dialogTitle: CharSequence, dialogMessage: CharSequence, positiveButton: CharSequence, positiveClickListener: DialogInterface.OnClickListener, negativeButton: CharSequence?,
                                    negativeClickListener: DialogInterface.OnClickListener?, onCancelListener: DialogInterface.OnCancelListener?)
    {
      val builder = AlertDialog.Builder(activity).setTitle(dialogTitle).setIcon(android.R.drawable.ic_dialog_alert).setMessage(dialogMessage).setPositiveButton(positiveButton, positiveClickListener)
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

  open class DefaultExceptionHandler(i18n: SweetApplication.I18N, issueAnalyzer: SweetIssueAnalyzer)
    : AbstractExceptionHandler(i18n, issueAnalyzer)

}