package com.revolut.recyclerkit.animations.holder


import androidx.core.view.ViewPropertyAnimatorListener
import androidx.recyclerview.widget.RecyclerView

interface AnimateAddViewHolder {

    fun preAnimateAddImpl(holder: RecyclerView.ViewHolder)

    fun animateAddImpl(holder: RecyclerView.ViewHolder, listener: ViewPropertyAnimatorListener)

    fun endAddAnimation(holder: RecyclerView.ViewHolder)

}
