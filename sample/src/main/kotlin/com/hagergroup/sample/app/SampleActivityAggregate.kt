package com.hagergroup.sample.app

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.hagergroup.sample.R
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.exception.isAConnectivityProblem
import kotlinx.android.synthetic.main.loading_error_and_retry.view.*

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleActivityAggregate(activity: FragmentActivity, activityAnnotation: SweetActivityAnnotation?)
  : SweetActivityAggregate(activity, activityAnnotation)
{

  class SampleErrorAndRetryManager(view: View)
    : SweetLoadingAndErrorInterceptor.ErrorAndRetryManager
  {

    private val errorText: TextView = view.errorText

    private val retry: View = view.retry

    private val errorAndRetry: View = view.errorAndRetry

    override fun showError(activity: FragmentActivity, throwable: Throwable, fromGuiThread: Boolean, onCompletion: Runnable)
    {
      if (throwable.isAConnectivityProblem() == true)
      {
        errorText.setText(R.string.connectivityProblem)
      }
      else
      {
        errorText.setText(R.string.unavailableService)
      }

      retry.setOnClickListener {
        onCompletion.run()
      }

      errorAndRetry.post {
        errorAndRetry.isVisible = true
      }
    }

    override fun hide()
    {
      errorAndRetry.isVisible = false
    }

  }

}