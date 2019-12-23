package com.hagergroup.sample.app

import androidx.appcompat.app.AppCompatActivity
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleActivityAggregate(activity: AppCompatActivity, activityAnnotation: SweetActivityAnnotation?, actionBarAnnotation: SweetActionBarAnnotation?)
  : SweetActivityAggregate(activity, activityAnnotation, actionBarAnnotation)