package com.hagergroup.sweetpotato.appcompat.app

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.app.Sweetizer
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener

/**
 * The basis class for all activities available in the framework.
 *
 * @param AggregateClass the aggregate class accessible through the [setAggregate] and [getAggregate] methods
 * @param ViewBindingClass the [ViewBinding] class associate with the activity
 *
 * @see Sweetable
 * @see SweetActivityAggregate
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
abstract class SweetAppCompatActivity<AggregateClass : SweetActivityAggregate, ViewBindingClass : ViewBinding>
  : AppCompatActivity(),
    Sweetable<AggregateClass>, SweetActionBarConfigurable, SweetActivityConfigurable
{

  protected var viewBinding: ViewBindingClass? = null

  private val sweetizer by lazy { Sweetizer(this, this, this, null, lifecycleScope) }

  /**
   * TODO : function doc
   */
  abstract fun inflateViewBinding(): ViewBindingClass

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?)
  {
    sweetizer.onCreate({
      super@SweetAppCompatActivity.onCreate(savedInstanceState)
    }, savedInstanceState)
  }

  @CallSuper
  override fun onRetrieveDisplayObjects()
  {
    viewBinding = inflateViewBinding()
    setContentView(viewBinding?.root)
  }

  @CallSuper
  override fun onStart()
  {
    super.onStart()
    sweetizer.onStart()
  }

  @CallSuper
  override fun onResume()
  {
    super.onResume()
    sweetizer.onResume()
  }

  @CallSuper
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

  @CallSuper
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

  @CallSuper
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

  override fun registerSweetSharedFlowListener(sweetSharedFlowListener: SweetSharedFlowListener)
  {
    sweetizer.registerSweetSharedFlowListener(sweetSharedFlowListener)
  }

  @CallSuper
  override fun onNewIntent(intent: Intent)
  {
    super.onNewIntent(intent)
    sweetizer.onNewIntent()
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

}
