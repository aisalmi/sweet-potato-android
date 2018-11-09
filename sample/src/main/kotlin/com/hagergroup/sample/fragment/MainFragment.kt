package com.hagergroup.sample.fragment

import com.hagergroup.sample.R
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_main, fragmentTitleId = R.string.expand_button_title)
class MainFragment
  : SampleFragment()
{

  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
    //TODO
    super.onRetrieveModel()

    Thread.sleep(5_000)
  }

  override fun onBindModel()
  {
    //TODO
    super.onBindModel()
  }

}
