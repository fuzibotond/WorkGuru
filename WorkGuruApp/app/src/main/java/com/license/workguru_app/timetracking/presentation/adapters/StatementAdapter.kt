package com.license.workguru_app.timetracking.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.license.workguru_app.R
import com.license.workguru_app.authentification.presentation.SharedViewModel
import com.license.workguru_app.timetracking.domain.model.Statement

class StatementAdapter(
    private var list: ArrayList<Statement>,
    private val context: Context,
    private val listener: OnItemClickListener,
    private val listener2: OnItemLongClickListener,
    private val sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<StatementAdapter.DataViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(position: Int)
    }

    // 1. user defined ViewHolder type - Embedded class!
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val statementTextView: TextView = itemView.findViewById(R.id.statement_item_txt)
        val trackedTimeTextView: TextView = itemView.findViewById(R.id.tracked_time_at_statement)
        val lessTrackedTimeTextView: TextView = itemView.findViewById(R.id.less_tracked_time_at_statement)
        val percentageTextView: TextView = itemView.findViewById(R.id.percent_at_statement)

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
            LayoutInflater.from(parent.context).inflate(R.layout.statement_item, parent, false)

        return DataViewHolder(itemView)
    }


    // 3. Called many times, when we scroll the list
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = list[position]
        holder.statementTextView.text = currentItem.total
        holder.trackedTimeTextView.text = currentItem.tracked_time
        holder.lessTrackedTimeTextView.text = currentItem.less_tracked_time
        holder.percentageTextView.text = currentItem.percentage

    }

    override fun getItemCount() = list.size

    // Update the list
    fun setData(newlist: ArrayList<Statement>){
        list = newlist
    }



}