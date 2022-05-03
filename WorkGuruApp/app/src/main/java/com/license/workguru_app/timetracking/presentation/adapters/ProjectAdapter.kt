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
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.presentation.components.ProjectListFragment
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class ProjectAdapter(
    private var list: ArrayList<Project>,
    private val context: Context,
    private val sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<ProjectAdapter.DataViewHolder>() {

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
            isExpanded.value = !isExpanded.value!!

        }

        override fun onLongClick(p0: View?): Boolean {
            val currentPosition = this.adapterPosition
            isExpanded.value = !isExpanded.value!!
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
        holder.trackedTimeTextView.text = convertLongToMinutes(currentItem.tracked)
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
    fun setData(newlist: ArrayList<Project>){
        list = newlist
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(string:String):String {
        val localDateTime: LocalDateTime = LocalDateTime.parse(string.substring(0,19));

        val date = convertLongToTime(localDateTime.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli())
        val nowdate = convertLongToTime(System.currentTimeMillis())

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
    private fun convertLongToMinutes(doubleValue: Int):String{

        val value = doubleValue

        val hours: Int = value / 3600
        val minutes: Int = value % 3600 / 60
        if (value == 0){
            return "00:00"
        }

        else if (minutes.toInt() % 60 == 0){
            if (minutes<10){
                return "00:0"+ minutes
            }else{
                return "00:"+minutes
            }
        }else{
            if (hours<10){
                if (minutes<10){
                    return "0"+hours+":0"+minutes
                }else{
                    return "0"+hours+":"+minutes
                }

            }else{
                if (minutes<10){
                    return ""+hours+":0"+minutes
                }else{
                    return ""+hours+":"+minutes
                }
            }
        }

    }

}