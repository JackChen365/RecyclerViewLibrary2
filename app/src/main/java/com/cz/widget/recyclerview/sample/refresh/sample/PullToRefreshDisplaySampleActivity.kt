package com.cz.widget.recyclerview.sample.refresh.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.refresh.header.DisplayHeader
import kotlinx.android.synthetic.main.activity_pull_to_refresh_display_sample.*

@SampleSourceCode
@RefRegister(title=R.string.pull_to_refresh_title2,desc = R.string.pull_to_refresh_desc2,category = R.string.pull_to_refresh)
class PullToRefreshDisplaySampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_refresh_display_sample)
        pullToRefreshLayout.pullToRefreshHeader=DisplayHeader()
    }
}
