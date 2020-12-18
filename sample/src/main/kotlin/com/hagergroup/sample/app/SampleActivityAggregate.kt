package com.hagergroup.sample.app

import androidx.appcompat.app.AppCompatActivity
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleActivityAggregate(activity: AppCompatActivity, activityConfigurable: SweetActivityConfigurable?, actionBarConfigurable: SweetActionBarConfigurable?)
  : SweetActivityAggregate(activity, activityConfigurable, actionBarConfigurable)