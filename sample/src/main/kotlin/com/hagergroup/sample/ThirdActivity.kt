package com.hagergroup.sample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.hagergroup.sample.databinding.ActivityThirdBinding
import com.hagergroup.sample.fragment.ThirdFragment
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
//@SweetActivityAnnotation(contentViewId = R.layout.activity_second, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = ThirdFragment::class, canRotate = true)
//@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class ThirdActivity
  : SampleActivity<ActivityThirdBinding>(),
    FragmentManager.OnBackStackChangedListener
{

  private var drawerToggle: ActionBarDrawerToggle? = null

  override fun inflateViewBinding(): ActivityThirdBinding =
      ActivityThirdBinding.inflate(layoutInflater)

  override fun fragmentPlaceholderId(): Int =
      R.id.fragmentContainer

  override fun fragmentClass(): KClass<out SweetFragment<*, *, *>> =
      ThirdFragment::class

  override fun canRotate(): Boolean =
      true

  override fun actionBarBehavior(): SweetActionBarConfigurable.ActionBarBehavior =
      SweetActionBarConfigurable.ActionBarBehavior.Drawer

  override fun toolbar(): Toolbar? =
      viewBinding?.toolbar

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    supportFragmentManager.addOnBackStackChangedListener(this)
  }

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

  override fun onBackStackChanged()
  {
    Timber.d("current opened fragment: ${getAggregate()?.openedFragment}")
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