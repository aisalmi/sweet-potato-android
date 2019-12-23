package com.hagergroup.sweetpotato.lifecycle

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * The basis class for all [ViewModel] available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.12.05
 */
abstract class SweetViewModel(application: Application)
  : AndroidViewModel(application)
{

  sealed class State
  {

    object LoadingState : State()

    object LoadedState : State()

    class ErrorState(val throwable: Throwable) : State()

  }

  val state = MutableLiveData<State>().apply {
    postValue(State.LoadingState)
  }

  val errorMessage = MutableLiveData<String>().apply {
    postValue("")
  }

  val loadingViewVisibility = Transformations.map(state)
  {
    if (it is State.LoadingState) View.VISIBLE else View.INVISIBLE
  }

  val errorViewVisibility = Transformations.map(state)
  {
    if (it is State.ErrorState) View.VISIBLE else View.INVISIBLE
  }

  abstract suspend fun computeViewModel(arguments: Bundle?)

  open fun computeViewModelInternal(arguments: Bundle?, displayLoadingState: Boolean = true, runnable: Runnable? = null)
  {
    viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
      viewModelScope.launch(Dispatchers.Main)
      {
        Timber.w(throwable, "An error occurred while computing the ViewModel")

        state.postValue(State.ErrorState(throwable))
      }
    }) {
      if (displayLoadingState == true)
      {
        state.postValue(State.LoadingState)
      }

      delay(200)

      computeViewModel(arguments)

      state.postValue(State.LoadedState)

      runnable?.run()
    }
  }

  open fun refreshViewModel(arguments: Bundle?, displayLoadingState: Boolean = true, runnable: Runnable? = null)
  {
    computeViewModelInternal(arguments, displayLoadingState, runnable)
  }

}