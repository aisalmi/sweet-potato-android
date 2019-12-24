package com.hagergroup.sweetpotato.appcompat.app

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.app.Sweetizer
import com.hagergroup.sweetpotato.content.SweetBroadcastListener

/**
 * The basis class for all activities available in the framework.
 *
 * @param AggregateClass the aggregate class accessible through the [setAggregate] and [getAggregate] methods
 *
 * @see Sweetable
 * @see SweetActivityAggregate
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
abstract class SweetAppCompatActivity<AggregateClass : SweetActivityAggregate>
  : AppCompatActivity(),
    Sweetable<AggregateClass>
{

  private val sweetizer by lazy { Sweetizer(this, this, this, null, lifecycleScope) }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    sweetizer.onCreate(Runnable {
      super@SweetAppCompatActivity.onCreate(savedInstanceState)
    }, savedInstanceState)
  }

  override fun onStart()
  {
    super.onStart()
    sweetizer.onStart()
  }

  override fun onResume()
  {
    super.onResume()
    sweetizer.onResume()
  }

  override fun onPause()
  {
    try
    {
      sweetizer.onPause()
    }
    finally
    {
      super.onPause()
    }
  }

  override fun onStop()
  {
    try
    {
      sweetizer.onStop()
    }
    finally
    {
      super.onStop()
    }
  }

  override fun onDestroy()
  {
    try
    {
      sweetizer.onDestroy()
    }
    finally
    {
      super.onDestroy()
    }
  }

  override fun getAggregate(): AggregateClass? =
      sweetizer.getAggregate()

  override fun setAggregate(aggregate: AggregateClass?)
  {
    sweetizer.setAggregate(aggregate)
  }

  override fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    sweetizer.registerBroadcastListeners(broadcastListeners)
  }

  override fun onNewIntent(intent: Intent)
  {
    super.onNewIntent(intent)
    sweetizer.onNewIntent()
  }

  override fun onRetrieveDisplayObjects()
  {
  }

  override fun isRefreshingModelAndBinding(): Boolean =
      sweetizer.isRefreshingModelAndBinding()

  override fun isFirstLifeCycle(): Boolean =
      sweetizer.isFirstLifeCycle()

  override fun isInteracting(): Boolean =
      sweetizer.isInteracting()

  override fun isAlive(): Boolean =
      sweetizer.isAlive()

  override suspend fun onRetrieveModel()
  {

  }

  override fun refreshModelAndBind(retrieveModel: Boolean, onOver: Runnable?, immediately: Boolean)
  {
    sweetizer.refreshModelAndBind(retrieveModel, onOver, immediately)
  }

  override fun shouldKeepOn(): Boolean =
      sweetizer.shouldKeepOn()

  override fun onSaveInstanceState(outState: Bundle)
  {
    super.onSaveInstanceState(outState)
    sweetizer.onSaveInstanceState(outState)
  }

  fun refreshModelAndBind()
  {
    refreshModelAndBind(true, null, false)
  }

  override fun onBindModel()
  {

  }

  @LayoutRes
  open fun getContentViewId(): Int? =
      null

  @IdRes
  open fun getFragmentPlaceholderId(): Int? =
      null

  @IdRes
  open fun getToolbarId(): Int? =
      null

}