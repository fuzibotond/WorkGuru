package com.license.workguru_app.admin.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.license.workguru_app.R
import com.license.workguru_app.admin.data.remote.DTO.WaitingUser
import com.license.workguru_app.admin.domain.use_cases.accept_user_by_id.AcceptUserViewModel
import com.license.workguru_app.di.SharedViewModel
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WaitingUserAdapter(
    private var list: ArrayList<WaitingUser>,
    private val context: Context,
    private val sharedViewModel: SharedViewModel,
    private val acceptUserViewModel: AcceptUserViewModel
) :
    RecyclerView.Adapter<WaitingUserAdapter.DataViewHolder>() {

    // 1. user defined ViewHolder type - Embedded class!
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val senderNameTextView: TextView = itemView.findViewById(R.id.sender_name_tv)
        val sendingDateTimeTextView: TextView = itemView.findViewById(R.id.created_date_tv)
        val replyingBtn: MaterialButton = itemView.findViewById(R.id.reply_to_message_btn)
        init{

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

        }
        override fun onClick(p0: View?) {
            val currentPosition = this.adapterPosition

        }

        override fun onLongClick(p0: View?): Boolean {
            val currentPosition = this.adapterPosition
            return true
        }

    }

    // 2. Called only a few times = number of items on screen + a few more ones
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.message_item_layout, parent, false)

        return DataViewHolder(itemView)
    }


    // 3. Called many times, when we scroll the list
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = list[position]
        holder.senderNameTextView.text = currentItem.email
        holder.sendingDateTimeTextView.text = formatDate(currentItem.created_at)

        holder.replyingBtn.setOnClickListener {
            (context as LifecycleOwner).lifecycleScope.launch {
                if(acceptUserViewModel.AcceptUser(currentItem.id)){
                    list.remove(currentItem)
                    this@WaitingUserAdapter.notifyDataSetChanged()
                }
            }
        }


    }

    override fun getItemCount() = list.size

    // Update the list
    fun setData(newlist: ArrayList<WaitingUser>){
        list = newlist
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(string:String):String {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date = format.parse(string.take(19))
            return date.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""

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