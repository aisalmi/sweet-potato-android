package com.hagergroup.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hagergroup.sample.R
import com.hagergroup.sample.databinding.FragmentSecondBinding
import com.hagergroup.sample.viewmodel.SecondFragmentViewModel
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import kotlinx.android.synthetic.main.fragment_second.*
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_second, fragmentTitleId = R.string.app_name, viewModelClass = SecondFragmentViewModel::class, surviveOnConfigurationChanged = false)
class SecondFragment
  : SampleFragment<FragmentSecondBinding, SecondFragmentViewModel>(),
    View.OnClickListener
{

  companion object
  {

    const val MY_EXTRA = "myExtra"

    const val ANOTHER_EXTRA = "anotherExtra"

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    refreshError.setOnClickListener(this)
    refreshInternetError.setOnClickListener(this)
    observableField.setOnClickListener(this)
  }

  override fun onClick(view: View?)
  {
    if (view == refreshError)
    {
      getCastedViewModel()?.throwError = true

      getCastedViewModel()?.refreshViewModel(arguments,true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      })
    }
    else if (view == refreshInternetError)
    {
      getCastedViewModel()?.throwInternetError = true
      getCastedViewModel()?.refreshViewModel(arguments,true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      })
    }
    else if (view == observableField)
    {
      getCastedViewModel()?.anotherString?.postValue(UUID.randomUUID().toString())
    }
  }

}
