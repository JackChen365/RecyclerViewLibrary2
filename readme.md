## RecyclerViewLibrary

### Sample file

[APK file](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/file/app-debug.apk?raw=true)

### Compile

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

implementation 'com.github.momodae.RecyclerViewLibrary2:layoutmanager:1.0.1'
implementation 'com.github.momodae.RecyclerViewLibrary2:pulltorefresh:1.0.1'
implementation 'com.github.momodae.RecyclerViewLibrary2:adapter:1.0.1'
implementation 'com.github.momodae.RecyclerViewLibrary2:library:1.0.1'
```

### Tools version

Most important: all the libraries were use the AndroidX, not the appcompat.

```
//The build.gradle
compileSdkVersion 29
buildToolsVersion "29.0.2"


defaultConfig {
    minSdkVersion 19
    targetSdkVersion 29
}

implementation 'androidx.appcompat:appcompat:1.1.0'
implementation 'androidx.recyclerview:recyclerview:1.1.0'

For module:library
Because of the nested scroll didn't support API-19 so we upgrade this module to API-21
```


Tips:
* All the libraries use java purely.
* Only the sample module use kotlin.
* The demonstrate module base on this library:[AndroidSampleLibrary](https://github.com/momodae/AndroidSampleLibrary). Just check all the activities. Do not try to find a initialize application or something like that.


### About PullToRefreshRecyclerView
![image1](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/library/image1.gif?raw=true)

![image1](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/library/image2.gif?raw=true)

Check this document:[readme](library/readme.md)

### About the PullToRefresh.

![image1](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/pulltorefresh/image1.gif?raw=true)

![image2](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/pulltorefresh/image2.gif?raw=true)

![image3](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/pulltorefresh/image3.gif?raw=true)

![image4](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/pulltorefresh/image4.gif?raw=true)


Check this document:[readme](pulltorefresh/readme.md)

### About LayoutManager
* ViewPager
* Gallery
* Wheel

![viewpager](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/layoutmanager/viewpager.gif?raw=true)

![gallery](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/layoutmanager/gallery.gif?raw=true)

![wheel](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/layoutmanager/wheel.gif?raw=true)


Check this document:[readme](layoutmanager/readme.md)

### About the support adapters.

* HeaderWrapperAdapter
* DynamicWrapperAdapter
* SelectWrapperAdapter
* DragWrapperAdapter
* StickyWrapperAdapter
* BaseAdapter
* ExpandAdapter
* TreeAdapter
* FilterAdapter
* CursorAdapter
* StickyAdapter


![cursor_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/cursor_adapter.gif?raw=true)

![drag_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/drag_adapter.gif?raw=true)

![dynamic_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/dynamic_adapter.gif?raw=true)

![expand_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/expand_adapter.gif?raw=true)

![filter_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/filter_adapter.gif?raw=true)

![header_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/header_adapter.gif?raw=true)

![select_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/select_adapter.gif?raw=true)

![sticky_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/sticky_adapter.gif?raw=true)

![tree_adapter](https://gitee.com/JackChen2/library-resources/raw/master/RecyclerViewLibrary/image/adapter/tree_adapter.gif?raw=true)

Check this document:[readme](adapter/readme.md)


### Problems

* About module:library

It's wired that I have another version of this library. It's totally written by the Kotlin.
And that module word in API-19 well. For some reason, I got this problem. It tells me the Nested scroll not support in API-19.
It doesn't matter. The Androidx if for modern application. So we support from API-21 is fine.
[android-nosuchmethoderror-android-support-v7-widget-recyclerview-onnestedscroll](https://stackoverflow.com/questions/50198392/android-nosuchmethoderror-android-support-v7-widget-recyclerview-onnestedscroll)
