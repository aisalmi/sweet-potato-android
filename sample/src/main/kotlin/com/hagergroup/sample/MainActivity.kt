package com.hagergroup.sample

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.hagergroup.sample.databinding.ActivityMainBinding
import com.hagergroup.sample.fragment.MainFragment
import com.hagergroup.sweetpotato.app.SweetConnectivityListener
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener
import com.hagergroup.sweetpotato.content.SweetSharedFlowListenersProvider
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
//@SweetActivityAnnotation(contentViewId = R.layout.activity_main, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = MainFragment::class, canRotate = true)
//@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Drawer)
class MainActivity
  : SampleActivity<ActivityMainBinding>(),
    SweetSharedFlowListenersProvider
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

  override fun getSharedFlowListenersCount(): Int =
      2

  override fun getSharedFlowListener(index: Int): SweetSharedFlowListener
  {
    return if (index == 0)
    {
      object : SweetSharedFlowListener
      {

        override fun getIntentFilter(): IntentFilter
        {
          return IntentFilter().apply {
            addAction(SweetConnectivityListener.CONNECTIVITY_CHANGED_ACTION)
          }
        }

        override fun onCollect(intent: Intent)
        {
          if (intent.action == SweetConnectivityListener.CONNECTIVITY_CHANGED_ACTION)
          {
            Toast.makeText(this@MainActivity, "has connectivity : '${intent.getBooleanExtra(SweetConnectivityListener.HAS_CONNECTIVITY_EXTRA, false)}'", Toast.LENGTH_LONG).show()
          }
        }
      }
    }
    else
    {
      object : SweetSharedFlowListener
      {
        override fun getIntentFilter(): IntentFilter
        {
          return IntentFilter().apply {
            addAction(MainFragment.MY_ACTION)
            addCategory(MainActivity::class.java.simpleName)
          }
        }

        override fun onCollect(intent: Intent)
        {
          if (intent.action == MainFragment.MY_ACTION)
          {
            Toast.makeText(this@MainActivity, "Click on Activity !", Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
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

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    if (drawerToggle?.onOptionsItemSelected(item) == true)
    {
      return true
    }

    return super.onOptionsItemSelected(item)
  }

}
