package com.hagergroup.sample

import com.hagergroup.sample.fragment.SecondFragment
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation

/**
 * @author Ludovic Roland
 * @since 2018.12.05
 */
@SweetActivityAnnotation(contentViewId = R.layout.activity_second, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = SecondFragment::class)
@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class SecondActivity
  : SampleActivity()
