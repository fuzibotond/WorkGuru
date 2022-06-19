package com.license.workguru_app.profile.presentation.adapetrs

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.license.workguru_app.R
import com.license.workguru_app.profile.data.remote.DTO.States

class StateAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    cities: List<States>
) :
    ArrayAdapter<States>(mContext, mLayoutResourceId, cities) {
    private val city: MutableList<States> = ArrayList(cities)
    private var allCities: List<States> = ArrayList(cities)

    override fun getCount(): Int {
        return city.size
    }
    override fun getItem(position: Int): States {
        return city[position]
    }
    override fun getItemId(position: Int): Long {
        return city[position].id!!.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }

        try {
            val city: States = getItem(position)
            val cityAutoCompleteView = convertView!!.findViewById<View>(R.id.text_view_list_item) as TextView
            cityAutoCompleteView.text = city.name

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }
}

