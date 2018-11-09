package com.hagergroup.sample.fragment

import com.hagergroup.sample.R
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.lifecycle.ViewModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_main, fragmentTitleId = R.string.app_name)
class MainFragment
  : SampleFragment()
{

  @Throws(ViewModelUnavailableException::class)
  override fun onRetrieveViewModel()
  {
    //TODO
    super.onRetrieveViewModel()
  }

  override fun onBindViewModel()
  {
    //TODO
    super.onBindViewModel()
  }

}
