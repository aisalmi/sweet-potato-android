package com.hagergroup.sample.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ViewModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleFragmentAggregate(fragment: Fragment, fragmentAnnotation: SweetFragmentAnnotation?)
  : SweetFragmentAggregate(fragment, fragmentAnnotation),
    SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider
{

  private val fragmentLoadingErrorAndRetryAggregate by lazy { SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate() }

  private val fragmentViewModelUnavailableExceptionKeeper by lazy { SweetLoadingAndErrorInterceptor.ViewModelUnavailableExceptionKeeper() }

  val viewModelContainer by lazy { SweetActivityInterceptor.ViewModelContainer() }

  fun rememberViewModelUnavailableException(exception: ViewModelUnavailableException)
  {
    fragmentViewModelUnavailableExceptionKeeper.exception = exception
  }

  fun forgetException()
  {
    fragmentViewModelUnavailableExceptionKeeper.exception = null
  }

  fun showViewModelUnavailableException(activity: FragmentActivity, sweetableFragment: Sweetable<*>, exception: ViewModelUnavailableException)
  {
    rememberViewModelUnavailableException(exception)
    fragmentLoadingErrorAndRetryAggregate.showViewModelUnavailableException(activity, sweetableFragment, exception)
  }

  @Throws(ViewModelUnavailableException::class)
  fun checkException()
  {
    fragmentViewModelUnavailableExceptionKeeper.checkException()
  }

  fun onRestoreInstanceState(bundle: Bundle)
  {
    viewModelContainer.onRestoreInstanceState(bundle)
  }

  fun onSaveInstanceState(bundle: Bundle)
  {
    viewModelContainer.onSaveInstanceState(bundle)
  }

  override fun getLoadingErrorAndRetryAggregate(): SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate =
      fragmentLoadingErrorAndRetryAggregate

  override fun getViewModelUnavailableExceptionKeeper(): SweetLoadingAndErrorInterceptor.ViewModelUnavailableExceptionKeeper =
      fragmentViewModelUnavailableExceptionKeeper

}
