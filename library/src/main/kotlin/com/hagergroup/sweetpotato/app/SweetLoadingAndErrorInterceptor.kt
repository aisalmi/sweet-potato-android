package com.hagergroup.sweetpotato.app

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.SweetLoadingAndErrorAnnotation
import com.hagergroup.sweetpotato.content.LoadingBroadcastListener
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ViewModelUnavailableException
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.full.findAnnotation

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetLoadingAndErrorInterceptor
  : SweetActivityController.Interceptor
{

  interface ViewModelUnavailableReporter<FragmentAggregateClass : SweetFragmentAggregate>
  {

    fun reportViewModelUnavailableException(fragment: Sweetable<FragmentAggregateClass>, viewModelUnavailableException: ViewModelUnavailableException)
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

    fun getViewModelUnavailableExceptionKeeper(): ViewModelUnavailableExceptionKeeper

  }

  interface DoNotHideLoadingNextTime

  interface ErrorAndRetryManager
  {

    fun showError(activity: FragmentActivity, throwable: Throwable, fromGuiThread: Boolean, onCompletion: Runnable)

    fun hide()

  }

  class ViewModelUnavailableExceptionKeeper
  {

    companion object
    {

      private val VIEW_MODEL_UNAVAILABLE_EXCEPTION_EXTRA = "viewModelUnavailableExceptionExtra"
    }

    var exception: ViewModelUnavailableException? = null

    @Throws(ViewModelUnavailableException::class)
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
      exception = bundle?.getSerializable(ViewModelUnavailableExceptionKeeper.VIEW_MODEL_UNAVAILABLE_EXCEPTION_EXTRA) as? ViewModelUnavailableException
    }

    fun onSaveInstanceState(bundle: Bundle)
    {
      if (exception != null)
      {
        bundle.putSerializable(ViewModelUnavailableExceptionKeeper.VIEW_MODEL_UNAVAILABLE_EXCEPTION_EXTRA, exception)
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

    fun handleLoading(errorAndRetryManagerProvider: ErrorAndRetryManagerProvider, isLoading: Boolean, issue: AtomicReference<ViewModelUnavailableException>)
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

    fun showIssue(activity: FragmentActivity, throwable: Throwable, onCompletion: Runnable?)
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

    private val issue = AtomicReference<ViewModelUnavailableException>()

    private var displayLoadingViewNextTime = true

    private var loadingErrorAndRetryAttributes: LoadingErrorAndRetryAttributes? = null

    fun onCreate(errorAndRetryAttributesProvider: ErrorAndRetryManagerProvider, activity: FragmentActivity, sweetable: Sweetable<*>, issue: ViewModelUnavailableException?, handleLoading: Boolean)
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

    fun showViewModelUnavailableException(activity: FragmentActivity, sweetable: Sweetable<*>, exception: ViewModelUnavailableException)
    {
      loadingErrorAndRetryAttributes?.showIssue(activity, exception, Runnable {
        sweetable.refreshViewModelAndBind(null)
      })
    }

    fun showViewModelUnavailableException(activity: FragmentActivity, exception: ViewModelUnavailableException, runnable: Runnable)
    {
      loadingErrorAndRetryAttributes?.showIssue(activity, exception, runnable)
    }

    fun showException(activity: FragmentActivity, throwable: Throwable, onRetry: Runnable)
    {
      loadingErrorAndRetryAttributes?.showIssue(activity, throwable, onRetry)
    }

    fun reportViewModelUnavailableException(exception: ViewModelUnavailableException)
    {
      issue.set(exception)
    }

  }

  private val errorAndRetryAttributesProvider = getErrorAndRetryAttributesProvider()

  protected abstract fun getErrorAndRetryAttributesProvider(): ErrorAndRetryManagerProvider

  override fun onLifeCycleEvent(activity: FragmentActivity?, fragment: Fragment?, event: Lifecycle.Event)
  {
    activity?.let {
      val actualComponent = fragment ?: activity

      if (actualComponent is Sweetable<*>)
      {
        // It's a Fragment or an Activity
        val sweetable = actualComponent as Sweetable<LoadingErrorAndRetryAggregateProvider>

        // We handle the loading, error and retry feature, but not with the DisableLoadingAndErrorInterceptor-annotated Fragments
        val loadingAndErrorAnnotation = sweetable::class.findAnnotation<SweetLoadingAndErrorAnnotation>()

        if (loadingAndErrorAnnotation?.enabled == true && (event == Lifecycle.Event.ON_CREATE || event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_PAUSE))
        {
          val aggregate = sweetable.getAggregate()?.getLoadingErrorAndRetryAggregate()
          val businessObjectsUnavailableExceptionKeeper = sweetable.getAggregate()?.getViewModelUnavailableExceptionKeeper()

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

}