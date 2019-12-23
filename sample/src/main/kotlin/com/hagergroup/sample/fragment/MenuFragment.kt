package com.hagergroup.sample.fragment

import com.hagergroup.sample.R
import com.hagergroup.sample.databinding.FragmentMenuBinding
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_menu)
class MenuFragment
  : SampleFragment<FragmentMenuBinding, DummySweetViewModel>()
