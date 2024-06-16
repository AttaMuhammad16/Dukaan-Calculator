package com.dukaancalculator.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import com.dukaancalculator.R
import com.dukaancalculator.ui.models.commonmodels.ProductModel

class ProductAdapter(context: Context, private val products: List<ProductModel>) : ArrayAdapter<ProductModel>(context, 0, products) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val holder: ViewHolder
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.product_counter_sample_row, parent, false)
            holder = ViewHolder()
            holder.productNameEdt = itemView.findViewById(R.id.productNameEdt)
            holder.priceEdt = itemView.findViewById(R.id.priceEdt)
            holder.quantityEdt = itemView.findViewById(R.id.quantityEdt)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }
        val product = products[position]
        holder.productNameEdt.setText(product.productName)
        holder.priceEdt.setText(product.productPrice)
        holder.quantityEdt.setText(product.productQuantity)

        return itemView!!
    }

    internal class ViewHolder {
        lateinit var productNameEdt: EditText
        lateinit var priceEdt: EditText
        lateinit var quantityEdt: EditText
    }
}
