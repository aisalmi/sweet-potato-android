package com.hagergroup.sample

import android.view.MenuItem
import androidx.viewbinding.ViewBinding
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleActivity<ViewBindingClass : ViewBinding>
  : SweetAppCompatActivity<SampleActivityAggregate, ViewBindingClass>(),
    SweetActivityConfigurable, SweetActionBarConfigurable
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