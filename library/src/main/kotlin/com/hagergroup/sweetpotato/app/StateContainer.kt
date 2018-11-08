package com.hagergroup.sweetpotato.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentActivity
import com.hagergroup.sweetpotato.annotation.SweetSendLoadingIntentAnnotation
import com.hagergroup.sweetpotato.content.LoadingBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider
import com.hagergroup.sweetpotato.content.SweetBroadcastListenersProvider
import com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle
import timber.log.Timber
import kotlin.reflect.full.findAnnotation

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class StateContainer<AggregateClass : Any, ComponentClass : Any>(private val activity: FragmentActivity, private val component: ComponentClass)
{

  private class RefreshViewModelAndBind(val onOver: Runnable?)
  {

    fun refreshViewModelAndBind(lifeCycleActivity: SweetLifeCycle)
    {
      lifeCycleActivity.refreshViewModelAndBind(onOver)
    }

  }

  companion object
  {

    private const val ALREADY_STARTED_EXTRA = "alreadyStartedExtra"

    private const val ILLEGAL_STATE_EXCEPTION_FRAGMENT_MESSAGE_SUFFIX = "not attached to Activity"

    fun isFirstCycle(savedInstanceState: Bundle?): Boolean =
        savedInstanceState?.containsKey(StateContainer.ALREADY_STARTED_EXTRA) ?: false

  }

  val handler by lazy { Handler() }

  var aggregate: AggregateClass? = null

  var firstLifeCycle = true

  var viewModelRetrieved = false
    private set

  var isInteracting = false
    private set

  var isAlive = true
    private set

  var resumedForTheFirstTime = true
    private set

  private var refreshingViewModelAndBindingCount = 0

  private var beingRedirected = false

  private var broadcastReceivers: Array<BroadcastReceiver?>? = null

  private var refreshViewModelAndBindNextTime: RefreshViewModelAndBind? = null

  private var refreshViewModelAndBindPending: RefreshViewModelAndBind? = null

  private fun registerBroadcastListeners(index: Int, broadcastListener: SweetBroadcastListener)
  {
    if (index == 0)
    {
      Timber.d("Registering for listening to intent broadcasts")
    }

    val broadcastReceiver = object : BroadcastReceiver()
    {

      override fun onReceive(context: Context?, intent: Intent?)
      {
        try
        {
          broadcastListener.onReceive(context, intent)
        }
        catch (throwable: Throwable)
        {
          Timber.e(throwable, "An exception occurred while handling a broadcast intent!")
        }
      }

    }

    broadcastReceivers?.set(index, broadcastReceiver)
    activity.registerReceiver(broadcastReceiver, broadcastListener.getIntentFilter())
  }

  private fun enrichBroadcastListeners(count: Int): Int
  {
    val newIndex: Int

    if (broadcastReceivers == null)
    {
      newIndex = 0
      broadcastReceivers = arrayOfNulls(count)
    }
    else
    {
      newIndex = broadcastReceivers?.size ?: 0

      val newBroadcastReceivers = arrayOfNulls<BroadcastReceiver>(count + (broadcastReceivers?.size ?: 0))

      broadcastReceivers?.indices?.forEach {
        newBroadcastReceivers[it] = broadcastReceivers?.get(it)
      }

      broadcastReceivers = newBroadcastReceivers
    }

    Timber.d("The entity is now able to welcome ${broadcastReceivers?.size} broadcast receiver(s)")

    return newIndex
  }

  fun isAliveAsWellAsHostingActivity(): Boolean =
      isAlive == true && activity.isFinishing == false

  fun registerBroadcastListeners()
  {
    if (component is SweetBroadcastListenersProvider)
    {
      val count = component.getBroadcastListenersCount()

      Timber.d("Found out that the entity supports $count intent broadcast listeners")

      val startIndex = enrichBroadcastListeners(count)

      for (index in 0 until count)
      {
        registerBroadcastListeners(startIndex + index, component.getBroadcastListener(index))
      }
    }
    else if (component is SweetBroadcastListenerProvider)
    {
      Timber.d("Found out that the entity supports a single intent broadcast listener")

      registerBroadcastListeners(enrichBroadcastListeners(1), component.getBroadcastListener())
    }
    else if (component is SweetBroadcastListener)
    {
      Timber.d("Found out that the entity implements a single intent broadcast listener")

      registerBroadcastListeners(enrichBroadcastListeners(1), component)
    }
  }

  fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    val startIndex = enrichBroadcastListeners(broadcastListeners.size)

    broadcastListeners.indices.forEach {
      registerBroadcastListeners(it + startIndex, broadcastListeners[it])
    }
  }

  fun unregisterBroadcastListeners()
  {
    broadcastReceivers?.let { broadcastReceiver ->
      broadcastReceiver.indices.reversed().forEach { indice ->
        broadcastReceiver[indice]?.let { currentBroadcastReceiver ->
          activity.unregisterReceiver(currentBroadcastReceiver)
        }
      }

      Timber.d("Stopped listening to ${broadcastReceivers?.size} intent broadcasts")
    }
  }

  fun onStartLoading()
  {
    if (component::class.findAnnotation<SweetSendLoadingIntentAnnotation>() != null)
    {
      // We indicate the activity which is loading, in order to filter the loading events
      LoadingBroadcastListener.broadcastLoading(activity, System.identityHashCode(activity), System.identityHashCode(component), true)
    }
  }

  fun onStopLoading()
  {
    if (component::class.findAnnotation<SweetSendLoadingIntentAnnotation>() != null)
    {
      // We indicate the activity which is loading, in order to filter the loading events
      LoadingBroadcastListener.broadcastLoading(activity, System.identityHashCode(activity), System.identityHashCode(component), false)
    }
  }

  fun onResume()
  {
    isInteracting = true
  }

  fun getRetrieveViewModelOver(): Runnable? =
      refreshViewModelAndBindNextTime?.onOver

  fun onRefreshingViewModelAndBindingStart()
  {
    refreshingViewModelAndBindingCount++
  }

  @Synchronized
  fun onRefreshingViewModelAndBindingStop(lifeCycleActivity: SweetLifeCycle)
  {
    refreshingViewModelAndBindingCount--

    // If the entity or the hosting Activity is not alive, we do nothing more
    if (isAliveAsWellAsHostingActivity() == false)
    {
      return
    }

    if (refreshViewModelAndBindPending != null)
    {
      Timber.d("The stacked refresh of the business objects and display is stacked can now be executed")

      refreshViewModelAndBindPending?.refreshViewModelAndBind(lifeCycleActivity)
      refreshViewModelAndBindPending = null
    }
  }

  fun isRefreshingViewModelAndBinding(): Boolean
  {
    return refreshingViewModelAndBindingCount > 0
  }

  fun onPause()
  {
    isInteracting = false
  }

  fun onDestroy()
  {
    isAlive = false

    // If the business objects retrieval and synchronization is not yet completed, we do not forget to notify
    if (isRefreshingViewModelAndBinding() == true)
    {
      onStopLoading()
    }

    // We unregister all the "BroadcastListener" entities
    unregisterBroadcastListeners()
  }

  fun onSaveInstanceState(outState: Bundle)
  {
    outState.putBoolean(StateContainer.ALREADY_STARTED_EXTRA, true)
  }

  fun beingRedirected()
  {
    beingRedirected = true
  }

  fun shouldKeepOn(): Boolean =
      beingRedirected == false

  @Synchronized
  fun shouldDelayRefreshBusinessObjectsAndDisplay(onOver: Runnable?): Boolean
  {
    // If the entity or the hosting Activity is finishing, we give up
    if (isAliveAsWellAsHostingActivity() == false)
    {
      return true
    }

    // We test whether the Activity is active (its life-cycle state is between 'onResume()' and 'onPause()'
    if (isInteracting == false)
    {
      refreshViewModelAndBindNextTime = RefreshViewModelAndBind(onOver)

      Timber.d("The refresh of the business objects and display is delayed because the Activity is not interacting")

      return true
    }
    // We test whether the Activity is already being refreshed
    if (isRefreshingViewModelAndBinding() == true)
    {
      // In that case, we need to wait for the refresh action to be over
      Timber.d("The refresh of the viewModel and bind is stacked because it is already refreshing")

      refreshViewModelAndBindPending = RefreshViewModelAndBind(onOver)

      return true
    }

    refreshViewModelAndBindNextTime = null

    return false
  }

  fun onInternalViewModelAvailableExceptionWorkAround(throwable: Throwable): Boolean
  {
    if (throwable is IllegalStateException)
    {
      if (throwable.message?.endsWith(StateContainer.ILLEGAL_STATE_EXCEPTION_FRAGMENT_MESSAGE_SUFFIX) == true)
      {
        return true
      }
    }
    return false
  }

  fun viewModelRetrieved()
  {
    viewModelRetrieved = true
  }

  fun markNotResumedForTheFirstTime()
  {
    resumedForTheFirstTime = false
  }

}