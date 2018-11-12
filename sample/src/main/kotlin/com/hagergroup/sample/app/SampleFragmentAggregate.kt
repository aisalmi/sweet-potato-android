package com.hagergroup.sample.app

import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleFragmentAggregate(fragment: Fragment, fragmentAnnotation: SweetFragmentAnnotation?)
  : SweetFragmentAggregate(fragment, fragmentAnnotation)