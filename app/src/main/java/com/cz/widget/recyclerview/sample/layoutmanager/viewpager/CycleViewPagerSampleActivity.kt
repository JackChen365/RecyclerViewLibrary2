package com.cz.widget.recyclerview.sample.layoutmanager.viewpager

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.layoutmanager.viewpager.CycleViewPager
import com.cz.widget.recyclerview.layoutmanager.widget.AbsCycleLayout
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.layoutmanager.viewpager.adapter.ViewPagerImageAdapter
import com.cz.widget.recyclerview.sample.view.CheckLayout
import kotlinx.android.synthetic.main.activity_cycle_view_pager_sample.*

@SampleSourceCode(".*ViewPager.*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/layoutmanager/document/en/CycleViewPager.md")
@RefRegister(title = R.string.view_pager,desc = R.string.view_pager_desc,category = R.string.view_pager)
class CycleViewPagerSampleActivity : AppCompatActivity() {
    companion object {
        private const val ROTATE=0
        private const val ALPHA=1
        private const val SCALE=2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycle_view_pager_sample)
        val imageAdapter = ViewPagerImageAdapter(
            this, listOf(
                R.mipmap.cartoon_image1,
                R.mipmap.cartoon_image2, R.mipmap.cartoon_image3, R.mipmap.cartoon_image4,
                R.mipmap.cartoon_image5, R.mipmap.cartoon_image6, R.mipmap.cartoon_image7,
                R.mipmap.cartoon_image8, R.mipmap.cartoon_image9
            )
        )
        cycleViewPager.adapter = imageAdapter
        cycleViewPager.addOnPageChangeCallback(object :AbsCycleLayout.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                numberTextView?.text = "Position:$position"
            }
        })

        val numberList=(0 until 9).map { it.toString() }
        initControlPanel(numberList = numberList)
    }

    /**
     * 初始化控制面板
     */
    private fun initControlPanel(numberList: List<String>) {
        val viewPager=findViewById<CycleViewPager>(R.id.cycleViewPager)
        val disableCheckbox=findViewById<CheckBox>(R.id.disableCheckbox)
        val orientationSpinner=findViewById<Spinner>(R.id.orientationSpinner)
        val pageSpinner=findViewById<Spinner>(R.id.pageSpinner)
        val checkLayout=findViewById<CheckLayout>(R.id.checkLayout)
        disableCheckbox.isChecked = !viewPager.isUserInputEnabled
        //Enable or disable the user input.
        disableCheckbox.setOnCheckedChangeListener { _, isDisabled ->
            viewPager.isUserInputEnabled = !isDisabled
        }
        val orientationArray = arrayOf("horizontal", "vertical")
        val orientationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, orientationArray)
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        orientationSpinner.adapter = orientationAdapter
        orientationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewPager.orientation = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        //Smooth move to a position
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numberList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pageSpinner.adapter = adapter
        cycleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewPager.isInfiniteCycle=isChecked
        }
        pageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewPager.currentItem = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        //Combine all the page transformer animation.
        checkLayout.setOnCheckedListener { _, items, _ ->
            viewPager.setPageTransformer(getViewPagerPageTransformer(items))
        }
    }

    /**
     * 获得viewPager页动画转换
     */
    private fun getViewPagerPageTransformer(items: List<Int>): AbsCycleLayout.PageTransformer {
        return AbsCycleLayout.PageTransformer { page, fraction ->
            page.rotation = if (ROTATE in items) fraction * 360 else 0f
            page.alpha = if (ALPHA in items) 0.1f+0.9f*fraction else 1f
            if (SCALE in items) {
                page.scaleX = 0.6f+0.4f*fraction
                page.scaleY = 0.6f+0.4f*fraction
            } else {
                page.scaleX = 1f
                page.scaleY = 1f
            }
        }
    }
}
