package com.revolut.rxdiffadapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.delegates.HasNestedDelegates
import com.revolut.recyclerkit.delegates.RecyclerViewDelegate

internal object Preconditions {

    private const val TAG: String = "RX_DIFF_ADAPTER"

    var throwErrorsEnabled = false

    fun checkForDuplicateDelegates(delegates: List<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>) {
        delegates.forEach { delegate ->
            if (delegate is HasNestedDelegates) {
                checkForDuplicateDelegates(delegate.delegates)
            } else {
                val possibleDuplicates = delegates.groupingBy { it.viewType }.eachCount()
                val hasAnyDuplicates = possibleDuplicates.any { it.value > 1 }

                if (hasAnyDuplicates) {
                    val message = "Duplicate delegates detected:\n${possibleDuplicates.mapToString()}"
                    val error = IllegalArgumentException(message)

                    if (throwErrorsEnabled) {
                        throw error
                    } else {
                        Log.e(TAG, message, error)
                    }
                }
            }
        }
    }

    private fun <K, V> Map<K, V>.mapToString(): String =
        entries.joinToString(separator = "\n") { (k, v) -> "$k = $v" }
}