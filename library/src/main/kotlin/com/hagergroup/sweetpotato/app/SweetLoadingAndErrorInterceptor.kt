package com.hagergroup.sweetpotato.app

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.SweetLoadingAndErrorAnnotation
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider
import com.hagergroup.sweetpotato.content.LoadingBroadcastListener
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import java.util.concurrent.atomic.AtomicReference

/**
 * An interceptor which is responsible for handling two things, very common in applications:
 * <ol>
 * <li>the graphical indicators while an entity [AppCompatActivity] or [Fragment] is being loaded: on that purpose, you need to
 * declare the entity [com.hagergroup.sweetpotato.annotation.SweetSendLoadingIntentAnnotation] annotation, so that loading events are triggered;</li>
 * <li>the display of errors.</li>
 * </ol>
 * <p>
 * Caution: in order to have this interceptor working, you need to make sure that the entity (deriving hence from [Sweetable]) uses a template
 * type implementing the [LoadingErrorAndRetryAggregateProvider] interface.
 * </p>
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetLoadingAndErrorInterceptor
  : SweetActivityController.Interceptor
{

  interface ModelUnavailableReporter<FragmentAggregateClass : SweetFragmentAggregate>
  {

    fun reportModelUnavailableException(fragment: Sweetable<FragmentAggregateClass>, modelUnavailableException: ModelUnavailableException)
  }

  interface ErrorAndRetryManagerProvider
  {

    fun getErrorAndRetryManager(view: View): ErrorAndRetryManager

    fun getLoadingAndRetryView(view: View): View?

    fun getLoadingView(view: View): View?

    fun getProgressBar(view: View): View?

    fun getTextView(view: View): TextView?

    fun getErrorAndRetryView(view: View): View?

    fun getErrorText(context: Context?): String

    fun getLoadingText(context: Context?): String

  }

  interface LoadingErrorAndRetryAggregateProvider
  {

    fun getLoadingErrorAndRetryAggregate(): LoadingErrorAndRetryAggregate

    fun getModelUnavailableExceptionKeeper(): ModelUnavailableExceptionKeeper

  }

  interface DoNotHideLoadingNextTime

  interface ErrorAndRetryManager
  {

    fun showError(activity: AppCompatActivity, throwable: Throwable, fromGuiThread: Boolean, onCompletion: Runnable)

    fun hide()

  }

  class ModelUnavailableExceptionKeeper
  {

    companion object
    {

      private val MODEL_UNAVAILABLE_EXCEPTION_EXTRA = "modelUnavailableExceptionExtra"
    }

    var exception: ModelUnavailableException? = null

    @Throws(ModelUnavailableException::class)
    fun checkException()
    {
      val newException = exception

      if (newException != null)
      {
        throw newException
      }
    }

    fun onRestoreInstanceState(bundle: Bundle?)
    {
      exception = bundle?.getSerializable(ModelUnavailableExceptionKeeper.MODEL_UNAVAILABLE_EXCEPTION_EXTRA) as? ModelUnavailableException
    }

    fun onSaveInstanceState(bundle: Bundle)
    {
      if (exception != null)
      {
        bundle.putSerializable(ModelUnavailableExceptionKeeper.MODEL_UNAVAILABLE_EXCEPTION_EXTRA, exception)
      }
    }

  }

  private class LoadingErrorAndRetryAttributes(view: View, errorAndRetryManagerProvider: ErrorAndRetryManagerProvider)
  {

    private val containerView: View?

    private val loadingView: View?

    private val errorAndRetryManager: ErrorAndRetryManager?

    private var progressBar: View? = null

    private var text: TextView? = null

    private var isHandlingError: Boolean = false

    init
    {
      val loadingErrorAndRetry = errorAndRetryManagerProvider.getLoadingAndRetryView(view)
      containerView = loadingErrorAndRetry ?: errorAndRetryManagerProvider.getLoadingView(view)

      if (containerView != null)
      {
        loadingView = errorAndRetryManagerProvider.getLoadingView(containerView)

        if (loadingView != null)
        {
          progressBar = errorAndRetryManagerProvider.getProgressBar(loadingView)
          text = errorAndRetryManagerProvider.getTextView(loadingView)
        }

        val errorAndRetryView = errorAndRetryManagerProvider.getErrorAndRetryView(containerView)

        if (errorAndRetryView != null)
        {
          errorAndRetryManager = errorAndRetryManagerProvider.getErrorAndRetryManager(errorAndRetryView)
          errorAndRetryManager.hide()
          errorAndRetryView.isVisible = false
        }
        else
        {
          errorAndRetryManager = null
        }
      }
      else
      {
        loadingView = null
        errorAndRetryManager = null
      }
    }

    fun showLoading()
    {
      setLoadingVisible()
    }

    fun hideLoading(context: Context)
    {
      containerView?.let {
        if (isHandlingError == false)
        {
          val animationListener = object : Animation.AnimationListener
          {

            override fun onAnimationRepeat(animation: Animation?)
            {
            }

            override fun onAnimationStart(animation: Animation?)
            {
            }

            override fun onAnimationEnd(animation: Animation)
            {
              loadingView?.isVisible = false

              // While the animation is played, an error may have been declared
              containerView.isVisible = isHandlingError
            }
          }

          val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
          animation.setAnimationListener(animationListener)

          // If the loading view contains tag and that this tag implements the 'DoNotHideLoadingNextTime' interface, we do not start the hide animation
          if (loadingView?.tag is DoNotHideLoadingNextTime == false)
          {
            containerView.startAnimation(animation)
          }
          else
          {
            loadingView?.tag = null
          }
        }
        else
        {
          loadingView?.isVisible = false
        }
      }
    }

    fun handleLoading(errorAndRetryManagerProvider: ErrorAndRetryManagerProvider, isLoading: Boolean, issue: AtomicReference<ModelUnavailableException>)
    {
      loadingView?.let {
        if (hasErrorAndRetryView() == false && issue.get() != null && isLoading == false)
        {
          issue.set(null)

          progressBar?.isVisible = false
          text?.text = errorAndRetryManagerProvider.getErrorText(text?.context)

          setLoadingVisible()
        }
        else
        {
          if (hasErrorAndRetryView() == false && isLoading == true)
          {
            progressBar?.isVisible = true
            text?.text = errorAndRetryManagerProvider.getLoadingText(text!!.context)
          }
          if (isLoading == true)
          {
            setLoadingVisible()
          }
          else
          {
            hideLoading(loadingView.context)
          }
        }
      }
    }

    fun showIssue(activity: AppCompatActivity, throwable: Throwable, onCompletion: Runnable?)
    {
      isHandlingError = true

      errorAndRetryManager?.let {
        errorAndRetryManager.showError(activity, throwable, true, Runnable {
          isHandlingError = false

          containerView?.post {
            containerView.isVisible = false
          }

          onCompletion?.run()
        })

        containerView?.post {
          containerView.isVisible = true
        }
      }
    }

    private fun setLoadingVisible()
    {
      if (loadingView != null)
      {
        loadingView.isVisible = true
        containerView?.isVisible = true
      }
      else
      {
        containerView?.isVisible = false
      }
    }

    private fun hasErrorAndRetryView(): Boolean =
        errorAndRetryManager != null

  }

  class LoadingErrorAndRetryAggregate
  {

    private val issue = AtomicReference<ModelUnavailableException>()

    private var displayLoadingViewNextTime = true

    private var loadingErrorAndRetryAttributes: LoadingErrorAndRetryAttributes? = null

    fun onCreate(errorAndRetryAttributesProvider: ErrorAndRetryManagerProvider, activity: AppCompatActivity, sweetable: Sweetable<*>, issue: ModelUnavailableException?, handleLoading: Boolean)
    {
      this.issue.set(issue)

      if (handleLoading == true)
      {
        displayLoadingViewNextTime = sweetable.isFirstLifeCycle() == true

        val loadingBroadcastListener = object : LoadingBroadcastListener(activity, sweetable)
        {

          override fun onLoading(isLoading: Boolean)
          {
            if (displayLoadingViewNextTime == true)
            {
              loadingErrorAndRetryAttributes?.handleLoading(errorAndRetryAttributesProvider, isLoading, this@LoadingErrorAndRetryAggregate.issue)
            }
            else
            {
              // We ignore the loading effect, until we receive a first "stop loading" event
              if (isLoading == false)
              {
                displayLoadingViewNextTime = true
              }
            }
          }
        }

        sweetable.registerBroadcastListeners(arrayOf(loadingBroadcastListener))
      }
    }

    fun onStart(errorAndRetryAttributesProvider: ErrorAndRetryManagerProvider, view: View?)
    {
      if (loadingErrorAndRetryAttributes == null)
      {
        loadingErrorAndRetryAttributes = if (view != null) LoadingErrorAndRetryAttributes(view, errorAndRetryAttributesProvider) else null
      }
    }

    fun onPause()
    {
      doNotDisplayLoadingViewNextTime()
    }

    fun doNotDisplayLoadingViewNextTime()
    {
      displayLoadingViewNextTime = false
    }

    fun showModelUnavailableException(activity: AppCompatActivity, sweetable: Sweetable<*>, exception: ModelUnavailableException)
    {
      loadingErrorAndRetryAttributes?.showIssue(activity, exception, Runnable {
        sweetable.refreshModelAndBind(null)
      })
    }

    fun showModelUnavailableException(activity: AppCompatActivity, exception: ModelUnavailableException, runnable: Runnable)
    {
      loadingErrorAndRetryAttributes?.showIssue(activity, exception, runnable)
    }

    fun showException(activity: AppCompatActivity, throwable: Throwable, onRetry: Runnable)
    {
      loadingErrorAndRetryAttributes?.showIssue(activity, throwable, onRetry)
    }

    fun reportModelUnavailableException(exception: ModelUnavailableException)
    {
      issue.set(exception)
    }

  }

  private val errorAndRetryAttributesProvider = getErrorAndRetryAttributesProvider()

  protected abstract fun getErrorAndRetryAttributesProvider(): ErrorAndRetryManagerProvider

  override fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)
  {
    val actualComponent = fragment ?: activity

    if (actualComponent is Sweetable<*>)
    {
      // It's a Fragment or an Activity
      val sweetable = actualComponent as Sweetable<LoadingErrorAndRetryAggregateProvider>

      // We handle the loading, error and retry feature, but not with the DisableLoadingAndErrorInterceptor-annotated Fragments
      val loadingAndErrorAnnotation = sweetable::class.java.getAnnotation(SweetLoadingAndErrorAnnotation::class.java)

      if (loadingAndErrorAnnotation?.enabled == true && (event == Lifecycle.Event.ON_CREATE || event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_PAUSE))
      {
        val aggregate = sweetable.getAggregate()?.getLoadingErrorAndRetryAggregate()
        val businessObjectsUnavailableExceptionKeeper = sweetable.getAggregate()?.getModelUnavailableExceptionKeeper()

        if (event == Lifecycle.Event.ON_CREATE)
        {
          aggregate?.onCreate(errorAndRetryAttributesProvider, activity, sweetable, businessObjectsUnavailableExceptionKeeper?.exception, loadingAndErrorAnnotation.loadingEnabled)
        }
        else if (event == Lifecycle.Event.ON_START)
        {
          val view = fragment?.view ?: activity.findViewById(android.R.id.content)

          aggregate?.onStart(errorAndRetryAttributesProvider, view)
        }
        else if (event == Lifecycle.Event.ON_PAUSE)
        {
          aggregate?.onPause()
        }
      }
    }
  }

}