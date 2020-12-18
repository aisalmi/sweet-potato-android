package com.hagergroup.sample

import com.hagergroup.sample.databinding.ActivitySecondBinding
import com.hagergroup.sample.fragment.SecondFragment
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.12.05
 */
//@SweetActivityAnnotation(contentViewId = R.layout.activity_second, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = SecondFragment::class, canRotate = true)
//@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class SecondActivity
  : SampleActivity<ActivitySecondBinding>()
{

  override fun inflateViewBinding(): ActivitySecondBinding =
      ActivitySecondBinding.inflate(layoutInflater)

  override fun fragmentPlaceholderId(): Int =
      R.id.fragmentContainer

  override fun fragmentClass(): KClass<out SweetFragment<*, *, *>> =
      SecondFragment::class

  override fun canRotate(): Boolean =
      true

  override fun actionBarBehavior(): SweetActionBarConfigurable.ActionBarBehavior =
      SweetActionBarConfigurable.ActionBarBehavior.Up

}
