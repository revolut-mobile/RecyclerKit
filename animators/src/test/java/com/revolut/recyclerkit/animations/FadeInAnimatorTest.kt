package com.revolut.recyclerkit.animations

import android.app.Activity
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revolut.recyclerkit.animations.holder.AnimateChangeViewHolder
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class FadeInAnimatorTest {

    private val fadeInAnimator = FadeInAnimator()

    @Test
    fun `animateAppearance and runPendingAnimations trigger addAnimation`() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        val activity = activityController.get()
        val view = TextView(activity)

        val vh = TestViewHolder(view)

        fadeInAnimator.animateAppearance(
            vh,
            null,
            RecyclerView.ItemAnimator.ItemHolderInfo()
        )
        assertEquals(0.0f, view.alpha) //alpha is set to zero in preAnimate method
        assertEquals(true, fadeInAnimator.isRunning)

        fadeInAnimator.runPendingAnimations()
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(10, TimeUnit.MILLISECONDS)

        assertEquals(1.0f, view.alpha)
        assertEquals(false, fadeInAnimator.isRunning)
    }

    @Test
    fun `WHEN animateChange is called with the same old-new VHs and same from-to X-Y THEN animation is not triggered`() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        val activity = activityController.get()
        val view = TextView(activity)

        val vhOld = TestAnimateChangeViewHolder(view, true) {}
        val didAnimate = fadeInAnimator.animateChange(vhOld, vhOld, 0, 0, 0, 0)

        assertEquals(
            false, didAnimate
        )
        assertEquals(
            false, fadeInAnimator.isRunning
        )
    }

    @Test
    fun `WHEN animateChange ends THEN vhOld translations and alpha are restored`() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        val activity = activityController.get()

        val viewOld = TextView(activity)
        val viewNew = TextView(activity)

        val vhOld = TestViewHolder(viewOld)
        val vhNew = TestViewHolder(viewNew)

        val didAnimate = fadeInAnimator.animateChange(
            vhOld, vhNew,
            fromX = 0, fromY = 0,
            toX = 100, toY = 100
        )
        assertEquals(true, didAnimate)

        fadeInAnimator.runPendingAnimations()

        Shadows.shadowOf(Looper.getMainLooper()).idleFor(100, TimeUnit.MILLISECONDS)

        //this is the end-state of animation when all attributes are returned to their initial values
        //unfortunately there seems to be no way to test animation progress in Robolectric 4
        assertEquals(0.0f, vhOld.itemView.translationY)
        assertEquals(0.0f, vhOld.itemView.translationX)
        assertEquals(1.0f, vhOld.itemView.alpha)
    }

    private class TestViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class TestAnimateChangeViewHolder(
        view: View,
        private val canAnimateChange: Boolean,
        private val endChangeAnimation: () -> Unit
    ) : RecyclerView.ViewHolder(view), AnimateChangeViewHolder {

        override fun canAnimateChange(payloads: List<Any>): Boolean = canAnimateChange

        override fun endChangeAnimation(holder: RecyclerView.ViewHolder) = endChangeAnimation()
    }

}