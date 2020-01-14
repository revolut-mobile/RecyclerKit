package com.revolut.decorations.dividers

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.decorations.dividers.delegates.DividerDecorationDelegate
import com.revolut.recyclerkit.delegates.BaseRecyclerViewHolder
import com.revolut.recyclerkit.delegates.DiffAdapter
import com.revolut.recyclerkit.delegates.ListItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DelegatesDividerItemDecorationTest {

    private val delegatesDividerItemDecoration = DelegatesDividerItemDecoration()

    lateinit var activity: Activity

    lateinit var recyclerView: RecyclerView
    lateinit var canvas: Canvas
    lateinit var recyclerViewState: RecyclerView.State

    @Before
    fun setUp() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        recyclerView = mock()
        canvas = mock()
        recyclerViewState = RecyclerView.State()
    }

    @Test
    fun `WHEN topDecoration is provided THEN onDrawTop is called for relevant View`() {

        val testItem = TestListItem(
            topDecoration = mock(),
            mockView = MockView(activity, position = 0)
        )

        recyclerView.setUpMockedRecyclerView(listOf(testItem))

        delegatesDividerItemDecoration.onDraw(
            canvas, recyclerView, recyclerViewState
        )

        verify(testItem.topDecoration!!).onDrawTop(canvas, testItem.mockView, recyclerView, recyclerViewState)
        verify(testItem.topDecoration!!, times(0)).onDrawBottom(any(), any(), any(), any())
        verify(testItem.topDecoration!!, times(0)).onDrawLeft(any(), any(), any(), any())
        verify(testItem.topDecoration!!, times(0)).onDrawRight(any(), any(), any(), any())
    }

    @Test
    fun `WHEN bottomDecoration is provided THEN onDrawBottom is called for relevant View`() {
        val testItem = TestListItem(
            bottomDecoration = mock(),
            mockView = MockView(activity, position = 0)
        )

        recyclerView.setUpMockedRecyclerView(listOf(testItem))

        delegatesDividerItemDecoration.onDraw(
            canvas, recyclerView, recyclerViewState
        )

        verify(testItem.bottomDecoration!!).onDrawBottom(canvas, testItem.mockView, recyclerView, recyclerViewState)
        verify(testItem.bottomDecoration!!, times(0)).onDrawTop(any(), any(), any(), any())
        verify(testItem.bottomDecoration!!, times(0)).onDrawLeft(any(), any(), any(), any())
        verify(testItem.bottomDecoration!!, times(0)).onDrawRight(any(), any(), any(), any())
    }

    @Test
    fun `WHEN leftDecoration is provided THEN onDrawLeft is called for relevant View`() {
        val testItem = TestListItem(
            leftDecoration = mock(),
            mockView = MockView(activity, position = 0)
        )

        recyclerView.setUpMockedRecyclerView(listOf(testItem))

        delegatesDividerItemDecoration.onDraw(
            canvas, recyclerView, recyclerViewState
        )

        verify(testItem.leftDecoration!!).onDrawLeft(canvas, testItem.mockView, recyclerView, recyclerViewState)
        verify(testItem.leftDecoration!!, times(0)).onDrawTop(any(), any(), any(), any())
        verify(testItem.leftDecoration!!, times(0)).onDrawBottom(any(), any(), any(), any())
        verify(testItem.leftDecoration!!, times(0)).onDrawRight(any(), any(), any(), any())
    }

    @Test
    fun `WHEN rightDecoration is provided THEN onDrawRight is called for relevant View`() {
        val testItem = TestListItem(
            rightDecoration = mock(),
            mockView = MockView(activity, position = 0)
        )

        recyclerView.setUpMockedRecyclerView(listOf(testItem))

        delegatesDividerItemDecoration.onDraw(
            canvas, recyclerView, recyclerViewState
        )

        verify(testItem.rightDecoration!!).onDrawRight(canvas, testItem.mockView, recyclerView, recyclerViewState)
        verify(testItem.rightDecoration!!, times(0)).onDrawTop(any(), any(), any(), any())
        verify(testItem.rightDecoration!!, times(0)).onDrawBottom(any(), any(), any(), any())
        verify(testItem.rightDecoration!!, times(0)).onDrawLeft(any(), any(), any(), any())
    }

}

private fun RecyclerView.setUpMockedRecyclerView(
    listItems: List<TestListItem>
) {
    val adapter = DiffAdapter().apply { setItems(listItems) }

    whenever(childCount).thenReturn(adapter.itemCount)

    listItems.forEach { testListItem ->
        whenever(getChildAt(testListItem.mockView.position)).thenReturn(testListItem.mockView)
        val mockVieHolder = mockViewHolder(testListItem.mockView, testListItem)
        whenever(getChildViewHolder(testListItem.mockView)).thenReturn(mockVieHolder)
    }

}

private fun mockViewHolder(
    view: MockView,
    listItem: TestListItem
): BaseRecyclerViewHolder = spy(TestViewHolder(itemView = view)).apply {
    whenever(this.lastBoundItem).thenReturn(listItem)
}

private class TestViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView)

private data class TestListItem(
    override val listId: String = "",
    val mockView: MockView,
    override var topDecoration: DividerDecorationDelegate? = null,
    override var bottomDecoration: DividerDecorationDelegate? = null,
    override var leftDecoration: DividerDecorationDelegate? = null,
    override var rightDecoration: DividerDecorationDelegate? = null
) : ListItem, DividerDecoratedItem

private class MockView(
    context: Context,
    val position: Int
) : View(context)