package com.hagergroup.sweetpotato.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider
import com.hagergroup.sweetpotato.content.SweetBroadcastListenersProvider
import timber.log.Timber

/**
 * There for gathering all instance variables, and in order to make copy and paste smarter.
 *
 * @param AggregateClass the aggregate class accessible though the [Sweetable.setAggregate] and [Sweetable.getAggregate] methods
 * @param ComponentClass the instance the container has been created for
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class StateContainer<AggregateClass : Any, ComponentClass : Any>(private val activity: AppCompatActivity, private val component: ComponentClass)
{

  var aggregate: AggregateClass? = null

  private var beingRedirected = false

  private var broadcastReceivers: Array<BroadcastReceiver?>? = null

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
    LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, broadcastListener.getIntentFilter())
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

  private fun unregisterBroadcastListeners()
  {
    broadcastReceivers?.let { broadcastReceiver ->
      broadcastReceiver.indices.reversed().forEach { indice ->
        broadcastReceiver[indice]?.let { currentBroadcastReceiver ->
          LocalBroadcastManager.getInstance(activity).unregisterReceiver(currentBroadcastReceiver)
        }
      }

      Timber.d("Stopped listening to ${broadcastReceivers?.size} intent broadcasts")
    }
  }

  fun onDestroy()
  {
    // We unregister all the "BroadcastListener" entities
    unregisterBroadcastListeners()
  }

  fun beingRedirected()
  {
    beingRedirected = true
  }

}