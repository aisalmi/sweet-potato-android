package com.hagergroup.sample.fragment

import android.view.View
import android.widget.Toast
import com.hagergroup.sample.R
import com.hagergroup.sample.SampleActivity
import com.hagergroup.sample.adapter.MyAdapter
import com.hagergroup.sample.databinding.FragmentThirdBinding
import com.hagergroup.sample.viewmodel.ThirdFragmentViewModel
import com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import kotlinx.android.synthetic.main.fragment_second.observableField
import kotlinx.android.synthetic.main.fragment_second.refreshError
import kotlinx.android.synthetic.main.fragment_second.refreshInternetError
import kotlinx.android.synthetic.main.fragment_third.*
import java.net.UnknownHostException
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
@SweetViewModelBindingFragmentAnnotation(layoutId = R.layout.fragment_third, fragmentTitleId = R.string.app_name, viewModelClass = ThirdFragmentViewModel::class, surviveOnConfigurationChanged = false, viewModelContext = SweetViewModelBindingFragmentAnnotation.ViewModelContext.Activity)
class ThirdFragment
  : SampleViewModelBindingFragment<FragmentThirdBinding>(),
    View.OnClickListener
{

  companion object
  {

    const val MY_EXTRA = "myExtra"

    const val ANOTHER_EXTRA = "anotherExtra"

  }

  private var count = 0

  private var throwError = false

  private var throwInternetError = false

  @Throws(ModelUnavailableException::class)
  override suspend fun computeViewModel()
  {
    Thread.sleep(1_000)

    if (throwError == true)
    {
      throwError = false

      throw ModelUnavailableException("Cannot retrieve the model")
    }

    if (throwInternetError == true)
    {
      throwInternetError = false

      throw ModelUnavailableException("Cannot retrieve the model", UnknownHostException())
    }

    (viewModel as? ThirdFragmentViewModel)?.apply {
      myString = if (count % 2 == 0) arguments?.getString(ThirdFragment.MY_EXTRA) else "Count !"
      anotherString.postValue(arguments?.getString(ThirdFragment.ANOTHER_EXTRA))
      persons.addAll(Array(15) { "Person ${it + 1}" })
    }
  }

  override fun onBindModel()
  {
    super.onBindModel()

    refreshError.setOnClickListener(this)
    refreshInternetError.setOnClickListener(this)
    observableField.setOnClickListener(this)
    backstack.setOnClickListener(this)

    list.apply {
      setHasFixedSize(true)
      adapter = MyAdapter((viewModel as? ThirdFragmentViewModel)?.persons ?: emptyList())
    }
  }

  override fun onClick(view: View?)
  {
    if (view == refreshError)
    {
      throwError = true
      refreshModelAndBind(true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      }, true)
    }
    else if (view == refreshInternetError)
    {
      throwInternetError = true
      refreshModelAndBind(true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      }, true)
    }
    else if (view == observableField)
    {
      (viewModel as ThirdFragmentViewModel).anotherString.postValue(UUID.randomUUID().toString())
    }
    else if (view == backstack)
    {
      (activity as? SampleActivity)?.getAggregate()?.addOrReplaceFragment(BackstackFragment::class, R.id.fragmentContainer, true, "BackstackFragment", null, null, SweetActivityAggregate.FragmentTransactionType.Add)
    }
  }

}
