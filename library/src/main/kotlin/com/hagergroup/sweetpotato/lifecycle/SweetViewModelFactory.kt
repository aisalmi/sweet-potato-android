package com.hagergroup.sweetpotato.lifecycle

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.reflect.Constructor
import java.util.*

class SweetViewModelFactory(
  private val dispatcher: CoroutineDispatcher,
  private val application: Application?,
  owner: SavedStateRegistryOwner,
  defaultArgs: Bundle?,
) : AbstractSavedStateViewModelFactory(owner, defaultArgs)
{

  override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T
  {
    var constructor: Constructor<T>? = findMatchingConstructor(modelClass, ANDROID_VIEWMODEL_DISPATCHER_SIGNATURE)

    if (constructor == null)
    {
      constructor = findMatchingConstructor(modelClass, ANDROID_VIEWMODEL_SIGNATURE)

      if (constructor == null)
      {
        throw Exception("Constructor not found")
      }
      else
      {
        return constructor.newInstance(application, handle)
      }
    }
    else
    {
      return constructor.newInstance(application, handle, dispatcher)
    }
  }

  private fun <T> findMatchingConstructor(modelClass: Class<T>, signature: Array<Class<*>>): Constructor<T>?
  {
    for (constructor in modelClass.constructors)
    {
      if (Arrays.equals(signature, constructor.parameterTypes))
      {
        return constructor as Constructor<T>
      }
    }

    return null
  }

  private val ANDROID_VIEWMODEL_DISPATCHER_SIGNATURE = arrayOf(Application::class.java, SavedStateHandle::class.java, CoroutineDispatcher::class.java)

  private val ANDROID_VIEWMODEL_SIGNATURE = arrayOf(Application::class.java, SavedStateHandle::class.java)
}
