package com.cz.widget.recyclerview.sample.refresh.sample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.pulltorefresh.PullToRefreshState
import com.cz.widget.recyclerview.sample.R
import kotlinx.android.synthetic.main.activity_pull_to_refresh_vector_header_sample.*


@SampleSourceCode(".*PullToRefreshVectorHeaderSampleActivity.*")
@RefRegister(title=R.string.pull_to_refresh_header_vector,desc = R.string.pull_to_refresh_vector_header_desc,category = R.string.pull_to_refresh,priority = 1)
class PullToRefreshVectorHeaderSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_refresh_vector_header_sample)

        startButton.setOnClickListener {
            startVectorAnimator()
        }
    }

    private fun startVectorAnimator() {
        //pull animator
        val pullAnimator = ValueAnimator.ofFloat(1f)
        pullAnimator.duration = 1000
        pullAnimator.interpolator=LinearInterpolator()
        pullAnimator.addUpdateListener {
            headerVectorView.onScrollOffset(it.animatedFraction)
        }
        pullAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                headerVectorView.onRefreshStateChange(PullToRefreshState.START_PULL)
            }
        })
        val loadAnimator=ValueAnimator.ofFloat(1f)
        loadAnimator.duration = 1000
        loadAnimator.repeatCount=3
        loadAnimator.repeatMode=ValueAnimator.RESTART
        loadAnimator.interpolator=LinearInterpolator()
        loadAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                headerVectorView.onRefreshStateChange(PullToRefreshState.REFRESHING)
            }
        })

        val completeAnimator=ValueAnimator.ofFloat(1f)
        completeAnimator.duration = 2000
        completeAnimator.interpolator=LinearInterpolator()
        completeAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                headerVectorView.onRefreshStateChange(PullToRefreshState.REFRESHING_COMPLETE)
            }
        })


        val animatorSet=AnimatorSet()
        animatorSet.playSequentially(pullAnimator,loadAnimator,completeAnimator)
        animatorSet.start()
    }
}
