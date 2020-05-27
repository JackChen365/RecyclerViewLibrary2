package com.cz.widget.recyclerview.sample.layoutmanager.wheel

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.layoutmanager.widget.AbsCycleLayout
import com.cz.widget.recyclerview.sample.R
import com.google.android.material.animation.ArgbEvaluatorCompat
import kotlinx.android.synthetic.main.activity_wheel_sample.*
import java.text.DecimalFormat
import java.util.*

@SampleSourceCode
@SampleDocument("Wheel.md")
@RefRegister(title = R.string.wheel,desc = R.string.wheel_desc,category = R.string.layout_manager)
class WheelSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wheel_sample)


        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                when (seekBar.id) {
                    R.id.seekYear -> yearText.text = getString(R.string.year_value, 2010 + progress)
                    R.id.seekMonth -> monthText.text = getString(R.string.month_value, progress + 1)
                    R.id.seekDay -> dayText.text = getString(R.string.day_value, progress + 1)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        }
        seekYear.setOnSeekBarChangeListener(listener)
        seekMonth.setOnSeekBarChangeListener(listener)
        seekDay.setOnSeekBarChangeListener(listener)
        scrollButton.setOnClickListener {
            wheel1.setCurrentItem(seekYear.progress)
            wheel2.setCurrentItem(seekMonth.progress)
            wheel3.setCurrentItem(seekDay.progress)
        }

        val calendar = Calendar.getInstance()
        val items = LinkedList<Int>()
        for (i in 0..9) {
            calendar.add(Calendar.YEAR, -1)
            items.offerFirst(calendar.get(Calendar.YEAR))
        }
        val monthItems = (1..12).toList()
        val dayItems = (1..30).toList()
        val dateAdapter1 = WheelVerticalDateAdapter(this, "00", items)
        val dateAdapter2 = WheelVerticalDateAdapter(this, "00", monthItems)
        val dateAdapter3 = WheelHorizontalDateAdapter(this, "00", dayItems)

        wheel1.adapter = dateAdapter1
        wheel2.adapter = dateAdapter2
        wheel3.adapter = dateAdapter3

        wheel1.addItemDecoration(CenterItemDecoration(this))
        wheel2.addItemDecoration(CenterItemDecoration(this))
        wheel3.addItemDecoration(CenterItemDecoration(this))

        val formatter = DecimalFormat("00")
        val onSelectPositionChangedListener = object : AbsCycleLayout.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(-1!=wheel1.currentItem&&-1!=wheel2.currentItem&&-1!=wheel3.currentItem){
                    val year = dateAdapter1.getItem(wheel1.currentItem)
                    val month = dateAdapter2.getItem(wheel2.currentItem)
                    val day = dateAdapter3.getItem(wheel3.currentItem)
                    dateText.text = getString(
                        R.string.date_value, year,
                        formatter.format(month),
                        formatter.format(day)
                    )
                }
            }
        }
        wheel1.addOnPageChangeCallback(onSelectPositionChangedListener)
        wheel2.addOnPageChangeCallback(onSelectPositionChangedListener)
        wheel3.addOnPageChangeCallback(onSelectPositionChangedListener)

        cycleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            wheel1.setInfiniteCycle(isChecked)
            wheel2.setInfiniteCycle(isChecked)
            wheel3.setInfiniteCycle(isChecked)
        }
        val evaluatorCompat = ArgbEvaluatorCompat.getInstance()
        val pageTransformer = AbsCycleLayout.PageTransformer { page, fraction ->
            Log.i("test","fraction:"+fraction)
            val textView = page as TextView
            textView.scaleX=1f+0.4f*fraction
            textView.scaleY=1f+0.4f*fraction
            textView.setTextColor(evaluatorCompat.evaluate(fraction,Color.WHITE, Color.RED))
        }
        wheel1.setPageTransformer(pageTransformer)
        wheel2.setPageTransformer(pageTransformer)
        wheel3.setPageTransformer(pageTransformer)
    }
}
