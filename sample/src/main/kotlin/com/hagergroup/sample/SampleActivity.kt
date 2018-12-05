package com.hagergroup.sample

import android.view.MenuItem
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetLoadingAndErrorActivity
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleActivity
  : SweetLoadingAndErrorActivity<SampleActivityAggregate, SampleFragmentAggregate>()
{

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      android.R.id.home ->
      {
        //In order to respect the Android navigation guidelines, we should use the NavUtils class but...
        // NavUtils.navigateUpFromSameTask(this);
        finish()
        super.onOptionsItemSelected(item)
      }
      else              ->
      {
        super.onOptionsItemSelected(item)
      }
    }
  }

  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
  }

  override fun onBindModel()
  {
  }

}
