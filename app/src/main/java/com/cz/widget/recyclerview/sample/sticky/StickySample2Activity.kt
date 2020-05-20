package com.cz.widget.recyclerview.sample.sticky

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.adapter.wrapper.sticky.StickyWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.sticky.adapter.StickySample2Adapter
import kotlinx.android.synthetic.main.activity_sticky_sample2.*

@SampleSourceCode(".*StickySample2.*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/StickyAdapter.md")
@RefRegister(title=R.string.sticky_sample2,desc = R.string.sticky_sample2_desc,category = R.string.sticky,priority = 1)
class StickySample2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_sample2)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.changeDuration=300

        val list= mutableListOf<ListData>()
        for(i in 0 until 100){
            list.add(ListData("Data:$i",i))
        }
        val adapter = StickySample2Adapter(this, list)
        adapter.setCondition { item, _ -> 5 < item.position&&item.position%5==0 }

        recyclerView.adapter= StickyWrapperAdapter(adapter)

        button.setOnClickListener {
            adapter.remove(0,1)
        }
    }

    private class ListData{
        val item:String
        val position:Int
        constructor(item:String,position:Int){
            this.item=item
            this.position=position;
        }

        override fun toString(): String {
            return item
        }
    }

}
