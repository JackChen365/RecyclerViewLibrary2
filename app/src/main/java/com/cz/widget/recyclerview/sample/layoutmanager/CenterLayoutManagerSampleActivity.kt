package com.cz.widget.recyclerview.sample.layoutmanager

import android.os.Bundle
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.HorizontalSimpleAdapter
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_center_layout_manager_sample.*

@SampleSourceCode
@RefRegister(title=R.string.center_layout_manager,desc = R.string.center_layout_manager_desc,category = R.string.layout_manager)
class CenterLayoutManagerSampleActivity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_center_layout_manager_sample)

        val layoutManager=CenterLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val list= mutableListOf<String>()
        for(i in 0 until 30){
            list.add("Data:$i")
        }
        val adapter= SimpleAdapter(this,list)
        recyclerView.adapter =adapter
        val debugItemDecoration = DebugItemDecoration(this)
        debugItemDecoration.setOrientation(CenterLayoutManager.VERTICAL)
        debugItemDecoration.attachToView(recyclerView)

        cycleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            layoutManager.isInfiniteCycle=isChecked;
        }
        buttonLayout.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                0->{
                    layoutManager.orientation=CenterLayoutManager.HORIZONTAL
                    debugItemDecoration.setOrientation(CenterLayoutManager.HORIZONTAL)
                    val adapter= HorizontalSimpleAdapter(this,list)
                    recyclerView.adapter =adapter
                }
                1->{
                    layoutManager.orientation=CenterLayoutManager.VERTICAL
                    debugItemDecoration.setOrientation(CenterLayoutManager.VERTICAL)
                    val adapter= SimpleAdapter(this,list)
                    recyclerView.adapter =adapter
                }
            }
        }
    }
}
