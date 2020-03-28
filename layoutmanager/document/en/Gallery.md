## Gallery

> This is gallery...

Be careful. If there are less than three views.
We will create the view twice. Like this example: [A B] <br>
If you want to loop this data. We will created each item twice.<br>
The first time looks like this:[B,A,B] Then after you move to the next position. It will like this:[A,B,A]<br>


#### Example classes:

* [GalleryImageAdapter](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/gallery/GalleryImageAdapter.kt)
* [GallerySampleActivity](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/wheel/GallerySampleActivity.kt)

#### Image
![gallery](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/layoutmanager/gallery.gif?raw=true)

### Usage

```
//Change the current position manually
gallery.setCurrentItem(position)

//Add scroll change listener.
gallery.addOnPageChangeCallback(object : AbsCycleLayout.OnPageChangeCallback(){
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        numberTextView?.text = "Position:$position"
    }
})

//The page transform
gallery.setPageTransformer { page, fraction ->
    page.scaleX = 0.8f+0.2f*fraction
    page.scaleY = 0.8f+0.2f*fraction
    page.alpha=0.1f+0.9f*fraction
}
```

Gallery is base on AbsCycleLayout. So take a look at that class file. The layout manager is actually the CenterLayoutManager. nothing special
