package com.hagergroup.sample

import com.hagergroup.sample.fragment.ThirdFragment
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
@SweetActivityAnnotation(contentViewId = R.layout.activity_second, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = ThirdFragment::class, canRotate = true)
@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class ThirdActivity
  : SampleActivity()
