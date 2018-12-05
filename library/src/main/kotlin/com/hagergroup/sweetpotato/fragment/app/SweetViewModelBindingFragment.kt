package com.hagergroup.sweetpotato.fragment.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetViewModelBindingFragment<AggregateClass : SweetFragmentAggregate, BindingClass : ViewDataBinding>
  : SweetFragment<AggregateClass>()
{

  protected lateinit var binding: BindingClass

  protected var viewModel: SweetViewModel? = null

  protected abstract fun getBindingVariable(): Int

  abstract fun computeViewModel(viewModel: SweetViewModel?)

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

    viewModel = ViewModelProviders.of(this).get(getAggregate()?.getViewModelClassFromAnnotation() ?: DummySweetViewModel::class.java)

    if (viewModel?.isAlreadyInitialized == false)
    {
      computeViewModel(viewModel)
      viewModel?.isAlreadyInitialized = true
    }
  }

  override fun onBindModel()
  {
    super.onBindModel()

    viewModel?.let {
      binding.setVariable(getBindingVariable(), it)
    }
  }

}