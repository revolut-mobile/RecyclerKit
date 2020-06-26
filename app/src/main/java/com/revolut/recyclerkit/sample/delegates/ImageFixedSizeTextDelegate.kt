package com.revolut.recyclerkit.sample.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.revolut.decorations.dividers.DividerDecoratedItem
import com.revolut.decorations.dividers.delegates.DividerDecorationDelegate
import com.revolut.decorations.frames.FrameDecoratedItem
import com.revolut.decorations.frames.delegates.FrameDecorationDelegate
import com.revolut.decorations.overlay.OverlayDecoratedItem
import com.revolut.decorations.overlay.delegates.OverlayDecorationDelegate
import com.revolut.kextensions.ContainerRecyclerViewHolder
import com.revolut.recyclerkit.animations.holder.AnimateChangeViewHolder
import com.revolut.recyclerkit.delegates.BaseRecyclerViewDelegate
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.sample.R
import com.revolut.recyclerkit.sample.delegates.ImageFixedSizeTextDelegate.Model
import com.revolut.recyclerkit.sample.delegates.ImageFixedSizeTextDelegate.ViewHolder
import kotlinx.android.synthetic.main.image_fixed_size_text_delegate.*

/**
 * This Delegate animates text change itself not relying on default RecyclerView cross-fade.
 * Take a look at [Model.calculatePayload] and [ViewHolder.canAnimateChange] to
 * understand how payloads are calculated and how they are used to decide if ViewHolder should perform animation itself.
 */
class ImageFixedSizeTextDelegate(
    val onClickListener: (Model) -> Unit
) : BaseRecyclerViewDelegate<Model, ViewHolder>(
    viewType = R.layout.image_fixed_size_text_delegate,
    rule = { _, data -> data is Model }
) {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_fixed_size_text_delegate, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, data: Model, pos: Int, payloads: List<Any>?) {
        super.onBindViewHolder(holder, data, pos, payloads)
        payloads?.filterIsInstance<Payload>()
            ?.forEach { payload -> holder.applyPayload(payload) }

        holder.takeIf { payloads.isNullOrEmpty() }?.applyData(data)
    }

    private fun ViewHolder.applyPayload(payload: Payload) {
        payload.imageUrl?.let { imageView.loadImage(it) }
        payload.text?.let {
            textView.text = it
            textView.startAnimation(AnimationSet(true).apply {
                addAnimation(ScaleAnimation(1.0f, 1.01f, 1.0f, 1.01f, textView.measuredWidth / 2.0f, textView.measuredHeight / 2.0f).apply {
                    duration = 200
                })
                addAnimation(ScaleAnimation(1.01f, 1.0f, 1.01f, 1.0f, textView.measuredWidth / 2.0f, textView.measuredHeight / 2.0f).apply {
                    startOffset = 200
                    duration = 200
                })
            })
        }
    }

    private fun ViewHolder.applyData(data: Model) {
        itemView.setOnClickListener { onClickListener(data) }
        textView.text = data.text
        imageView.loadImage(data.imageUrl)
    }

    private fun ImageView.loadImage(imageUrl: String) {
        Glide.with(this).load(imageUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(this)
    }

    data class Payload(
        val text: CharSequence?,
        val imageUrl: String?
    )

    data class Model(
        override val listId: String,
        val text: CharSequence,
        val imageUrl: String,
        override var topDecoration: DividerDecorationDelegate? = null,
        override var bottomDecoration: DividerDecorationDelegate? = null,
        override var leftDecoration: DividerDecorationDelegate? = null,
        override var rightDecoration: DividerDecorationDelegate? = null,
        override var frameDecoration: FrameDecorationDelegate? = null,
        override var overlayColorDecoration: OverlayDecorationDelegate? = null
    ) : ListItem, DividerDecoratedItem, FrameDecoratedItem, OverlayDecoratedItem {

        override fun calculatePayload(oldItem: Any): Any? {
            if (oldItem !is Model) return null

            return Payload(
                text = text.takeIf { text != oldItem.text },
                imageUrl = imageUrl.takeIf { imageUrl != oldItem.imageUrl }
            )
        }

    }

    class ViewHolder(itemView: View) : ContainerRecyclerViewHolder(itemView), AnimateChangeViewHolder {

        override fun canAnimateChange(payloads: List<Any>): Boolean {
            //Animates only when Model.text changes
            return payloads.filterIsInstance<Payload>().any { it.text != null }
        }

        override fun endChangeAnimation(holder: RecyclerView.ViewHolder) {
            textView.clearAnimation()
        }
    }

}