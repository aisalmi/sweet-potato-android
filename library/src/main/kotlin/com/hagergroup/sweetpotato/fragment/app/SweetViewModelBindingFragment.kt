package com.hagergroup.sweetpotato.fragment.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
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
abstract class SweetViewModelBindingFragment<AggregateClass : SweetFragmentAggregate, BindingClass : ViewDataBinding>
  : SweetFragment<AggregateClass>()
{

  protected lateinit var binding: BindingClass

  protected var viewModel: SweetViewModel? = null

  protected abstract fun getBindingVariable(): Int

  abstract fun computeViewModel()

  override fun inflateLayout(inflater: LayoutInflater, container: ViewGroup?): View?
  {
    binding = if (getLayoutId() != -1)
    {
      DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
    }
    else
    {
      DataBindingUtil.inflate(inflater, getAggregate()?.getFragmentLayoutIdFromAnnotation() ?: -1, container, false)
    }

    return binding.root
  }

  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
    super.onRetrieveModel()

    val viewModelFactory = getViewModelFactory()
    val viewModelClass = getAggregate()?.getViewModelClassFromAnnotation() ?: DummySweetViewModel::class.java

    viewModel = if (getAggregate()?.getViewModelContextFromAnnotation() == SweetViewModelBindingFragmentAnnotation.ViewModelContext.Fragment)
    {
      ViewModelProviders.of(this@SweetViewModelBindingFragment, viewModelFactory).get(viewModelClass)
    }
    else
    {
      ViewModelProviders.of(requireActivity(), viewModelFactory).get(viewModelClass)
    }

    if (viewModel?.isAlreadyInitialized == false)
    {
      computeViewModel()
      viewModel?.isAlreadyInitialized = true
    }
  }

  override fun refreshModelAndBind(retrieveModel: Boolean, onOver: Runnable?, immediately: Boolean)
  {
    viewModel?.isAlreadyInitialized = false
    super.refreshModelAndBind(retrieveModel, onOver, immediately)
  }

  override fun onBindModel()
  {
    super.onBindModel()

    viewModel?.let {
      binding.setVariable(getBindingVariable(), it)
    }
  }

  protected open fun getViewModelFactory(): ViewModelProvider.Factory? =
      null

}