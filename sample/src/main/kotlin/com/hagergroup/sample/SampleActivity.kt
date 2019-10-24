package com.hagergroup.sample

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

  @Throws(ModelUnavailableException::class)
  override suspend fun onRetrieveModel()
  {
  }

  override fun onBindModel()
  {
  }

}
