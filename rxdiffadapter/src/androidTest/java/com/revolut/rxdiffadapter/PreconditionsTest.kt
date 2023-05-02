package com.revolut.rxdiffadapter

import android.view.View
import android.view.ViewGroup
import com.revolut.recyclerkit.delegates.BaseRecyclerViewDelegate
import com.revolut.recyclerkit.delegates.BaseRecyclerViewHolder
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.delegates.HasNestedDelegates
import com.revolut.recyclerkit.delegates.RecyclerViewDelegate
import com.revolut.rxdiffadapter.PreconditionsTest.TestInnerDelegatesDelegate.Model
import com.revolut.rxdiffadapter.PreconditionsTest.TestInnerDelegatesDelegate.ViewHolder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PreconditionsTest {

    @Test
    fun testErrorWithDuplicatesDetected() = runWithErrorsEnabled {
        assertThrows<IllegalArgumentException> {
            RxDiffAdapter(
                delegates = listOf(
                    TestDelegateA(),
                    TestDelegateB(),
                    TestDelegateB(),
                )
            )
        }
    }

    @Test
    fun testErrorWithInnerDelegateDuplicatesDetected() = runWithErrorsEnabled {
        assertThrows<IllegalArgumentException> {
            RxDiffAdapter(
                delegates = listOf(
                    TestDelegateA(),
                    TestDelegateB(),
                    TestInnerDelegatesDelegate(
                        delegates = listOf(
                            TestDelegateB(),
                            TestDelegateB(),
                        )
                    )
                )
            )
        }
    }

    @Test
    fun testNoErrorsWithoutDuplicates() = runWithErrorsEnabled {
        RxDiffAdapter(
            delegates = listOf(
                TestDelegateA(),
                TestDelegateB(),
                TestInnerDelegatesDelegate(
                    delegates = listOf(
                        TestDelegateB(),
                        TestDelegateA(),
                    )
                )
            )
        )
    }

    private fun runWithErrorsEnabled(block: () -> Unit) {
        RxDiffAdapter.setThrowErrorsOnPreconditions(true)
        block()
        RxDiffAdapter.setThrowErrorsOnPreconditions(false)
    }

    class TestDelegateA : BaseRecyclerViewDelegate<TestDelegateA.Model, TestDelegateA.ViewHolder>(
        viewType = 0,
        rule = { _, _ -> true }
    ) {

        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder = throw RuntimeException("Stub")

        data class Model(override val listId: String) : ListItem

        class ViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView)
    }

    class TestDelegateB : BaseRecyclerViewDelegate<TestDelegateB.Model, TestDelegateB.ViewHolder>(
        viewType = 1,
        rule = { _, _ -> true }
    ) {

        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder = throw RuntimeException("Stub")

        data class Model(override val listId: String) : ListItem

        class ViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView)
    }

    class TestInnerDelegatesDelegate(
        override val delegates: List<RecyclerViewDelegate<*, *>>,
    ) : BaseRecyclerViewDelegate<Model, ViewHolder>(
        viewType = 2,
        rule = { _, _ -> true }
    ), HasNestedDelegates {

        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder = throw RuntimeException("Stub")

        data class Model(override val listId: String) : ListItem

        class ViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView)
    }
}