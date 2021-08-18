package com.hagergroup.sample.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.hagergroup.sample.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class MainFragmentViewModelTest
{

  /*
      You should ALWAYS inject Dispatchers
      Testing Coroutines on Android (Android Dev Summit '19): https://www.youtube.com/watch?v=KMb0Fs8rCRs
   */

  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()

  @Test
  fun countDefaultDispatcher()
  {
    val viewModel = MainFragmentViewModel(ApplicationProvider.getApplicationContext(), SavedStateHandle())
    viewModel.count(3)

    // count will return 0 instead of 3, because count() has been executed in Dispatchers.IO
    assertEquals(0, viewModel.count)
  }

  @Test
  fun countSameDispatcher() = mainCoroutineRule.runBlockingTest {
    val viewModel = MainFragmentViewModel(ApplicationProvider.getApplicationContext(), SavedStateHandle(), mainCoroutineRule.dispatcher)
    viewModel.count(3)

    assertEquals(3, viewModel.count)
  }
}