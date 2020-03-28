package com.cz.widget.recyclerview.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.ProjectRepository

@ProjectRepository("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
