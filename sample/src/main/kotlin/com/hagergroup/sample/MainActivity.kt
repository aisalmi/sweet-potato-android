package com.hagergroup.sample

import androidx.appcompat.app.ActionBarDrawerToggle
import com.hagergroup.sample.fragment.MainFragment
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetActivityAnnotation(contentViewId = R.layout.activity_main, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = MainFragment::class)
class MainActivity
  : SampleActivity()
{

  private lateinit var drawerToggle: ActionBarDrawerToggle

  //  override fun onCreate(savedInstanceState: Bundle?)
  //  {
  //    super.onCreate(savedInstanceState)
  //
  //    drawerToggle = ActionBarDrawerToggle(this, drawerLayout, null, R.string.app_name, R.string.app_name)
  //    drawerLayout.addDrawerListener(drawerToggle)
  //  }
  //
  //  override fun onPostCreate(savedInstanceState: Bundle?)
  //  {
  //    super.onPostCreate(savedInstanceState)
  //    drawerToggle.syncState()
  //  }
  //
  //  override fun onOptionsItemSelected(item: MenuItem): Boolean
  //  {
  //    if (drawerToggle.onOptionsItemSelected(item))
  //    {
  //      return true
  //    }
  //
  //    return super.onOptionsItemSelected(item)
  //  }

}
