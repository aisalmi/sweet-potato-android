package com.hagergroup.sample

import android.content.Intent
import android.content.IntentFilter
import android.view.MenuItem
import androidx.viewbinding.ViewBinding
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener
import com.hagergroup.sweetpotato.content.SweetSharedFlowListenerProvider
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleActivity<ViewBindingClass : ViewBinding>
  : SweetAppCompatActivity<SampleActivityAggregate, ViewBindingClass>(),
    SweetActivityConfigurable, SweetActionBarConfigurable,
    SweetSharedFlowListenerProvider
{

  companion object
  {

    const val SAMPLE_ACTION = "sampleAction"

  }

  override fun getSweetSharedFlowListener(): SweetSharedFlowListener
  {
    return object: SweetSharedFlowListener
    {

      override fun getIntentFilter(): IntentFilter =
          IntentFilter(SampleActivity.SAMPLE_ACTION)

      override fun onCollect(intent: Intent)
      {
        if(intent.action == SampleActivity.SAMPLE_ACTION)
        {
          Timber.d("SAMPLE ACTION RECEIVED")
        }
      }

    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return if (item.itemId == android.R.id.home)
    {
      onBackPressed()
      true
    }
    else
    {
      super.onOptionsItemSelected(item)
    }
  }

}