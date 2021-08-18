package com.hagergroup.sample.fragment

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hagergroup.sample.R
import com.hagergroup.sample.SecondActivity
import com.hagergroup.sample.ThirdActivity
import com.hagergroup.sample.databinding.FragmentMainBinding
import com.hagergroup.sample.viewmodel.MainFragmentViewModel
import com.hagergroup.sweetpotato.content.LocalSharedFlowManager
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener
import com.hagergroup.sweetpotato.content.SweetSharedFlowListenerProvider
import com.hagergroup.sweetpotato.coroutines.SweetCoroutines
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import timber.log.Timber
import java.net.URL

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
//@SweetFragmentAnnotation(layoutId = R.layout.fragment_main, fragmentTitleId = R.string.app_name, viewModelClass = MainFragmentViewModel::class)
class MainFragment
  : SampleFragment<FragmentMainBinding, MainFragmentViewModel>(),
    View.OnClickListener, SweetSharedFlowListenerProvider
{

  companion object
  {

    const val MY_ACTION = "myAction"

  }

  override fun getSweetSharedFlowListener(): SweetSharedFlowListener
  {
    return object : SweetSharedFlowListener
    {
      override fun getIntentFilter(): IntentFilter =
          IntentFilter(MainFragment.MY_ACTION)

      override fun onCollect(intent: Intent)
      {
        if (intent.action == MainFragment.MY_ACTION)
        {
          Toast.makeText(context, "Click !", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  override fun layoutId(): Int =
      R.layout.fragment_main

  override fun fragmentTitleId(): Int =
      R.string.app_name

  override fun getViewModelClass(): Class<MainFragmentViewModel> =
      MainFragmentViewModel::class.java

  override fun getDispatcher(): CoroutineDispatcher =
      Dispatchers.Default

  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    viewDatabinding?.openBinding?.setOnClickListener(this)
    viewDatabinding?.openBinding2?.setOnClickListener(this)
    viewDatabinding?.click?.setOnClickListener(this)
    viewDatabinding?.refreshLoading?.setOnClickListener(this)
    viewDatabinding?.refreshNoLoading?.setOnClickListener(this)
    viewDatabinding?.refreshError?.setOnClickListener(this)
    viewDatabinding?.refreshInternetError?.setOnClickListener(this)
    viewDatabinding?.coroutines?.setOnClickListener(this)
    viewDatabinding?.coroutinesError?.setOnClickListener(this)
    viewDatabinding?.alertDialog?.setOnClickListener(this)
  }

  override fun onClick(view: View?)
  {
    when (view)
    {
      viewDatabinding?.openBinding          ->
      {
        val intent = Intent(context, SecondActivity::class.java).apply {
          putExtra(SecondFragment.MY_EXTRA, "hey !")
          putExtra(SecondFragment.ANOTHER_EXTRA, "Another Hey !")
        }

        startActivity(intent)
      }
      viewDatabinding?.openBinding2         ->
      {
        val intent = Intent(context, ThirdActivity::class.java).apply {
          putExtra(ThirdFragment.MY_EXTRA, "go !")
          putExtra(ThirdFragment.ANOTHER_EXTRA, "Another go !")
        }

        startActivity(intent)
      }
      viewDatabinding?.click                ->
      {
        LocalSharedFlowManager.emit(lifecycleScope, Intent(MainFragment.MY_ACTION))
      }
      viewDatabinding?.refreshLoading       ->
      {
        viewModel?.refreshViewModel(true) {
          Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
        }
      }
      viewDatabinding?.refreshNoLoading     ->
      {
        viewModel?.refreshViewModel(false) {
          Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
        }
      }
      viewDatabinding?.refreshError         ->
      {
        viewModel?.apply {
          throwError = true
          refreshViewModel(true) {
            Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
          }
        }
      }
      viewDatabinding?.refreshInternetError ->
      {
        viewModel?.apply {
          throwInternetError = true
          refreshViewModel(true) {
            Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
          }
        }
      }
      viewDatabinding?.coroutines           ->
      {
        startSweetCoroutines()
      }
      viewDatabinding?.coroutinesError      ->
      {
        startSweetCoroutinesError()
      }
      viewDatabinding?.alertDialog          ->
      {
        displayAlertDialog()
      }
    }
  }

  private fun displayAlertDialog()
  {
    context?.let {
      MaterialAlertDialogBuilder(it).apply {
        setTitle("Hi!")
        setMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        setPositiveButton(android.R.string.ok, null)
        setNegativeButton(android.R.string.cancel, null)
      }.show()
    }
  }

  private fun startSweetCoroutines()
  {
    SweetCoroutines.execute(lifecycleScope, object : SweetCoroutines.SweetGuardedCoroutine(context)
    {
      override suspend fun run()
      {
        val oracle = URL("https://www.google.com/")
        oracle.openConnection().inputStream.use {
          Timber.d(it.bufferedReader().readText())
        }
      }
    })
  }

  private fun startSweetCoroutinesError()
  {
    SweetCoroutines.execute(lifecycleScope, object : SweetCoroutines.SweetGuardedCoroutine(context)
    {
      override suspend fun run()
      {
        delay(2_000)

        23.div(0)
      }

    })
  }

  override fun getRetryView(): View? =
      viewDatabinding?.loadingErrorAndRetry?.retry

}
