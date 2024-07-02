package com.dukaancalculator.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.atta.dukaancalculator.R
import com.dukaancalculator.ui.models.commonmodels.IntroModel

class ViewAdapter(private val context: Context) : PagerAdapter() {

    private val list = listOf(
        IntroModel("CALCULATOR","Calculator build for your any business Type",R.drawable.calculator),
        IntroModel("DAILY REPORTS","Get your sales, profits, top stocks & cash reports",R.drawable.calculator),
        IntroModel("SALES COUNTER","Quick way for doing bills with inventory",R.drawable.calculator),
        IntroModel("ADD EXPENSE","Add & manage all your expense in a single app!",R.drawable.calculator),
    )

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.intro_sample_row, container, false)
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
        val imageView: ImageView = view.findViewById(R.id.img)
        imageView.setImageResource(list[position].img)
        title.text=list[position].title
        description.text=list[position].description
        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
