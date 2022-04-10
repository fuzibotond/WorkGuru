package com.license.workguru_app.timetracking.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.Data
import com.license.workguru_app.timetracking.presentation.components.ProjectListFragment
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class ProjectAdapter(
    private var list: ArrayList<Data>,
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
        val categoryNameTextView:TextView = itemView.findViewById(R.id.category_name)
        val trackedTimeTextView:TextView = itemView.findViewById(R.id.tracked_time)
        val startDateTextView:TextView = itemView.findViewById(R.id.start_date)
        val taskAmountTextView:TextView = itemView.findViewById(R.id.task_amount)
        val numberOfMembersTextView:TextView = itemView.findViewById(R.id.number_of_member)
        val hiddenLayout:LinearLayout = itemView.findViewById(R.id.hidden_layout)
        val expandBtn:ImageButton = itemView.findViewById(R.id.expand_hidden_content_btn)
        val isExpanded:MutableLiveData<Boolean> = MutableLiveData(false)
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
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = list[position]
        holder.hiddenLayout.visibility = View.GONE
        holder.projectNameTextView.text = currentItem.name
        holder.categoryNameTextView.text = currentItem.category_name
        holder.startDateTextView.text = formatDate(currentItem.start_date)
        holder.trackedTimeTextView.text = currentItem.tracked.toString()
        holder.numberOfMembersTextView.text =currentItem.members.toString()
        holder.taskAmountTextView.text = currentItem.tasks.toString()

        holder.expandBtn.setOnClickListener {
            holder.isExpanded.value = !holder.isExpanded.value!!
        }

        holder.isExpanded.observe(context as LifecycleOwner){
            if (holder.isExpanded.value == true){
                holder.expandBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_24))
                holder.hiddenLayout.visibility = View.VISIBLE
            }else{
                holder.expandBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_24))
                holder.hiddenLayout.visibility = View.GONE
            }
        }

    }

    override fun getItemCount() = list.size

    // Update the list
    fun setData(newlist: ArrayList<Data>){
        list = newlist
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(string:String):String {
//        Log.d("Time", "Exp original: ${string?.substring(0,19)}")
        val localDateTime: LocalDateTime = LocalDateTime.parse(string.substring(0,19));

        val date = convertLongToTime(localDateTime.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli())
        val nowdate = convertLongToTime(System.currentTimeMillis())

//        Log.d("Time", "Exp: ${date} and now: ${nowdate}")
        return date
    }
    fun convertLongToTime(time: Long?): String {
        val date = time?.let { Date(it) }
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return df.parse(date).time
    }


}