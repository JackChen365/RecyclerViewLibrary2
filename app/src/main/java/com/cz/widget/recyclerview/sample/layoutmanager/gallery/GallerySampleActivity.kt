package com.cz.widget.recyclerview.sample.layoutmanager.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.layoutmanager.widget.AbsCycleLayout
import com.cz.widget.recyclerview.sample.R
import kotlinx.android.synthetic.main.activity_gallery_sample.*

@SampleSourceCode
@SampleDocument("Gallery.md")
@RefRegister(title = R.string.gallery,desc = R.string.gallery_desc,category = R.string.layout_manager)
class GallerySampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_sample)

        val imageAdapter = GalleryImageAdapter(
            this, listOf(
                R.mipmap.cartoon_image1,
                R.mipmap.cartoon_image2, R.mipmap.cartoon_image3, R.mipmap.cartoon_image4,
                R.mipmap.cartoon_image5, R.mipmap.cartoon_image6, R.mipmap.cartoon_image7,
                R.mipmap.cartoon_image8, R.mipmap.cartoon_image9
            )
        )
        gallery.adapter = imageAdapter
        gallery.addOnPageChangeCallback(object : AbsCycleLayout.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                numberTextView?.text = "Position:$position"
            }
        })
        gallery.setPageTransformer { page, fraction ->
            page.scaleX = 0.8f+0.2f*fraction
            page.scaleY = 0.8f+0.2f*fraction
            page.alpha=0.1f+0.9f*fraction
        }
    }
}
