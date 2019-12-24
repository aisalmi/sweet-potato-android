package com.hagergroup.sample.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hagergroup.sample.R
import com.hagergroup.sample.SecondActivity
import com.hagergroup.sample.databinding.FragmentMainBinding
import com.hagergroup.sample.viewmodel.MainFragmentViewModel
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider
import com.hagergroup.sweetpotato.coroutines.SweetCoroutines
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.delay
import timber.log.Timber
import java.net.URL

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_main, fragmentTitleId = R.string.app_name, viewModelClass = MainFragmentViewModel::class, surviveOnConfigurationChanged = true)
class MainFragment
  : SampleFragment<FragmentMainBinding, MainFragmentViewModel>(),
    View.OnClickListener, SweetBroadcastListenerProvider
{

  companion object
  {

    const val MY_ACTION = "myAction"

  }

  override fun getBroadcastListener(): SweetBroadcastListener
  {
    return object : SweetBroadcastListener
    {
      override fun getIntentFilter(): IntentFilter
      {
        return IntentFilter(MainFragment.MY_ACTION)
      }

      override fun onReceive(context: Context?, intent: Intent?)
      {
        if (intent?.action == MainFragment.MY_ACTION)
        {
          Toast.makeText(context, "Click !", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    openBinding.setOnClickListener(this)
    openBinding2.setOnClickListener(this)
    click.setOnClickListener(this)
    refreshLoading.setOnClickListener(this)
    refreshNoLoading.setOnClickListener(this)
    refreshError.setOnClickListener(this)
    refreshInternetError.setOnClickListener(this)
    coroutines.setOnClickListener(this)
    coroutinesError.setOnClickListener(this)
  }

  override fun onClick(view: View?)
  {
    if (view == openBinding)
    {
      val intent = Intent(context, SecondActivity::class.java).apply {
        putExtra(SecondFragment.MY_EXTRA, "hey !")
        putExtra(SecondFragment.ANOTHER_EXTRA, "Another Hey !")
      }

      startActivity(intent)
    }
    else if (view == openBinding2)
    {
      val intent = Intent(context, SecondActivity::class.java).apply {
        putExtra(ThirdFragment.MY_EXTRA, "go !")
        putExtra(ThirdFragment.ANOTHER_EXTRA, "Another go !")
      }

      startActivity(intent)
    }
    else if (view == click)
    {
      context?.let {
        LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(MainFragment.MY_ACTION))
      }
    }
    else if (view == refreshLoading)
    {
      getCastedViewModel()?.refreshViewModel(arguments,true, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      })
    }
    else if (view == refreshNoLoading)
    {
      getCastedViewModel()?.refreshViewModel(arguments,false, Runnable {
        Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
      })
    }
    else if (view == refreshError)
    {
      getCastedViewModel()?.apply {
        throwError = true
        getCastedViewModel()?.refreshViewModel(arguments,true, Runnable {
          Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
        })
      }
    }
    else if (view == refreshInternetError)
    {
      getCastedViewModel()?.apply {
        throwInternetError = true
        getCastedViewModel()?.refreshViewModel(arguments,true, Runnable {
          Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
        })
      }
    }
    else if (view == coroutines)
    {
      startSweetCoroutines()
    }
    else if (view == coroutinesError)
    {
      startSweetCoroutinesError()
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

}
