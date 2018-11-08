package com.hagergroup.sweetpotato.app

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
class Sweetizer<AggregateClass : Any, ComponentClass : Any>(val activity: FragmentActivity,
                                                            val sweetable: Sweetable<AggregateClass>,
                                                            val component: ComponentClass,
                                                            val fragment: Fragment?)
  : Sweetable<AggregateClass>
{

  init
  {
    Timber.d("Creating the Sweetizer for Activity belonging to class '${activity.javaClass.name}' and with Fragment belonging to class '${fragment?.javaClass?.name}'")
  }

  private val stateContainer by lazy { StateContainer<AggregateClass, ComponentClass>(activity, component) }

  private val coroutineJob by lazy { Job() }

  private val coroutineScope by lazy { CoroutineScope(Dispatchers.Main + coroutineJob) }

  override suspend fun onRetrieveViewModel()
  {
    sweetable.onRetrieveViewModel()
  }

  override fun onBindViewModel()
  {
    sweetable.onBindViewModel()
  }

  override fun getAggregate(): AggregateClass? =
      stateContainer.aggregate

  override fun setAggregate(aggregate: AggregateClass)
  {
    stateContainer.aggregate = aggregate
  }

  override fun getHandler(): Handler =
      stateContainer.handler

  override fun onException(throwable: Throwable, fromGuiThread: Boolean)
  {
    SweetActivityController.handleException(true, activity, fragment, throwable)
  }

  override fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    stateContainer.registerBroadcastListeners(broadcastListeners)
  }

  override fun refreshViewModelAndBind(onOver: Runnable?)
  {
    if (stateContainer.isAliveAsWellAsHostingActivity() == false)
    {
      // In that case, we skip the processing
      return
    }

    if (stateContainer.shouldDelayRefreshBusinessObjectsAndDisplay(onOver) == true)
    {
      return
    }

    if (stateContainer.isAliveAsWellAsHostingActivity() == false)
    {
      // In that case, we skip the processing
      return
    }

    stateContainer.onRefreshingViewModelAndBindingStart()

    if (onRetrieveViewModelInternal() == false)
    {
      return
    }

    onBindViewModelInternal(onOver)
  }

  override fun isRefreshingViewModelAndBinding(): Boolean =
      stateContainer.isRefreshingViewModelAndBinding()

  override fun isFirstLifeCycle(): Boolean =
      stateContainer.firstLifeCycle

  override fun isInteracting(): Boolean =
      stateContainer.isInteracting

  override fun isAlive(): Boolean =
      stateContainer.isAlive

  override fun shouldKeepOn(): Boolean =
      stateContainer.shouldKeepOn()

  fun refreshViewModelAndBind()
  {
    refreshViewModelAndBind(null)
  }

  fun onCreate(superMethod: Runnable, savedInstanceState: Bundle?)
  {
    Timber.d("Sweetizer::onCreate")

    superMethod.run()

    if (isFragment() == false && SweetActivityController.needsRedirection(activity) == true)
    {
      // We stop here if a redirection is needed
      stateContainer.beingRedirected()

      return
    }
    else
    {
      SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_CREATE)
    }

    stateContainer.firstLifeCycle = if (StateContainer.isFirstCycle(savedInstanceState) == true) false else true

    stateContainer.registerBroadcastListeners()
  }

  fun onNewIntent()
  {
    Timber.d("Sweetizer::onNewIntent")

    if (isFragment() == false && SweetActivityController.needsRedirection(activity) == true)
    {
      // We stop here if a redirection is needed
      stateContainer.beingRedirected()
    }
  }

  fun onResume()
  {
    Timber.d("Sweetizer::onResume")

    if (shouldKeepOn() == false)
    {
      return
    }

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_RESUME)

    stateContainer.onResume()

    refreshViewModelAndBindInternal()
  }

  fun onSaveInstanceState(outState: Bundle)
  {
    Timber.d("Sweetizer::onSaveInstanceState")

    stateContainer.onSaveInstanceState(outState)
  }

  fun onStart()
  {
    Timber.d("Sweetizer::onStart")

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_START)
  }

  fun onPause()
  {
    Timber.d("Sweetizer::onPause")

    if (shouldKeepOn() == false)
    {
      // We stop here if a redirection is needed or is something went wrong
      return
    }

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_PAUSE)
    stateContainer.onPause()
  }

  fun onStop()
  {
    Timber.d("Sweetizer::onStop")

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_STOP)
  }

  fun onDestroy()
  {
    Timber.d("Sweetizer::onDestroy")

    coroutineJob.cancel()
    stateContainer.onDestroy()

    if (shouldKeepOn() == false)
    {
      // We stop here if a redirection is needed or is something went wrong
      return
    }

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_DESTROY)
  }

  private fun onRetrieveViewModelInternal(): Boolean
  {
    return runBlocking {
      try
      {
        onBeforeRefreshViewModelAndBind()

        if (stateContainer.isAliveAsWellAsHostingActivity() == false)
        {
          // If the entity is no more alive, we give up the process
          false
        }
        else
        {
          coroutineScope.launch {
            onRetrieveViewModel()
          }

          // We notify the entity that the business objects have actually been loaded
          if (stateContainer.isAliveAsWellAsHostingActivity() == false)
          {
            // If the entity is no more alive, we give up the process
            false
          }
          else
          {
            stateContainer.viewModelRetrieved()
            true
          }
        }
      }
      catch (throwable: Throwable)
      {
        stateContainer.onRefreshingViewModelAndBindingStop(this@Sweetizer)

        // We check whether the issue does not come from a non-alive entity
        if (stateContainer.isAliveAsWellAsHostingActivity() == false)
        {
          // In that case, we just ignore the exception: it is very likely that the entity or the hosting Activity have turned as non-alive
          // during the "onRetrieveBusinessObjects()" method!
          false
        }
        else
        {
          // Otherwise, we report the exception
          onInternalViewModelAvailableException(throwable)

          false
        }
      }
    }
  }

  private fun onBeforeRefreshViewModelAndBind()
  {
    stateContainer.onStartLoading()
  }

  private fun onBindViewModelInternal(onOver: Runnable?)
  {
    if (stateContainer.resumedForTheFirstTime == true)
    {
      try
      {
        onBindViewModel()
      }
      catch (throwable: Throwable)
      {
        stateContainer.onRefreshingViewModelAndBindingStop(this)
        sweetable.onException(throwable, true)
        stateContainer.onStopLoading()

        return
      }
    }

    stateContainer.onStopLoading()
    stateContainer.markNotResumedForTheFirstTime()

    if (onOver != null)
    {
      try
      {
        onOver.run()
      }
      catch (throwable: Throwable)
      {
        Timber.e(throwable, "An exception occurred while executing the 'refreshViewModelAndBind()' runnable!")
      }

    }
    stateContainer.onRefreshingViewModelAndBindingStop(this)
  }

  private fun refreshViewModelAndBindInternal()
  {
    sweetable.refreshViewModelAndBind(stateContainer.getRetrieveViewModelOver())
  }

  private fun onInternalViewModelAvailableException(throwable: Throwable)
  {
    Timber.e(throwable, "Cannot retrieve the view model")

    stateContainer.onStopLoading()

    if (stateContainer.onInternalViewModelAvailableExceptionWorkAround(throwable) == true)
    {
      return
    }

    // We need to indicate to the method that it may have been triggered from another thread than the GUI's
    sweetable.onException(throwable, false)
  }

  private fun isFragment(): Boolean =
      activity != sweetable

}