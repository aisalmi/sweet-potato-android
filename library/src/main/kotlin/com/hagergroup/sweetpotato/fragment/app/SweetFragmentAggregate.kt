package com.hagergroup.sweetpotato.fragment.app

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The basis class for all Fragment Aggregate available in the framework.
 *
 * @see SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragmentAggregate(val fragment: Fragment, private val fragmentAnnotation: Any?)
  : SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider
{

  init
  {

    if (fragmentAnnotation !is SweetFragmentAnnotation && fragmentAnnotation !is SweetViewModelBindingFragmentAnnotation)
    {
      throw IllegalArgumentException("The fragment annotation field has to be a SweetFragmentAnnotation or a SweetViewModelBindingFragmentAnnotation class")
    }

  }

  interface OnBackPressedListener
  {

    fun onBackPressed(): Boolean

  }

  private val fragmentLoadingErrorAndRetryAggregate by lazy { SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate() }

  private val fragmentModelUnavailableExceptionKeeper by lazy { SweetLoadingAndErrorInterceptor.ModelUnavailableExceptionKeeper() }

  val modelContainer by lazy { SweetActivityInterceptor.ModelContainer() }

  /**
   * Open the specified fragment, the previous fragment is add to the back stack.
   */
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
        val titleIdentifier = getFragmentTitleIdFromAnnotation()
        val subTitleIdentifier = getFragmentSubtitleIdFromAnnotation()

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

  @StringRes
  fun getFragmentTitleIdFromAnnotation(): Int
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation                 -> fragmentAnnotation.fragmentTitleId
      is SweetViewModelBindingFragmentAnnotation -> fragmentAnnotation.fragmentTitleId
      else                                       -> -1
    }
  }

  @StringRes
  fun getFragmentSubtitleIdFromAnnotation(): Int
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation                 -> fragmentAnnotation.fragmentSubtitleId
      is SweetViewModelBindingFragmentAnnotation -> fragmentAnnotation.fragmentSubtitleId
      else                                       -> -1
    }
  }

  @LayoutRes
  fun getFragmentLayoutIdFromAnnotation(): Int
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation                 -> fragmentAnnotation.layoutId
      is SweetViewModelBindingFragmentAnnotation -> fragmentAnnotation.layoutId
      else                                       -> -1
    }
  }

  fun getFragmentSurviveOnConfigurationChangedFromAnnotation(): Boolean
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation                 -> fragmentAnnotation.surviveOnConfigurationChanged
      is SweetViewModelBindingFragmentAnnotation -> fragmentAnnotation.surviveOnConfigurationChanged
      else                                       -> false
    }
  }

  fun getViewModelClassFromAnnotation(): Class<out SweetViewModel>
  {
    return when (fragmentAnnotation)
    {
      is SweetViewModelBindingFragmentAnnotation -> fragmentAnnotation.viewModelClass.java
      else                                       -> DummySweetViewModel::class.java
    }
  }

  fun getViewModelContextFromAnnotation(): SweetViewModelBindingFragmentAnnotation.ViewModelContext
  {
    return when (fragmentAnnotation)
    {
      is SweetViewModelBindingFragmentAnnotation -> fragmentAnnotation.viewModelContext
      else                                       -> SweetViewModelBindingFragmentAnnotation.ViewModelContext.Fragment
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