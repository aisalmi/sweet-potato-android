package com.hagergroup.sample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleFragmentAggregate(fragment: Fragment, fragmentAnnotation: SweetFragmentAnnotation?)
  : SweetFragmentAggregate(fragment, fragmentAnnotation),
    SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider
{

  private val fragmentLoadingErrorAndRetryAggregate by lazy { SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate() }

  private val fragmentModelUnavailableExceptionKeeper by lazy { SweetLoadingAndErrorInterceptor.ModelUnavailableExceptionKeeper() }

  val modelContainer by lazy { SweetActivityInterceptor.ModelContainer() }

  fun rememberModelUnavailableException(exception: ModelUnavailableException)
  {
    fragmentModelUnavailableExceptionKeeper.exception = exception
  }

  fun forgetException()
  {
    fragmentModelUnavailableExceptionKeeper.exception = null
  }

  fun showModelUnavailableException(activity: AppCompatActivity, sweetableFragment: Sweetable<*>, exception: ModelUnavailableException)
  {
    rememberModelUnavailableException(exception)
    fragmentLoadingErrorAndRetryAggregate.showModelUnavailableException(activity, sweetableFragment, exception)
  }

  @Throws(ModelUnavailableException::class)
  fun checkException()
  {
    fragmentModelUnavailableExceptionKeeper.checkException()
  }

  fun onRestoreInstanceState(bundle: Bundle)
  {
    modelContainer.onRestoreInstanceState(bundle)
  }

  fun onSaveInstanceState(bundle: Bundle)
  {
    modelContainer.onSaveInstanceState(bundle)
  }

  override fun getLoadingErrorAndRetryAggregate(): SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate =
      fragmentLoadingErrorAndRetryAggregate

  override fun getModelUnavailableExceptionKeeper(): SweetLoadingAndErrorInterceptor.ModelUnavailableExceptionKeeper =
      fragmentModelUnavailableExceptionKeeper

}
