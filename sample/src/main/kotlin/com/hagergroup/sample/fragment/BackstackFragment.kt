package com.hagergroup.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hagergroup.sample.R
import com.hagergroup.sample.SampleActivity
import com.hagergroup.sample.adapter.MyAdapter
import com.hagergroup.sample.databinding.FragmentThirdBinding
import com.hagergroup.sample.viewmodel.ThirdFragmentViewModel
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import kotlinx.android.synthetic.main.fragment_third.*
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_third, fragmentTitleId = R.string.app_name, viewModelClass = ThirdFragmentViewModel::class, surviveOnConfigurationChanged = false, viewModelContext = SweetFragmentAnnotation.ViewModelContext.Activity)
class BackstackFragment
  : SampleFragment<FragmentThirdBinding, ThirdFragmentViewModel>(),
    View.OnClickListener
{

  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    refreshError?.setOnClickListener(this)
    refreshInternetError?.setOnClickListener(this)
    observableField?.setOnClickListener(this)
    backstack?.setOnClickListener(this)

    list?.setHasFixedSize(true)
  }

  override fun onLoadedState()
  {
    super.onLoadedState()

    list?.adapter = MyAdapter((viewModel as? ThirdFragmentViewModel)?.persons ?: emptyList())
  }

  override fun onClick(view: View?)
  {
    if (view == refreshError)
    {
      getCastedViewModel()?.throwError = true
      getCastedViewModel()?.refreshViewModel(arguments, true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      })
    }
    else if (view == refreshInternetError)
    {
      getCastedViewModel()?.throwInternetError = true
      getCastedViewModel()?.refreshViewModel(arguments, true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      })
    }
    else if (view == observableField)
    {
      (viewModel as ThirdFragmentViewModel).anotherString.postValue(UUID.randomUUID().toString())
    }
    else if (view == backstack)
    {
      (activity as? SampleActivity)?.getAggregate()?.addOrReplaceFragment(BackstackFragment::class, R.id.fragmentContainer, true, "BackstackFragment" + System.identityHashCode(this@BackstackFragment), null, null, SweetActivityAggregate.FragmentTransactionType.Add)
    }
  }

}
