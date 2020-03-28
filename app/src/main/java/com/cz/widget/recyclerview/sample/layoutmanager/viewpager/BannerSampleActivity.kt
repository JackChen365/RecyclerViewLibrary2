package com.cz.widget.recyclerview.sample.layoutmanager.viewpager

import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.layoutmanager.viewpager.CycleViewPager
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.layoutmanager.viewpager.adapter.ViewPagerImageAdapter
import kotlinx.android.synthetic.main.activity_banner_sample.*
import java.util.*

@SampleSourceCode(".*(Banner|Adapter).*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/layoutmanager/document/en/CycleViewPager.md")
@RefRegister(title = R.string.view_pager_banner,desc = R.string.view_pager_banner_desc,category = R.string.view_pager)
class BannerSampleActivity : SampleAppCompatActivity() {

    private var landscape = false
    private var lastValue: Float = 0f

    private val isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) ==
            ViewCompat.LAYOUT_DIRECTION_RTL

    private val CycleViewPager.isHorizontal: Boolean
        get() {
            return orientation == CycleViewPager.ORIENTATION_HORIZONTAL
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_sample)
        landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val imageAdapter = ViewPagerImageAdapter(
            this, listOf(R.mipmap.cartoon_image1, R.mipmap.cartoon_image2, R.mipmap.cartoon_image3))
        cycleViewPager.isInfiniteCycle=true
        cycleViewPager.isUserInputEnabled=false
        cycleViewPager.adapter = imageAdapter

        dragView.setOnTouchListener { _, event ->
            handleOnTouchEvent(event)
        }

        buttonLayout.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                0->{
                    cycleViewPager.startAutoScroll()
                }
                1->{
                    cycleViewPager.stopAutoScroll()
                }
            }
        }
    }

    private fun mirrorInRtl(f: Float): Float {
        return if (isRtl) -f else f
    }

    private fun getValue(event: MotionEvent): Float {
        return if (landscape) event.y else mirrorInRtl(event.x)
    }

    private fun handleOnTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastValue = getValue(event)
                cycleViewPager.beginFakeDrag()
            }

            MotionEvent.ACTION_MOVE -> {
                val value = getValue(event)
                val delta = value - lastValue
                cycleViewPager.fakeDragBy(if (cycleViewPager.isHorizontal) mirrorInRtl(delta) else delta)
                lastValue = value
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                cycleViewPager.endFakeDrag()
            }
        }
        return true
    }
}
