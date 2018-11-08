package com.hagergroup.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.annotation.SweetLoadingAndErrorAnnotation
import com.hagergroup.sweetpotato.annotation.SweetSendLoadingIntentAnnotation
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ViewModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetLoadingAndErrorAnnotation
@SweetSendLoadingIntentAnnotation
abstract class SampleFragment
  : SweetFragment<SampleFragmentAggregate>(),
    SweetFragmentAggregate.OnBackPressedListener
{

  @CallSuper
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
  {
    savedInstanceState?.let {
      getAggregate()?.onRestoreInstanceState(it)
    }

    return inflater.inflate(getAggregate()?.fragmentAnnotation?.layoutId ?: -1, container, false)
  }


  @CallSuper
  @Throws(ViewModelUnavailableException::class)
  override suspend fun onRetrieveViewModel()
  {
    getAggregate()?.checkException()
  }

  override fun onBindViewModel()
  {
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle)
  {
    super.onSaveInstanceState(outState)
    getAggregate()?.onSaveInstanceState(outState)
  }

  override fun onBackPressed(): Boolean =
      false

}
