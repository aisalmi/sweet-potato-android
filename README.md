[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://hagerdigitalfactory.visualstudio.com/Digital%20Factory%20Documentation/_apis/build/status/sweet-potato-android-master?branchName=master)](https://hagerdigitalfactory.visualstudio.com/Digital%20Factory%20Documentation/_build/latest?definitionId=36?branchName=master)
[ ![Download](https://api.bintray.com/packages/hagergroup/Maven/sweetpotato/images/download.svg) ](https://bintray.com/hagergroup/Maven/sweetpotato/_latestVersion)

# Sweet Potato Android

## For full documentation, check out the [wiki](https://github.com/hagergroup/sweet-potato-android/wiki)

Sweet Potato is the Android framework from Hager Group mainly written in Kotlin and only compatible with [Android Jetpack](https://developer.android.com/jetpack/). 

Sweet Potato provides a light-weight wrapper around Android components that contains everyting you need :

|  Sweet Potato |
|---------------|
Easy integration
Lifecycle management
Centralized exception handling
Activity redirection
Events interception
Application helper
Threading
Broadcasting
MVVM with databinding or MVC ready

Our goal is to make building products easier, faster, and more fun from the simplest of screens to the most complex in your app.

## Installation

Gradle is the only supported build configuration, so just add the dependency to your project `build.gradle` file :

```groovy
dependencies 
{
  implementation "com.hagergroup:sweetpotato:0.1"
}
```

The latest version of Sweet Potato is [ ![Download](https://api.bintray.com/packages/hagergroup/Maven/sweetpotato/images/download.svg) ](https://bintray.com/hagergroup/Maven/sweetpotato/_latestVersion)

## Components to Know

|             |  Conductor Components |
------|------------------------------
**SweetLoadingAndErrorActivity** | A basis class for activities that implements the loading and the error mechanism. This component is an `AppCompatActivity` wrapper that gives you all of the lifecycle management features
**SweetFragment** and **SweetViewModelBindingFragment** | A basis class for fragments that implements the loading and the error mechanism. These components are `Fragment` wrappers that give you all of the lifecycle management features
**Interceptor** | A component which handles in one place most of the entities (`AppCompatActivity` and `Fragment`) life cycle events through a dedicated class.
**Redirector** | A component which controls if an Activity should always be displayed before another one
**SweetExceptionHandler** | A component which centralizes exception handling. Whenever an exception is thrown during the application, it can be handled in a centralized and secure way whenever an exception is thrown during the application, it can be handled in a centralized and secure way

## Getting Started

### Minimal Activity implementation

```kotlin
@SweetActivityAnnotation(contentViewId = R.layout.activity_sample, fragmentPlaceholderId = R.id.fragmentContainer, fragmentClass = SampleFragment::class, canRotate = true)
@SweetActionBarAnnotation(actionBarBehavior = SweetActionBarAnnotation.ActionBarBehavior.Up)
class SampleActivity
  : SweetLoadingAndErrorActivity<SampleActivityAggregate, SampleFragmentAggregate>()
{

  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
  }

  override fun onBindModel()
  {
  }

}

```

### Minimal Fragment implementation

```kotlin
@SweetLoadingAndErrorAnnotation
@SweetSendLoadingIntentAnnotation
@SweetViewModelBindingFragmentAnnotation(layoutId = R.layout.fragment_sample, fragmentTitleId = R.string.fragment_title, viewModelClass = FragmentViewModel::class, surviveOnConfigurationChanged = false, viewModelContext = SweetViewModelBindingFragmentAnnotation.ViewModelContext.Activity)
class SampleFragment
  : SweetViewModelBindingFragment<SampleFragmentAggregate, FragmentSecondBinding>(),
{

@Throws(ModelUnavailableException::class)
  override fun computeViewModel()
  {
    // do your async work here (http request, etc.)
    // throw a ModelUnavailableException in case of issue
    // or compute your ViewModel

    (viewModel as? FragmentViewModel)?.apply {
      myString = "a String"
      anotherString.set("a String again")
    }
  }

  override fun getBindingVariable(): Int =
      com.hagergroup.sample.BR.model

}
```

### Minimal ViewModel implementation

```kotlin
class SecondFragmentViewModel
  : SweetViewModel()
{

  var myString: String? = null

  val anotherString = ObservableField<String>()

}
```

### Sample app

[Sample app](https://github.com/hagergroup/sweet-potato-android/tree/master/sample) - Shows how to use all basic and most advanced functions of Sweet Potato.

## License

This library is available under the MIT license. See the LICENSE file for more info.

## Author

This library is developed by the Digital Factory of the [Hager Group](http://www.hagergroup.com/).

## For full documentation, check out the [wiki](https://github.com/hagergroup/sweet-potato-android/wiki)
