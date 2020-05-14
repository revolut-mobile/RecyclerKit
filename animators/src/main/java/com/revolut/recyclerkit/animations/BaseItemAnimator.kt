package com.revolut.recyclerkit.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SimpleItemAnimator
import com.revolut.recyclerkit.animations.holder.AnimateAddViewHolder
import com.revolut.recyclerkit.animations.holder.AnimateChangeViewHolder
import com.revolut.recyclerkit.animations.holder.AnimateRemoveViewHolder
import com.revolut.recyclerkit.animations.internal.clear
import java.util.*
import kotlin.math.abs

/*
 * Copyright (C) 2019 Revolut
 * Copyright (C) 2018 Wasabeef
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
/**
 * @param supportsChangeAnimations - true: default change animations will be applied for ViewHolders
 * not implementing AnimatedChangeViewHolder, false: change animations are only applied for ViewHolders implementing AnimatedChangeViewHolder,
 * if they return false in canAnimateChange callback.
 * @param withCrossFade - if true - then default change animation will cross fade new view onto old view (old view alpha animated from 1.0 to 0.1),
 * if false: new view will fade in atop an old view (old view alpha is not animated).
 */
abstract class BaseItemAnimator(
    protected val interpolator: Interpolator = DecelerateInterpolator(),
    private val withCrossFade: Boolean = true,
    supportsChangeAnimations: Boolean = false
) : SimpleItemAnimator() {

    protected var addAnimations = ArrayList<ViewHolder>()
    protected var removeAnimations = ArrayList<ViewHolder>()
    private val pendingRemovals = ArrayList<ViewHolder>()
    private val pendingAdditions = ArrayList<ViewHolder>()
    private val pendingMoves = ArrayList<MoveInfo>()
    private val pendingChanges = ArrayList<ChangeInfo>()
    private val additionsList = ArrayList<ArrayList<ViewHolder>>()
    private val movesList = ArrayList<ArrayList<MoveInfo>>()
    private val changesList = ArrayList<ArrayList<ChangeInfo>>()
    private val moveAnimations = ArrayList<ViewHolder>()
    private val changeAnimations = ArrayList<ViewHolder>()

    companion object {
        private val DEBUG = false
    }

    init {
        this.supportsChangeAnimations = supportsChangeAnimations
    }

    private class MoveInfo constructor(
        var holder: ViewHolder,
        var fromX: Int,
        var fromY: Int,
        var toX: Int,
        var toY: Int
    )

    private class ChangeInfo private constructor(
        var oldHolder: ViewHolder?,
        var newHolder: ViewHolder?
    ) {

        var fromX: Int = 0
        var fromY: Int = 0
        var toX: Int = 0
        var toY: Int = 0

        constructor(
            oldHolder: ViewHolder?, newHolder: ViewHolder?, fromX: Int, fromY: Int, toX: Int,
            toY: Int
        ) : this(oldHolder, newHolder) {
            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY
        }

        override fun toString(): String {
            return "ChangeInfo{" +
                    "oldHolder=" + oldHolder +
                    ", newHolder=" + newHolder +
                    ", fromX=" + fromX +
                    ", fromY=" + fromY +
                    ", toX=" + toX +
                    ", toY=" + toY +
                    '}'.toString()
        }
    }

    override fun runPendingAnimations() {
        val removalsPending = pendingRemovals.isNotEmpty()
        val movesPending = pendingMoves.isNotEmpty()
        val changesPending = pendingChanges.isNotEmpty()
        val additionsPending = pendingAdditions.isNotEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            // nothing to animate
            return
        }
        // First, remove stuff
        for (holder in pendingRemovals) {
            doAnimateRemove(holder)
        }
        pendingRemovals.clear()
        // Next, move stuff
        if (movesPending) {
            val moves = ArrayList<MoveInfo>()
            moves.addAll(pendingMoves)
            movesList.add(moves)
            pendingMoves.clear()
            val mover = Runnable {
                for (moveInfo in moves) {
                    animateMoveImpl(
                        moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX,
                        moveInfo.toY
                    )
                }
                moves.clear()
                movesList.remove(moves)
            }
            if (removalsPending) {
                val view = moves[0].holder.itemView
                ViewCompat.postOnAnimationDelayed(view, mover, removeDuration)
            } else {
                mover.run()
            }
        }
        // Next, change stuff, to run in parallel with move animations
        if (changesPending) {
            val changes = ArrayList<ChangeInfo>()
            changes.addAll(pendingChanges)
            changesList.add(changes)
            pendingChanges.clear()
            val changer = Runnable {
                for (change in changes) {
                    animateChangeImpl(change)
                }
                changes.clear()
                changesList.remove(changes)
            }
            if (removalsPending) {
                val holder = changes[0].oldHolder
                ViewCompat.postOnAnimationDelayed(holder!!.itemView, changer, removeDuration)
            } else {
                changer.run()
            }
        }
        // Next, add stuff
        if (additionsPending) {
            val additions = ArrayList<ViewHolder>()
            additions.addAll(pendingAdditions)
            additionsList.add(additions)
            pendingAdditions.clear()
            val adder = Runnable {
                for (holder in additions) {
                    doAnimateAdd(holder)
                }
                additions.clear()
                additionsList.remove(additions)
            }
            if (removalsPending || movesPending || changesPending) {
                val removeDuration = if (removalsPending) removeDuration else 0
                val moveDuration = if (movesPending) moveDuration else 0
                val changeDuration = if (changesPending) changeDuration else 0
                val totalDelay = removeDuration + Math.max(moveDuration, changeDuration)
                val view = additions[0].itemView
                ViewCompat.postOnAnimationDelayed(view, adder, totalDelay)
            } else {
                adder.run()
            }
        }
    }

    override fun animateRemove(holder: ViewHolder): Boolean {
        endAnimation(holder)
        preAnimateRemove(holder)
        pendingRemovals.add(holder)
        return true
    }

    private fun preAnimateRemove(holder: ViewHolder) {
        holder.itemView.clear()

        if (holder is AnimateRemoveViewHolder) {
            (holder as AnimateRemoveViewHolder).preAnimateRemoveImpl(holder)
        }
        preAnimateRemoveImpl(holder)
    }

    protected fun preAnimateRemoveImpl(holder: ViewHolder) = Unit

    private fun doAnimateRemove(holder: ViewHolder) {
        if (holder is AnimateRemoveViewHolder) {
            (holder as AnimateRemoveViewHolder).animateRemoveImpl(holder, DefaultRemoveVpaListener(holder))
        }
        animateRemoveImpl(holder)
    }

    protected abstract fun animateRemoveImpl(holder: ViewHolder)

    override fun animateAdd(holder: ViewHolder): Boolean {
        endAnimation(holder)
        preAnimateAdd(holder)
        pendingAdditions.add(holder)
        return true
    }

    private fun preAnimateAdd(holder: ViewHolder) {
        holder.itemView.clear()

        if (holder is AnimateAddViewHolder) {
            (holder as AnimateAddViewHolder).preAnimateAddImpl(holder)
        }
        preAnimateAddImpl(holder)
    }

    protected open fun preAnimateAddImpl(holder: ViewHolder) = Unit

    private fun doAnimateAdd(holder: ViewHolder) {
        if (holder is AnimateAddViewHolder) {
            (holder as AnimateAddViewHolder).animateAddImpl(holder, DefaultAddVpaListener(holder))
        }
        animateAddImpl(holder)
    }

    protected abstract fun animateAddImpl(holder: ViewHolder)


    override fun animateMove(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        val view = holder.itemView
        val fromXTranslated = fromX + view.translationX.toInt()
        val fromYTranslated = fromY + view.translationY.toInt()

        endAnimation(holder)
        val deltaX = toX - fromXTranslated
        val deltaY = toY - fromYTranslated
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            view.translationX = (-deltaX).toFloat()
        }
        if (deltaY != 0) {
            view.translationY = (-deltaY).toFloat()
        }
        pendingMoves.add(MoveInfo(holder, fromXTranslated, fromYTranslated, toX, toY))
        return true
    }

    private fun animateMoveImpl(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            view.animate().translationX(0f)
        }
        if (deltaY != 0) {
            view.animate().translationY(0f)
        }
        // TODO: make EndActions end listeners instead, since end actions aren't called when
        // vpas are canceled (and can't end them. why?)
        // need listener functionality in VPACompat for this. Ick.
        moveAnimations.add(holder)
        val animation = view.animate()
        animation.setDuration(moveDuration).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animator: Animator) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationCancel(animator: Animator) {
                if (deltaX != 0) {
                    view.translationX = 0f
                }
                if (deltaY != 0) {
                    view.translationY = 0f
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                moveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }
        }).start()
    }

    override fun animateChange(
        oldHolder: ViewHolder, newHolder: ViewHolder?, fromX: Int, fromY: Int,
        toX: Int, toY: Int
    ): Boolean {

        if (oldHolder === newHolder) {
            return animateMove(oldHolder, fromX, fromY, toX, toY)
        }

        val prevTranslationX = oldHolder.itemView.translationX
        val prevTranslationY = oldHolder.itemView.translationY
        val prevAlpha = oldHolder.itemView.alpha

        endAnimation(oldHolder)
        val deltaX = (toX.toFloat() - fromX.toFloat() - prevTranslationX).toInt()
        val deltaY = (toY.toFloat() - fromY.toFloat() - prevTranslationY).toInt()

        // recover prev translation state after ending animation
        oldHolder.itemView.apply {
            translationX = prevTranslationX
            translationY = prevTranslationY
            alpha = prevAlpha
        }

        if (newHolder?.itemView != null) {
            // carry over translation values
            endAnimation(newHolder)
            newHolder.itemView.apply {
                alpha = 0f
                translationX = (-deltaX).toFloat()
                translationY = (-deltaY).toFloat()
            }
        }
        pendingChanges.add(ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY))
        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val oldHolder = changeInfo.oldHolder
        val view = oldHolder?.itemView
        val newHolder = changeInfo.newHolder
        val newView = newHolder?.itemView

        if (view != null) {
            changeAnimations.add(oldHolder)
            val oldViewAnim = view.animate().setDuration(changeDuration)

            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat()).translationY((changeInfo.toY - changeInfo.fromY).toFloat())
                .alpha((if (withCrossFade) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(changeInfo.oldHolder, true)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        oldViewAnim.setListener(null)
                        view.alpha = 1f
                        view.translationY = 0f
                        view.translationX = 0f
                        dispatchChangeFinished(oldHolder, true)
                        changeAnimations.remove(oldHolder)
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
        if (newView != null) {
            changeAnimations.add(newHolder)
            val newViewAnimation = newView.animate()

            newViewAnimation.translationX(0f).translationY(0f)
                .setDuration(changeDuration)
                .alpha(1f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(newHolder, false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        newViewAnimation.setListener(null)
                        newView.alpha = 1f
                        newView.translationX = 0f
                        newView.translationY = 0f
                        dispatchChangeFinished(newHolder, false)
                        changeAnimations.remove(newHolder)
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
    }

    private fun endChangeAnimation(infoList: MutableList<ChangeInfo>, item: ViewHolder) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo, item: ViewHolder?): Boolean {
        var oldItem = false
        when (item) {
            changeInfo.newHolder -> changeInfo.newHolder = null
            changeInfo.oldHolder -> {
                changeInfo.oldHolder = null
                oldItem = true
            }
            else -> return false
        }
        item?.itemView?.apply {
            alpha = 1f
            translationY = 0f
            translationX = 0f
        }
        dispatchChangeFinished(item, oldItem)
        return true
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder)
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder)
        }
    }

    override fun endAnimation(item: ViewHolder) {
        val view = item.itemView
        // this will trigger end callback which should set properties to their target values.
        view.animate().cancel()

        if (item is AnimateAddViewHolder) {
            (item as AnimateAddViewHolder).endAddAnimation(item)
        }
        if (item is AnimateChangeViewHolder) {
            (item as AnimateChangeViewHolder).endChangeAnimation(item)
        }
        if (item is AnimateRemoveViewHolder) {
            (item as AnimateRemoveViewHolder).endRemoveAnimation(item)
        }

        // TODO if some other animations are chained to end, how do we cancel them as well?
        for (i in pendingMoves.indices.reversed()) {
            val moveInfo = pendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                pendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(pendingChanges, item)
        if (pendingRemovals.remove(item)) {
            item.itemView.clear()
            dispatchRemoveFinished(item)
        }
        if (pendingAdditions.remove(item)) {
            item.itemView.clear()
            dispatchAddFinished(item)
        }

        for (i in changesList.indices.reversed()) {
            val changes = changesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                changesList.removeAt(i)
            }
        }
        for (i in movesList.indices.reversed()) {
            val moves = movesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        movesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in additionsList.indices.reversed()) {
            val additions = additionsList[i]
            if (additions.remove(item)) {
                item.itemView.clear()
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    additionsList.removeAt(i)
                }
            }
        }

        // animations should be ended by the cancel above.
        if (removeAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                "after animation is cancelled, item should not be in " + "removeAnimations list"
            )
        }

        if (addAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                "after animation is cancelled, item should not be in " + "addAnimations list"
            )
        }

        if (changeAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                "after animation is cancelled, item should not be in " + "changeAnimations list"
            )
        }

        if (moveAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                "after animation is cancelled, item should not be in " + "moveAnimations list"
            )
        }
        dispatchFinishedWhenDone()
    }

    override fun isRunning(): Boolean {
        return pendingAdditions.isNotEmpty() ||
                pendingChanges.isNotEmpty() ||
                pendingMoves.isNotEmpty() ||
                pendingRemovals.isNotEmpty() ||
                moveAnimations.isNotEmpty() ||
                removeAnimations.isNotEmpty() ||
                addAnimations.isNotEmpty() ||
                changeAnimations.isNotEmpty() ||
                movesList.isNotEmpty() ||
                additionsList.isNotEmpty() ||
                changesList.isNotEmpty()
    }

    /**
     * Check the state of currently pending and running animations. If there are none
     * pending/running, call #dispatchAnimationsFinished() to notify any
     * listeners.
     */
    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    protected fun getRemoveDelay(holder: ViewHolder): Long {
        return Math.abs(holder.oldPosition * removeDuration / 4)
    }

    protected fun getAddDelay(holder: ViewHolder): Long {
        return abs(holder.adapterPosition * addDuration / 4)
    }


    override fun endAnimations() {
        var count = pendingMoves.size
        for (i in count - 1 downTo 0) {
            val item = pendingMoves[i]
            val view = item.holder.itemView
            view.apply {
                translationX = 0f
                translationY = 0f
            }
            dispatchMoveFinished(item.holder)
            pendingMoves.removeAt(i)
        }
        count = pendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = pendingRemovals[i]
            dispatchRemoveFinished(item)
            pendingRemovals.removeAt(i)
        }
        count = pendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = pendingAdditions[i]
            item.itemView.clear()
            dispatchAddFinished(item)
            pendingAdditions.removeAt(i)
        }
        count = pendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(pendingChanges[i])
        }
        pendingChanges.clear()

        if (!isRunning) {
            return
        }

        var listCount = movesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = movesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                val view = item.itemView
                view.apply {
                    translationX = 0f
                    translationY = 0f
                }
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    movesList.remove(moves)
                }
            }
        }
        listCount = additionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = additionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                view.alpha = 1f
                dispatchAddFinished(item)
                //this check prevent exception when removal already happened during finishing animation
                if (j < additions.size) {
                    additions.removeAt(j)
                }
                if (additions.isEmpty()) {
                    additionsList.remove(additions)
                }
            }
        }
        listCount = changesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = changesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    changesList.remove(changes)
                }
            }
        }

        cancelAll(removeAnimations)
        cancelAll(moveAnimations)
        cancelAll(addAnimations)
        cancelAll(changeAnimations)

        dispatchAnimationsFinished()
    }


    private fun cancelAll(viewHolders: List<ViewHolder>) {
        for (i in viewHolders.indices.reversed()) {
            viewHolders[i].itemView.animate().cancel()
        }
    }

    protected inner class DefaultAddVpaListener(private var mViewHolder: ViewHolder) : AnimatorListenerAdapter() {

        override fun onAnimationStart(animator: Animator) {
            //Might have been dispatched already
            if (!addAnimations.contains(mViewHolder)) {
                dispatchAddStarting(mViewHolder)
            }
            addAnimations.add(mViewHolder)
        }

        override fun onAnimationCancel(animator: Animator) {
            mViewHolder.itemView.clear()
        }

        override fun onAnimationEnd(animator: Animator) {
            addAnimations.remove(mViewHolder)
            //Might still have another animation
            if (!addAnimations.contains(mViewHolder)) {
                dispatchAddFinished(mViewHolder)
            }
            mViewHolder.itemView.clear()
            dispatchFinishedWhenDone()
        }
    }

    protected inner class DefaultRemoveVpaListener(private var mViewHolder: ViewHolder) : AnimatorListenerAdapter() {

        override fun onAnimationStart(view: Animator) {
            //Might have been dispatched already
            if (!removeAnimations.contains(mViewHolder)) {
                dispatchRemoveStarting(mViewHolder)
            }
            removeAnimations.add(mViewHolder)
        }


        override fun onAnimationEnd(view: Animator) {
            mViewHolder.itemView.clear()
            removeAnimations.remove(mViewHolder)

            if (!removeAnimations.contains(mViewHolder)) {
                dispatchRemoveFinished(mViewHolder)
            }
            dispatchFinishedWhenDone()
        }
    }

    override fun canReuseUpdatedViewHolder(viewHolder: ViewHolder, payloads: List<Any>): Boolean {
        return if (viewHolder is AnimateChangeViewHolder) {
            (viewHolder as AnimateChangeViewHolder).canAnimateChange(payloads)
        } else {
            super.canReuseUpdatedViewHolder(viewHolder, payloads)
        }
    }

}
