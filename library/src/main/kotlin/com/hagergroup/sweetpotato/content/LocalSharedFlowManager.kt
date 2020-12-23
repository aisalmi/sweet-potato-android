package com.hagergroup.sweetpotato.content

import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * TODO : class description
 *
 * @author Ludovic Roland
 * @since 2020.12.23
 */
object LocalSharedFlowManager
{

  private val sharedFlow = MutableSharedFlow<Intent>()

  /**
   * TODO : function description
   */
  fun emit(coroutineScope: CoroutineScope, intent: Intent)
  {
    coroutineScope.launch(Dispatchers.IO) {
      sharedFlow.emit(intent)
    }
  }

  suspend fun collect(sweetSharedFlowListener: SweetSharedFlowListener)
  {
    val intentFilter = sweetSharedFlowListener.getIntentFilter()
    sharedFlow.filter { intent ->
      intentFilter.hasAction(intent.action) == true && ((intentFilter.countCategories() == 0 && (intent.categories?.size ?: 0) == 0) || (intentFilter.countCategories() > 0 && intentFilter.categoriesIterator().asSequence().firstOrNull { intent.categories?.contains(it) == true } != null))
    }.collect { intent ->
      sweetSharedFlowListener.onCollect(intent)
    }
  }

}