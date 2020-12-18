package com.hagergroup.sample

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.hagergroup.sample.databinding.ActivitySecondBinding
import com.hagergroup.sample.fragment.ThirdFragment
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
//@SweetActivityAnnotation(contentViewId = R.layout.activity_second, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = ThirdFragment::class, canRotate = true)
//@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class ThirdActivity
  : SampleActivity<ActivitySecondBinding>(),
    FragmentManager.OnBackStackChangedListener
{

  override fun inflateViewBinding(): ActivitySecondBinding =
      ActivitySecondBinding.inflate(layoutInflater)

  override fun fragmentPlaceholderId(): Int =
      R.id.fragmentContainer

  override fun fragmentClass(): KClass<out SweetFragment<*, *, *>> =
      ThirdFragment::class

  override fun canRotate(): Boolean =
      true

  override fun actionBarBehavior(): SweetActionBarConfigurable.ActionBarBehavior =
      SweetActionBarConfigurable.ActionBarBehavior.Up

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    supportFragmentManager.addOnBackStackChangedListener(this)
  }

  override fun onBackStackChanged()
  {
    Timber.d("current opened fragment: ${getAggregate()?.openedFragment}")
  }

}