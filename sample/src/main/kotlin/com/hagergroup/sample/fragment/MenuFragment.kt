package com.hagergroup.sample.fragment

import android.view.View
import com.hagergroup.sample.R
import com.hagergroup.sample.databinding.FragmentMenuBinding
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class MenuFragment
  : SampleFragment<FragmentMenuBinding, DummySweetViewModel>()
{

  override fun layoutId(): Int =
      R.layout.fragment_menu

  override fun getRetryView(): View? =
      null

}
