package com.revolut.recyclerkit.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.revolut.decorations.dividers.DelegatesDividerItemDecoration
import com.revolut.recyclerkit.animations.FadeInAnimator
import com.revolut.recyclerkit.delegates.DelegatesManager
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.sample.PaddingDecorations.PADDING_16DP
import com.revolut.recyclerkit.sample.delegates.ImageFixedSizeTextDelegate
import com.revolut.recyclerkit.sample.delegates.ImageWrapSizeTextDelegate
import com.revolut.rxdiffadapter.RxDiffAdapter
import com.thedeanda.lorem.LoremIpsum
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private val items: MutableList<ListItem> = listOf(
        *(0..5).map { index ->
            ImageFixedSizeTextDelegate.Model(
                listId = index.toString(),
                text = LoremIpsum.getInstance().getParagraphs(1, 1),
                imageUrl = "https://picsum.photos/200",
                leftDecoration = PADDING_16DP,
                rightDecoration = PADDING_16DP,
                topDecoration = ShadowDecorations.TOP_SHADOW_DECORATION,
                bottomDecoration = ShadowDecorations.BOTTOM_SHADOW_DECORATION
            )
        }.toTypedArray<ListItem>(),
        *(6..9).map { index ->
            ImageWrapSizeTextDelegate.Model(
                listId = index.toString(),
                text = LoremIpsum.getInstance().getParagraphs(1, 1),
                imageUrl = "https://picsum.photos/200",
                leftDecoration = PADDING_16DP,
                rightDecoration = PADDING_16DP,
                topDecoration = ShadowDecorations.TOP_SHADOW_DECORATION,
                bottomDecoration = ShadowDecorations.BOTTOM_SHADOW_DECORATION
            )
        }.toTypedArray<ListItem>()
    ).toMutableList()

    private val adapter by lazy {
        RxDiffAdapter(async = true, delegatesManager = DelegatesManager()
            .addDelegate(
                ImageFixedSizeTextDelegate { model -> onClick(model) }
            ).addDelegate(
                ImageWrapSizeTextDelegate { model -> onClick(model) }
            ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DelegatesDividerItemDecoration())
            itemAnimator = FadeInAnimator(supportsChangeAnimations = true)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.setItems(ArrayList(items))
    }


    private fun onClick(model: ImageWrapSizeTextDelegate.Model) {
        items[model.listId.toInt()] = model.copy(
            text = LoremIpsum.getInstance().getParagraphs(1, 1)
        )
        adapter.setItems(ArrayList(items))
    }

    private fun onClick(model: ImageFixedSizeTextDelegate.Model) {
        items[model.listId.toInt()] = model.copy(
            text = LoremIpsum.getInstance().getParagraphs(1, 1)
        )
        adapter.setItems(ArrayList(items))
    }

}
