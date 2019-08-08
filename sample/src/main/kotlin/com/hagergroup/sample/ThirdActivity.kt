package com.hagergroup.sample

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.hagergroup.sample.fragment.ThirdFragment
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
@SweetActivityAnnotation(contentViewId = R.layout.activity_second, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = ThirdFragment::class, canRotate = true, fragmentBackStackName = "ThirdActivity", addFragmentToBackStack = true)
@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class ThirdActivity
  : SampleActivity(), FragmentManager.OnBackStackChangedListener
{

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