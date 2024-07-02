package com.dukaancalculator.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.atta.dukaancalculator.R
import com.dukaancalculator.ui.models.kharcha.ExpenseCategoryModel

class CustomSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val items: List<ExpenseCategoryModel>,
) : ArrayAdapter<ExpenseCategoryModel>(context, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val item = getItem(position)
        val categoryColor: View = view.findViewById(R.id.categoryColor)
        val categoryTv: TextView = view.findViewById(R.id.categoryTv)
        val itemConstraintBack: ConstraintLayout = view.findViewById(R.id.itemConstraintBack)
        val topItemConstraint: ConstraintLayout = view.findViewById(R.id.topItemConstraint)

        item?.let {
            categoryColor.setBackgroundColor(it.color)
            categoryTv.text = it.categoryName
        }

        return view
    }
}

