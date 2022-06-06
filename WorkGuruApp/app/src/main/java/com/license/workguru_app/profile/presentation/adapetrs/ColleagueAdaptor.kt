package com.license.workguru_app.profile.presentation.adapetrs


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.help_request.presentation.components.SendMessageDialog
import com.license.workguru_app.profile.data.remote.DTO.Colleague
import com.license.workguru_app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController


class ColleagueAdaptor(
    private var list: ArrayList<Colleague>,
    private val context: Context,
    private val sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<ColleagueAdaptor.DataViewHolder>() {

    // 1. user defined ViewHolder type - Embedded class!
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val colleagueName:TextView = itemView.findViewById(R.id.colleague_name)
        val colleagueAvatar:ImageView = itemView.findViewById(R.id.colleague_image)
        val colleagueRole:Chip = itemView.findViewById(R.id.colleague_role)
        val colleagueEmail:TextView = itemView.findViewById(R.id.colleague_email)
        val colleagueStatus:Chip = itemView.findViewById(R.id.colleague_status)
        val colleagueLanguages:ChipGroup = itemView.findViewById(R.id.colleague_languages_chip_group)
        val colleagueTracked:TextView = itemView.findViewById(R.id.colleague_tracked)

        init{

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

        }
        override fun onClick(p0: View?) {
            val currentPosition = this.adapterPosition
            val currentItem = list[currentPosition]
            sharedViewModel.saveMessageColleagueUserId(currentItem.id)
            sharedViewModel.saveMessageColleagueUserName(currentItem.name)

            val activity = context as FragmentActivity
            activity.findNavController(R.id.auth_nav_host_fragment).navigate(R.id.chatFragment)
//            val fm: FragmentManager = activity.supportFragmentManager
//            val alertDialog = SendMessageDialog()
//            alertDialog.show(fm, "fragment_alert");

        }

        override fun onLongClick(p0: View?): Boolean {
            val currentPosition = this.adapterPosition
            return true
        }

    }

    // 2. Called only a few times = number of items on screen + a few more ones
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.colleague_adapter_layout, parent, false)

        return DataViewHolder(itemView)
    }


    // 3. Called many times, when we scroll the list
    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = list[position]

        holder.colleagueName.setText(currentItem.name)
        if (currentItem.avatar != null){
            val imgUri = Uri.parse(Constants.VUE_APP_USER_AVATAR_URL+currentItem.avatar)
            Glide.with(context)
                .load(imgUri)
                .circleCrop()
                .into(holder.colleagueAvatar)
        }

        holder.colleagueEmail.setText(currentItem.email)
        when(currentItem.role){
            "admin" -> {
                holder.colleagueRole.setText(currentItem.role)
                holder.colleagueRole.setChipBackgroundColorResource(R.color.yellow_100)
            }
            "user" -> {
                holder.colleagueRole.setText(currentItem.role)
                holder.colleagueRole.setChipBackgroundColorResource(R.color.teal_100)
            }
        }
        holder.colleagueTracked.setText(convertIntToMinutes(currentItem.tracked))
        when(currentItem.status){
            "Available" -> {
                holder.colleagueStatus.setText(currentItem.status)
                holder.colleagueStatus.setChipIconResource(R.drawable.ic_baseline_check_circle_24)
                holder.colleagueStatus.setChipIconTintResource(R.color.teal_200)
//                holder.colleagueStatus.setChipBackgroundColorResource(R.color.teal_200)
            }
            "Busy" -> {
                holder.colleagueStatus.setText(currentItem.status)
                holder.colleagueStatus.setChipIconResource(R.drawable.ic_baseline_cancel_24)
                holder.colleagueStatus.setChipIconTintResource(R.color.yellow_500)
//                holder.colleagueStatus.setChipBackgroundColorResource(R.color.yellow_200)

            }
            "Offline" -> {
                holder.colleagueStatus.setText(currentItem.status)
                holder.colleagueStatus.setChipIconResource(R.drawable.ic_baseline_remove_circle_24)
                holder.colleagueStatus.setChipIconTintResource(R.color.red_200)
//                holder.colleagueStatus.setChipBackgroundColorResource(R.color.red_200)

            }
            null-> {
                holder.colleagueStatus.setText(context.getString(R.string.j_dont_know))
                holder.colleagueStatus.setChipIconResource(R.drawable.ic_baseline_recommend_24)
                holder.colleagueStatus.setChipIconTintResource(R.color.purple_200)
//                holder.colleagueStatus.setChipBackgroundColorResource(R.color.purple_200)
            }
        }
        val languages = mutableListOf<String>()
        currentItem.languages.forEach {
            val temp = it.name + " " + convertIntToMinutes(it.tracked.toInt())
            languages.add(temp)
        }
        holder.colleagueLanguages.removeAllViews()
        languages.forEach {
            val chip = Chip(context)
            chip.text = it
            chip.setChipBackgroundColorResource(R.color.teal_100)
//            chip.setTextColor(context.getColor(R.color.white))
//            chip.setTextAppearance(R.style.ShapeAppearanceOverlay_Material3_Chip)
            holder.colleagueLanguages.addView(chip)
        }
    }

    override fun getItemCount() = list.size

    // Update the list
    fun setData(newList: ArrayList<Colleague>){
        list = newList
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
    private fun convertIntToMinutes(doubleValue: Int):String{

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