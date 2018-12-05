package com.hagergroup.sample.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hagergroup.sample.R
import com.hagergroup.sample.SecondActivity
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.net.UnknownHostException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetFragmentAnnotation(layoutId = R.layout.fragment_main, fragmentTitleId = R.string.expand_button_title, surviveOnConfigurationChanged = true)
class MainFragment
  : SampleFragment(),
    View.OnClickListener, SweetBroadcastListenerProvider
{

  companion object
  {

    const val MY_ACTION = "myAction"

  }

  private var throwError = false

  private var throwInternetError = false

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
          context?.toast("Click !")
        }
      }

    }
  }

  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
    super.onRetrieveModel()

    Thread.sleep(1_000)

    if (throwError == true)
    {
      throwError = false

      throw ModelUnavailableException("Cannot retrieve the model")
    }

    if (throwInternetError == true)
    {
      throwInternetError = false

      throw ModelUnavailableException("Cannot retrieve the model", UnknownHostException())
    }
  }

  override fun onBindModel()
  {
    super.onBindModel()

    binding.setOnClickListener(this)
    click.setOnClickListener(this)
    refreshLoading.setOnClickListener(this)
    refreshNoLoading.setOnClickListener(this)
    refreshError.setOnClickListener(this)
    refreshInternetError.setOnClickListener(this)
  }

  override fun onClick(view: View?)
  {
    if (view == binding)
    {
      context?.startActivity<SecondActivity>(SecondFragment.MY_EXTRA to "hey !")
    }
    else if (view == click)
    {
      context?.let {
        LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(MainFragment.MY_ACTION))
      }
    }
    else if (view == refreshLoading)
    {
      refreshModelAndBind(Runnable {
        context?.toast("Finish !")
      })
    }
    else if (view == refreshNoLoading)
    {
      getAggregate()?.getLoadingErrorAndRetryAggregate()?.doNotDisplayLoadingViewNextTime()
      refreshModelAndBind(Runnable {
        context?.toast("Finish !")
      })
    }
    else if (view == refreshError)
    {
      throwError = true
      refreshModelAndBind(Runnable {
        context?.toast("Finish !")
      })
    }
    else if (view == refreshInternetError)
    {
      throwInternetError = true
      refreshModelAndBind(Runnable {
        context?.toast("Finish !")
      })
    }
  }

}
