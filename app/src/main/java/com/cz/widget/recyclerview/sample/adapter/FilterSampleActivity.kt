package com.cz.widget.recyclerview.sample.adapter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleFilterAdapter
import kotlinx.android.synthetic.main.activity_adapter_filter_sample.*

@SampleSourceCode
@RefRegister(title=R.string.filter,desc = R.string.filter_desc,category = R.string.adapter)
class FilterSampleActivity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_filter_sample)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.changeDuration=300
        val list= mutableListOf<String>()
        for(i in 0 until 100){
            list.add("Data:$i")
        }
        val adapter = SimpleFilterAdapter(this, list)
        recyclerView.adapter = adapter

        editView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })
    }
}