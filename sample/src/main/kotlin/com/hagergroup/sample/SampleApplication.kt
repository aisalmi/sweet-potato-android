package com.hagergroup.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sample.app.SampleConnectivityListener
import com.hagergroup.sample.app.SampleInterceptor
import com.hagergroup.sweetpotato.app.SweetActivityController
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.app.SweetConnectivityListener
import com.hagergroup.sweetpotato.appcompat.app.SweetSplashscreenActivity
import com.hagergroup.sweetpotato.exception.ExceptionHandlers
import com.hagergroup.sweetpotato.exception.SweetExceptionHandler
import com.hagergroup.sweetpotato.exception.SweetIssueAnalyzer
import com.hagergroup.sweetpotato.log.SweetLogTree
import timber.log.Timber

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
      SweetApplication.I18N(R.string.businessProblem, R.string.connectivityProblem, R.string.unavailableService)

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

    return object : SweetActivityController.Interceptor
    {
      override fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)
      {
        applicationInterceptor.onLifeCycleEvent(activity, fragment, event)
        connectivityListener?.onLifeCycleEvent(activity, fragment, event)
      }

    }

  }

  override fun getExceptionHandler(): SweetExceptionHandler
  {

    return object : ExceptionHandlers.DefaultExceptionHandler(getI18N(), SweetIssueAnalyzer.DefaultIssueAnalyzer(applicationContext))
    {

      override fun reportIssueIfNecessary(isRecoverable: Boolean, throwable: Throwable)
      {
        if (isRecoverable == false)
        {
          return
        }

        //TODO : log exception in Crashlytics
      }

    }
  }

}

