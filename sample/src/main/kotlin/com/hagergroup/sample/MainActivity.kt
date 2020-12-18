package com.hagergroup.sample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.hagergroup.sample.databinding.ActivityMainBinding
import com.hagergroup.sample.fragment.MainFragment
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
//@SweetActivityAnnotation(contentViewId = R.layout.activity_main, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = MainFragment::class, canRotate = true)
//@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Drawer)
class MainActivity
  : SampleActivity<ActivityMainBinding>()
{

  private var drawerToggle: ActionBarDrawerToggle? = null

  override fun inflateViewBinding(): ActivityMainBinding =
      ActivityMainBinding.inflate(layoutInflater)

  override fun fragmentPlaceholderId(): Int =
      R.id.fragmentContainer

  override fun fragmentClass(): KClass<out SweetFragment<*, *, *>> =
      MainFragment::class

  override fun canRotate(): Boolean =
      false

  override fun actionBarBehavior(): SweetActionBarConfigurable.ActionBarBehavior =
      SweetActionBarConfigurable.ActionBarBehavior.Drawer

  override fun onRetrieveDisplayObjects()
  {
    super.onRetrieveDisplayObjects()

    drawerToggle = ActionBarDrawerToggle(this, viewBinding?.drawerLayout, null, R.string.app_name, R.string.app_name)

    drawerToggle?.let {
      viewBinding?.drawerLayout?.addDrawerListener(it)
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?)
  {
    super.onPostCreate(savedInstanceState)
    drawerToggle?.syncState()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    if (drawerToggle?.onOptionsItemSelected(item) == true)
    {
      return true
    }

    return super.onOptionsItemSelected(item)
  }

}
