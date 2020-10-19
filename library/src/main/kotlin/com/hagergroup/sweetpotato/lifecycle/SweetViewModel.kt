package com.hagergroup.sweetpotato.lifecycle

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hagergroup.sweetpotato.R
import com.hagergroup.sweetpotato.app.SweetActivityController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.Serializable

/**
 * The basis class for all [ViewModel] available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.12.05
 */
abstract class SweetViewModel(application: Application)
  : AndroidViewModel(application)
{

  class StateManager
    : Serializable
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

    val errorMessage = MutableLiveData<Int>().apply {
      postValue(SweetActivityController.exceptionHandler?.getGenericErrorMessage() ?: R.string.sweetpotato_defaultErrorMessage)
    }

    val errorAndLoadingViewVisibility = Transformations.map(state)
    {
      if (it is State.LoadedState) View.INVISIBLE else View.VISIBLE
    }

    val loadingViewVisibility = Transformations.map(state)
    {
      if (it is State.ErrorState) View.INVISIBLE else View.VISIBLE
    }

  }

  val stateManager = StateManager()

  private var dataAlreadyLoaded = false

  abstract suspend fun computeViewModel(arguments: Bundle?)

  open fun computeViewModelInternal(arguments: Bundle?, displayLoadingState: Boolean = true, runnable: Runnable? = null)
  {
    viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
      viewModelScope.launch(Dispatchers.Main)
      {
        Timber.w(throwable, "An error occurred while computing the ViewModel")

        //TODO : log the error + update the message error

        val errorStringRes = SweetActivityController.handleException(true, throwable)

        stateManager.apply {
          state.postValue(StateManager.State.ErrorState(throwable))
          errorMessage.postValue(errorStringRes)
        }
      }
    }) {
      if (dataAlreadyLoaded == false)
      {
        if (displayLoadingState == true)
        {
          stateManager.state.postValue(StateManager.State.LoadingState)
        }

        computeViewModel(arguments)

        dataAlreadyLoaded = true
        stateManager.state.postValue(StateManager.State.LoadedState)
      }

      runnable?.run()
    }
  }

  open fun refreshViewModel(arguments: Bundle?, displayLoadingState: Boolean = true, runnable: Runnable? = null)
  {
    dataAlreadyLoaded = false
    computeViewModelInternal(arguments, displayLoadingState, runnable)
  }

}