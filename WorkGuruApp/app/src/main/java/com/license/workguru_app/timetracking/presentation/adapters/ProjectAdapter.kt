package com.license.workguru_app.timetracking.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.license.workguru_app.R
import com.license.workguru_app.authentification.presentation.SharedViewModel
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.presentation.components.ProjectListFragment
import kotlinx.android.synthetic.main.project_item_layout.view.*

class ProjectAdapter(
    private var list: ArrayList<Project>,
    private val context: Context,
    private val listener: ProjectListFragment,
    private val listener2: OnItemLongClickListener,
    private val sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<ProjectAdapter.DataViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(position: Int)
    }

    // 1. user defined ViewHolder type - Embedded class!
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val projectNameTextView:TextView = itemView.findViewById(R.id.project_name)

        init{

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

        }
        override fun onClick(p0: View?) {
            val currentPosition = this.adapterPosition
            listener.onItemClick(currentPosition)

        }

        override fun onLongClick(p0: View?): Boolean {
            val currentPosition = this.adapterPosition
            listener2.onItemLongClick(currentPosition)
            return true
        }

    }

    // 2. Called only a few times = number of items on screen + a few more ones
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.project_item_layout, parent, false)

        return DataViewHolder(itemView)
    }


    // 3. Called many times, when we scroll the list
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = list[position]
        holder.projectNameTextView.text = currentItem.name

    }

    override fun getItemCount() = list.size

    // Update the list
    fun setData(newlist: ArrayList<Project>){
        list = newlist
    }



}