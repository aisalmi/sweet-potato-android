package com.hagergroup.sample

import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleActivity
  : SweetAppCompatActivity<SampleActivityAggregate>()