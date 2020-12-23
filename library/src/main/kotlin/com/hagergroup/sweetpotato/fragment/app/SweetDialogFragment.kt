package com.hagergroup.sweetpotato.fragment.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.app.Sweetizer
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener
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
abstract class SweetDialogFragment<AggregateClass : SweetFragmentAggregate, BindingClass : ViewDataBinding, ViewModelClass : SweetViewModel>
  : DialogFragment(),
    Sweetable<AggregateClass>, SweetFragmentConfigurable
{

  private var sweetizer: Sweetizer<AggregateClass, SweetDialogFragment<AggregateClass, BindingClass, ViewModelClass>>? = null

  protected var viewDatabinding: BindingClass? = null

  protected var viewModel: ViewModelClass? = null

  private val onRebindCallback = object : OnRebindCallback<BindingClass>()
  {

    override fun onPreBind(binding: BindingClass): Boolean
    {
      return false
    }

  }

  protected abstract fun getViewModelClass(): Class<ViewModelClass>

  protected abstract fun getBindingVariable(): Int?

  @CallSuper
  override fun onAttach(context: Context)
  {
    super.onAttach(context)

    (activity as? AppCompatActivity)?.let {
      sweetizer = Sweetizer(it, this, this, this, lifecycleScope)
    }
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?)
  {
    sweetizer?.onCreate({
      super@SweetDialogFragment.onCreate(savedInstanceState)
    }, savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
  {
    viewDatabinding = DataBindingUtil.inflate(inflater, layoutId() ?: android.R.layout.activity_list_item, container, false)

    viewDatabinding?.lifecycleOwner = viewLifecycleOwner

    if (preBind() == false)
    {
      viewDatabinding?.addOnRebindCallback(onRebindCallback)
    }

    return viewDatabinding?.root
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
    val viewModelClass = getViewModelClass()
    val viewModelOwner: ViewModelStoreOwner = if (viewModelContext() == SweetFragmentConfigurable.ViewModelContext.Fragment) this@SweetDialogFragment else requireActivity()

    viewModel = ViewModelProvider(viewModelOwner, SavedStateViewModelFactory(requireActivity().application, this, arguments)).get(viewModelClass)
  }

  protected open fun observeStates()
  {
    viewModel?.stateManager?.state?.observe(viewLifecycleOwner, {
      when (it)
      {
        is SweetViewModel.StateManager.State.LoadingState -> onLoadingState()
        is SweetViewModel.StateManager.State.LoadedState  -> onLoadedState()
        is SweetViewModel.StateManager.State.ErrorState   -> onErrorState()
      }
    })
  }

  protected open fun computeViewModel()
  {
    viewModel?.computeViewModelInternal()
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

  override fun registerSweetSharedFlowListener(sweetSharedFlowListeners: SweetSharedFlowListener)
  {
    sweetizer?.registerSweetSharedFlowListener(sweetSharedFlowListeners)
  }

  protected open fun onErrorState()
  {

  }

  @CallSuper
  protected open fun onLoadedState()
  {
    viewModel?.let {
      viewDatabinding?.apply {
        removeOnRebindCallback(onRebindCallback)

        getBindingVariable()?.let { bindingVariable ->
          setVariable(bindingVariable, it)
        }
      }
    }
  }

  protected open fun onLoadingState()
  {

  }

}
