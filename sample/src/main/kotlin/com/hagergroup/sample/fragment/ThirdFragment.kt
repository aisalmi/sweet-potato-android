package com.hagergroup.sample.fragment

import android.view.View
import com.hagergroup.sample.R
import com.hagergroup.sample.databinding.FragmentSecondBinding
import com.hagergroup.sample.viewmodel.SecondFragmentViewModel
import com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import kotlinx.android.synthetic.main.fragment_second.*
import org.jetbrains.anko.toast
import java.net.UnknownHostException
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
@SweetViewModelBindingFragmentAnnotation(layoutId = R.layout.fragment_second, fragmentTitleId = R.string.expand_button_title, viewModelClass = SecondFragmentViewModel::class, surviveOnConfigurationChanged = false, viewModelContext = SweetViewModelBindingFragmentAnnotation.ViewModelContext.Activity)
class ThirdFragment
  : SampleViewModelBindingFragment<FragmentSecondBinding>(),
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
  override fun computeViewModel()
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

    (viewModel as? SecondFragmentViewModel)?.apply {
      myString = if (count % 2 == 0) arguments?.getString(ThirdFragment.MY_EXTRA) else "Count !"
      anotherString.set(arguments?.getString(ThirdFragment.ANOTHER_EXTRA))
    }
  }

  override fun onBindModel()
  {
    super.onBindModel()

    refreshError.setOnClickListener(this)
    refreshInternetError.setOnClickListener(this)
    observableField.setOnClickListener(this)
  }

  override fun onClick(view: View?)
  {
    if (view == refreshError)
    {
      throwError = true
      refreshModelAndBind(Runnable {
        context?.toast("Finish !")
      })
    }
    else if (view == refreshInternetError)
    {
      throwInternetError = true
      refreshModelAndBind(Runnable {
        context?.toast("Finish !")
      })
    }
    else if (view == observableField)
    {
      (viewModel as SecondFragmentViewModel).anotherString.set(UUID.randomUUID().toString())
    }
  }

}
