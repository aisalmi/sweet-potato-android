package com.hagergroup.sweetpotato.fragment.app

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragmentAggregate(val fragment: Fragment, val fragmentAnnotation: SweetFragmentAnnotation?)
  : SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider
{

  interface OnBackPressedListener
  {

    fun onBackPressed(): Boolean

  }

  private val fragmentLoadingErrorAndRetryAggregate by lazy { SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate() }

  private val fragmentModelUnavailableExceptionKeeper by lazy { SweetLoadingAndErrorInterceptor.ModelUnavailableExceptionKeeper() }

  val modelContainer by lazy { SweetActivityInterceptor.ModelContainer() }

  fun openChildFragment(parentFragment: SweetFragment<*>, @IdRes fragmentPlaceholderIdentifier: Int, fragmentClass: KClass<SweetFragment<*>>, savedState: Fragment.SavedState?)
  {
    try
    {
      val fragmentTransaction = parentFragment.childFragmentManager.beginTransaction()
      val childfragment = fragmentClass.java.newInstance()
      childfragment.arguments = parentFragment.arguments

      // We (re)set its initial state if necessary
      if (savedState != null)
      {
        childfragment.setInitialSavedState(savedState)
      }

      fragmentTransaction.replace(fragmentPlaceholderIdentifier, childfragment)
      fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
      fragmentTransaction.commit()
    }
    catch (exception: Exception)
    {
      Timber.e(exception, "Unable to instanciate the openedFragment '${fragmentClass.simpleName}'")
    }
  }

  fun onCreate(activity: AppCompatActivity?)
  {
    fragmentAnnotation?.let {
      activity?.supportActionBar?.let { actionBar ->
        val titleIdentifier = fragmentAnnotation.fragmentTitleId
        val subTitleIdentifier = fragmentAnnotation.fragmentSubTitleId

        if (titleIdentifier > 0)
        {
          actionBar.setTitle(titleIdentifier)
        }

        if (subTitleIdentifier > 0)
        {
          actionBar.setSubtitle(subTitleIdentifier)
        }
      }
    }
  }

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