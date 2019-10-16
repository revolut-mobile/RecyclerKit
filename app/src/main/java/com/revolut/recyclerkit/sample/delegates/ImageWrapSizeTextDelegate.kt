package com.revolut.recyclerkit.sample.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.revolut.decorations.dividers.DividerDecoratedItem
import com.revolut.decorations.dividers.delegates.DividerDecorationDelegate
import com.revolut.recyclerkit.delegates.BaseRecyclerViewDelegate
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.sample.R
import kotlinx.android.synthetic.main.image_wrap_size_text_delegate.view.*

class ImageWrapSizeTextDelegate(
    val onClickListener: (Model) -> Unit
) : BaseRecyclerViewDelegate<ImageWrapSizeTextDelegate.Model, ImageWrapSizeTextDelegate.ViewHolder>(
    viewType = R.layout.image_wrap_size_text_delegate,
    rule = { _, data -> data is Model }
) {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, data: Model, pos: Int, payloads: List<Any>?) {
        holder.takeIf { payloads.isNullOrEmpty() }?.applyData(data)
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
            .into(imageView)
    }

    data class Model(
        override val listId: String,
        val text: CharSequence,
        val imageUrl: String,
        override var topDecoration: DividerDecorationDelegate? = null,
        override var bottomDecoration: DividerDecorationDelegate? = null,
        override var leftDecoration: DividerDecorationDelegate? = null,
        override var rightDecoration: DividerDecorationDelegate? = null
    ) : ListItem, DividerDecoratedItem

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.textView
        val imageView: ImageView = itemView.imageView
    }

}