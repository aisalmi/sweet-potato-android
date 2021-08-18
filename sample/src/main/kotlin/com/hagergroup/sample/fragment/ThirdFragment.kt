package com.hagergroup.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hagergroup.sample.R
import com.hagergroup.sample.SampleActivity
import com.hagergroup.sample.adapter.MyAdapter
import com.hagergroup.sample.databinding.FragmentThirdBinding
import com.hagergroup.sample.viewmodel.ThirdFragmentViewModel
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.12.12
 */
//@SweetFragmentAnnotation(layoutId = R.layout.fragment_third, fragmentTitleId = R.string.app_name, viewModelClass = ThirdFragmentViewModel::class, viewModelContext = SweetFragmentAnnotation.ViewModelContext.Activity)
class ThirdFragment
  : SampleFragment<FragmentThirdBinding, ThirdFragmentViewModel>(),
    View.OnClickListener
{

  companion object
  {

    const val MY_EXTRA = "myExtra"

    const val ANOTHER_EXTRA = "anotherExtra"

  }

  override fun layoutId(): Int =
      R.layout.fragment_third

  override fun fragmentTitleId(): Int =
      R.string.app_name

  override fun getViewModelClass(): Class<ThirdFragmentViewModel> =
      ThirdFragmentViewModel::class.java


  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    viewDatabinding?.refreshError?.setOnClickListener(this)
    viewDatabinding?.refreshInternetError?.setOnClickListener(this)
    viewDatabinding?.observableField?.setOnClickListener(this)
    viewDatabinding?.backstack?.setOnClickListener(this)

    viewDatabinding?.list?.setHasFixedSize(true)
  }

  override fun onLoadedState()
  {
    super.onLoadedState()

    viewDatabinding?.list?.adapter = MyAdapter(viewModel?.persons ?: emptyList())
  }

  override fun onClick(view: View?)
  {
    when (view)
    {
      viewDatabinding?.refreshError         ->
      {
        viewModel?.apply {
          throwError = true
          refreshViewModel(true) {
            Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
          }
        }
      }
      viewDatabinding?.refreshInternetError ->
      {
        viewModel?.apply {
          throwInternetError = true
          refreshViewModel(true) {
            Toast.makeText(context, "Finish !", Toast.LENGTH_SHORT).show()
          }
        }
      }
      viewDatabinding?.observableField      ->
      {
        (viewModel as ThirdFragmentViewModel).anotherString.postValue(UUID.randomUUID().toString())
      }
      viewDatabinding?.backstack            ->
      {
        (activity as? SampleActivity<*>)?.getAggregate()?.addOrReplaceFragment(BackstackFragment::class, R.id.fragmentContainer, true, "BackstackFragment", null, null, SweetActivityAggregate.FragmentTransactionType.Add)
      }
    }
  }

  override fun getRetryView(): View? =
      viewDatabinding?.loadingErrorAndRetry?.retry

}
