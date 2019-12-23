package com.hagergroup.sweetpotato.fragment.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.app.Sweetizer
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * A basis class for designing an Android compatibility library [Fragment] compatible with the framework, i.e.
 * sweet potato ready.
 * <p>
 * This implementation use a [SweetViewModel] in order to implement the databinding and the MVVM architecture
 * </p>
 *
 * @param AggregateClass the aggregate class accessible though the [setAggregate] and [getAggregate] methods
 * @param BindingClass the binding class in order to implement the databinding
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragment<AggregateClass : SweetFragmentAggregate, BindingClass : ViewDataBinding, ViewModelClass : SweetViewModel>
  : Fragment(),
    Sweetable<AggregateClass>
{

  private var sweetizer: Sweetizer<AggregateClass, SweetFragment<AggregateClass, BindingClass, ViewModelClass>>? = null

  protected var binding: BindingClass? = null

  protected var viewModel: SweetViewModel? = null

  private val onRebindCallback = object : OnRebindCallback<BindingClass>()
  {

    override fun onPreBind(binding: BindingClass): Boolean
    {
      return false
    }

  }

  protected abstract fun getBindingVariable(): Int

  @LayoutRes
  protected open fun getLayoutId(): Int? =
      null

  protected open fun getViewModelFactory(): ViewModelProvider.Factory? =
      null

  @Suppress("UNCHECKED_CAST")
  protected open fun getCastedViewModel(): ViewModelClass? =
      viewModel as? ViewModelClass

  @CallSuper
  override fun onAttach(context: Context)
  {
    super.onAttach(context)

    (activity as? AppCompatActivity)?.let {
      sweetizer = Sweetizer(it, this, this, this)
    }
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?)
  {
    sweetizer?.onCreate(Runnable {
      super@SweetFragment.onCreate(savedInstanceState)
    })
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
  {
    val layoutId = getLayoutId() ?: getAggregate()?.getFragmentLayoutIdFromAnnotation() ?: android.R.layout.simple_list_item_1

    binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

    binding?.lifecycleOwner = viewLifecycleOwner

    if (getAggregate()?.getPreBindBehaviourFromAnnotation() == false)
    {
      binding?.addOnRebindCallback(onRebindCallback)
    }

    return binding?.root
  }

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    createViewModel()
    observeStates()
    computeViewModel()
  }

  protected open fun createViewModel()
  {
    val viewModelFactory = getViewModelFactory()
    val viewModelClass = getAggregate()?.getViewModelClassFromAnnotation() ?: DummySweetViewModel::class.java
    val viewModelOwner: ViewModelStoreOwner = if (getAggregate()?.getViewModelContextFromAnnotation() == SweetFragmentAnnotation.ViewModelContext.Fragment) this@SweetFragment else requireActivity()

    viewModel = if (viewModelFactory != null)
    {
      ViewModelProvider(viewModelOwner, viewModelFactory).get(viewModelClass)
    }
    else
    {
      ViewModelProvider(viewModelOwner).get(viewModelClass)
    }
  }

  protected open fun observeStates()
  {
    viewModel?.state?.observe(this, Observer
    {
      when (it)
      {
        is SweetViewModel.State.LoadingState -> onLoadingState()
        is SweetViewModel.State.LoadedState  -> onLoadedState()
        is SweetViewModel.State.ErrorState   -> onErrorState()
      }
    })
  }

  protected open fun computeViewModel()
  {
    viewModel?.computeViewModelInternal(arguments)
  }

  override fun onResume()
  {
    super.onResume()
    sweetizer?.onResume()
  }

  override fun onPause()
  {
    super.onPause()
    sweetizer?.onPause()
  }

  override fun onStop()
  {
    super.onStop()
    sweetizer?.onStop()
  }

  override fun onDestroy()
  {
    super.onDestroy()
    sweetizer?.onDestroy()
  }

  override fun getAggregate(): AggregateClass? =
      sweetizer?.getAggregate()

  override fun setAggregate(aggregate: AggregateClass?)
  {
    sweetizer?.setAggregate(aggregate)
  }

  override fun onException(throwable: Throwable, fromGuiThread: Boolean)
  {
    sweetizer?.onException(throwable, fromGuiThread)
  }

  override fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    sweetizer?.registerBroadcastListeners(broadcastListeners)
  }

  protected open fun onErrorState()
  {

  }

  @CallSuper
  protected open fun onLoadedState()
  {
    viewModel?.let {
      binding?.apply {
        removeOnRebindCallback(onRebindCallback)
        setVariable(getBindingVariable(), it)
      }
    }
  }

  protected open fun onLoadingState()
  {

  }

}