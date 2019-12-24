package com.hagergroup.sample

import android.view.MenuItem
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleActivity
  : SweetAppCompatActivity<SampleActivityAggregate>()
{

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