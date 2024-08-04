package com.tehnokotiki.stroymatch

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog

class MultiSelectSpinner(context: Context, attrs: AttributeSet? = null) : androidx.appcompat.widget.AppCompatSpinner(context, attrs) {

    private var options = arrayOf<String>()
    private val selectedItems = mutableSetOf<String>()
    private var listener: (() -> Unit)? = null

    init {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showMultiSelectDialog()
            }
            true
        }
    }

    fun setOptions(options: Array<String>) {
        this.options = options
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setAdapter(adapter)
    }

    fun setSelection(selectedItems: Set<String>) {
        this.selectedItems.clear()
        this.selectedItems.addAll(selectedItems)
    }

    fun getSelectedItems(): List<String> {
        return selectedItems.toList()
    }

    fun setOnItemSelectedListener(listener: () -> Unit) {
        this.listener = listener
    }

    private fun showMultiSelectDialog() {
        val selected = BooleanArray(options.size) { false }
        options.forEachIndexed { index, option ->
            selected[index] = selectedItems.contains(option)
        }

        AlertDialog.Builder(context)
            .setTitle("Select Options")
            .setMultiChoiceItems(options, selected) { _, which, isChecked ->
                if (isChecked) {
                    selectedItems.add(options[which])
                } else {
                    selectedItems.remove(options[which])
                }
            }
            .setPositiveButton("OK") { _, _ ->
                listener?.invoke()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
