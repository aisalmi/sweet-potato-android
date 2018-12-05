package com.hagergroup.sample.fragment

import android.view.View
import com.hagergroup.sample.R
import com.hagergroup.sample.databinding.FragmentSecondBinding
import com.hagergroup.sample.viewmodel.SecondFragmentViewModel
import com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import kotlinx.android.synthetic.main.fragment_second.*
import org.jetbrains.anko.toast
import java.net.UnknownHostException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetViewModelBindingFragmentAnnotation(layoutId = R.layout.fragment_second, fragmentTitleId = R.string.expand_button_title, viewModelClass = SecondFragmentViewModel::class)
class SecondFragment
  : SampleViewModelBindingFragment<FragmentSecondBinding>(),
    View.OnClickListener
{

  companion object
  {

    const val MY_EXTRA = "myExtra"

  }

  private var throwError = false

  private var throwInternetError = false

  @Throws(ModelUnavailableException::class)
  override fun computeViewModel(viewModel: SweetViewModel?)
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

    (viewModel as? SecondFragmentViewModel)?.myString = arguments?.getString(SecondFragment.MY_EXTRA)
  }

  override fun onBindModel()
  {
    super.onBindModel()

    refreshError.setOnClickListener(this)
    refreshInternetError.setOnClickListener(this)
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
  }

}
