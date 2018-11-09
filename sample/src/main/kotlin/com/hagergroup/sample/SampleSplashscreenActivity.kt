package com.hagergroup.sample

import androidx.fragment.app.FragmentActivity
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetSplashscreenActivity
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleSplashscreenActivity
  : SweetSplashscreenActivity<SampleActivityAggregate>()
{

  override fun onRetrieveDisplayObjects()
  {
    setContentView(R.layout.activity_splashscreen)
  }

  override fun getNextActivity(): KClass<out FragmentActivity> =
      MainActivity::class

  override fun onRetrieveViewModelCustom()
  {
    Thread.sleep(5_000)
  }

}