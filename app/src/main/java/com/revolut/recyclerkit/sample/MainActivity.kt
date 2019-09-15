package com.revolut.recyclerkit.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.revolut.recyclerkit.delegates.DelegatesManager
import com.revolut.recyclerkit.delegates.DiffAdapter
import com.revolut.recyclerkit.sample.delegates.ImageTextDelegate
import com.thedeanda.lorem.LoremIpsum
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val items: MutableList<ImageTextDelegate.Model> = (0..25).map { index ->
        ImageTextDelegate.Model(
            listId = index.toString(),
            text = LoremIpsum.getInstance().getParagraphs(1, 1),
            imageUrl = "https://picsum.photos/200"
        )
    }.toMutableList()

    private val adapter by lazy {
        DiffAdapter(DelegatesManager()
            .addDelegate(
                ImageTextDelegate { model -> onClick(model) }
            ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.setItems(ArrayList(items))
    }

    private fun onClick(model: ImageTextDelegate.Model) {
        items[model.listId.toInt()] = model.copy(
            text = LoremIpsum.getInstance().getParagraphs(1, 1)
        )
        adapter.setItems(ArrayList(items))
    }

}
