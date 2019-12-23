package com.hagergroup.sample

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sample.app.SampleConnectivityListener
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sample.app.SampleInterceptor
import com.hagergroup.sample.exception.CallException
import com.hagergroup.sweetpotato.app.SweetActivityController
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.app.SweetConnectivityListener
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.appcompat.app.SweetSplashscreenActivity
import com.hagergroup.sweetpotato.exception.ExceptionHandlers
import com.hagergroup.sweetpotato.exception.SweetExceptionHandler
import com.hagergroup.sweetpotato.exception.SweetIssueAnalyzer
import com.hagergroup.sweetpotato.exception.searchForCause
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.log.SweetLogTree
import timber.log.Timber
import java.net.HttpURLConnection

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleApplication
  : SweetApplication<SweetApplication.ApplicationConstants>()
{

  override fun setupTimber()
  {
    if (BuildConfig.DEBUG == true)
    {
      Timber.plant(Timber.DebugTree())
    }
    else
    {
      Timber.plant(SweetLogTree())
    }
  }

  override fun getI18N(): SweetApplication.I18N =
      SweetApplication.I18N(getString(R.string.error), getString(R.string.businessProblem), getString(R.string.connectivityProblem), getString(R.string.unavailableService))

  override fun retrieveConnectivityListener(): SweetConnectivityListener =
      SampleConnectivityListener(this)

  override fun onSetupExceptionHandlers()
  {
    //Init Crashlytics here
  }

  override fun getActivityRedirector(): SweetActivityController.Redirector
  {
    return object : SweetActivityController.Redirector
    {

      override fun getRedirection(activity: AppCompatActivity): Intent?
      {
        return if (SweetSplashscreenActivity.isInitialized(SampleSplashscreenActivity::class) == null && activity is SampleSplashscreenActivity == false)
        {
          Intent(activity, SampleSplashscreenActivity::class.java)
        }
        else
        {
          null
        }
      }

    }
  }

  override fun getInterceptor(): SweetActivityController.Interceptor
  {
    val applicationInterceptor = SampleInterceptor()
    val loadingAndErrorInterceptor = object : SweetLoadingAndErrorInterceptor()
    {

      override fun getErrorAndRetryAttributesProvider(): SweetLoadingAndErrorInterceptor.ErrorAndRetryManagerProvider
      {

        return object : SweetLoadingAndErrorInterceptor.ErrorAndRetryManagerProvider
        {

          override fun getErrorAndRetryManager(view: View): SweetLoadingAndErrorInterceptor.ErrorAndRetryManager =
              SampleActivityAggregate.SampleErrorAndRetryManager(view)

          override fun getLoadingAndRetryView(view: View): View? =
              view.findViewById(R.id.loadingErrorAndRetry)

          override fun getLoadingView(view: View): View? =
              view.findViewById(R.id.loading)

          override fun getProgressBar(view: View): View =
              view.findViewById(R.id.progressBar)

          override fun getTextView(view: View): TextView =
              view.findViewById(R.id.text)

          override fun getErrorAndRetryView(view: View): View? =
              view.findViewById(R.id.errorAndRetry)

          override fun getErrorText(context: Context?): String =
              ""

          override fun getLoadingText(context: Context?): String =
              ""

        }
      }

    }

    return object : SweetActivityController.Interceptor
    {
      override fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)
      {
        applicationInterceptor.onLifeCycleEvent(activity, fragment, event)
        loadingAndErrorInterceptor.onLifeCycleEvent(activity, fragment, event)
        connectivityListener?.onLifeCycleEvent(activity, fragment, event)
      }

    }

  }

  override fun getExceptionHandler(): SweetExceptionHandler
  {

    return object : ExceptionHandlers.DefaultExceptionHandler(getI18N(), SweetIssueAnalyzer.DefaultIssueAnalyzer(applicationContext))
    {

      override fun onActivityExceptionFallback(activity: AppCompatActivity, fragment: Fragment?, throwable: Throwable): Boolean
      {
        reportIssueIfNecessary(true, throwable)
        return super.onActivityExceptionFallback(activity, fragment, throwable)
      }

      override fun onModelUnavailableExceptionFallback(activity: AppCompatActivity?, fragment: Fragment?, exception: ModelUnavailableException): Boolean
      {
        (fragment as? Sweetable<SampleFragmentAggregate>)?.let {
          if (activity is SampleActivity)
          {
            // We focus on Fragments
            if (activity is SweetLoadingAndErrorInterceptor.ModelUnavailableReporter<*>)
            {
              activity.reportModelUnavailableException(it, exception)
            }
            else if (it.getAggregate() is SampleFragmentAggregate)
            {
              activity.getHandler().post {
                it.getAggregate()?.showModelUnavailableException(activity, it, exception)
              }
            }

            return true
          }
        }

        if (checkNotFoundException(fragment, exception))
        {
          return true
        }

        reportIssueIfNecessary(true, exception)

        return super.onModelUnavailableExceptionFallback(activity, fragment, exception)
      }

      override fun handleConnectivityProblemInCause(activity: AppCompatActivity?, fragment: Fragment?, throwable: Throwable, connectivityUIExperience: ConnectivityUIExperience?): Boolean
      {
        val exceptionCause = throwable.searchForCause(ModelUnavailableException::class)

        return if (exceptionCause != null)
        {
          // We handle this connectivity issue has BusinessObjectAvailableException to display better error interface.
          onModelUnavailableExceptionFallback(activity, fragment, exceptionCause as ModelUnavailableException)
        }
        else
        {
          super.handleConnectivityProblemInCause(activity, fragment, throwable, connectivityUIExperience)
        }
      }

      override fun onContextExceptionFallback(isRecoverable: Boolean, context: Context?, throwable: Throwable): Boolean
      {
        reportIssueIfNecessary(isRecoverable, throwable)
        return super.onContextExceptionFallback(isRecoverable, context, throwable)
      }

      override fun onExceptionFallback(isRecoverable: Boolean, throwable: Throwable): Boolean
      {
        reportIssueIfNecessary(isRecoverable, throwable)
        return super.onExceptionFallback(isRecoverable, throwable)
      }

      override fun handleOtherCauses(activity: AppCompatActivity?, fragment: Fragment?, throwable: Throwable): Boolean
      {
        if (checkDetachedFragmentProblem(fragment, throwable))
        {
          return true
        }

        return super.handleOtherCauses(activity, fragment, throwable)
      }

      override fun reportIssueIfNecessary(isRecoverable: Boolean, throwable: Throwable)
      {
        if (isRecoverable == false)
        {
          return
        }

        //TODO : log exception in Crashlytics
      }

      private fun checkDetachedFragmentProblem(fragment: Fragment?, throwable: Throwable?): Boolean =
          fragment != null && throwable.searchForCause(IllegalStateException::class) != null

      private fun checkNotFoundException(fragment: Fragment?, exception: ModelUnavailableException): Boolean
      {
        val callException = exception.searchForCause(CallException::class) as? CallException

        if (callException != null && callException.code >= HttpURLConnection.HTTP_BAD_REQUEST && callException.code < HttpURLConnection.HTTP_INTERNAL_ERROR)
        {
          // This is a 40X exception
          if (fragment is Sweetable<*>)
          {
            (fragment as? Sweetable<SampleFragmentAggregate>)?.getAggregate()?.getLoadingErrorAndRetryAggregate()?.reportModelUnavailableException(exception)

            return true
          }
        }

        return false
      }
    }
  }

}

