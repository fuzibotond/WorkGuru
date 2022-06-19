package com.license.workguru_app.profile.presentation.components

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.databinding.FragmentStatmentsBinding
import com.license.workguru_app.profile.data.remote.DTO.MonthlyHistory
import com.license.workguru_app.profile.data.remote.DTO.TodaysHistory
import com.license.workguru_app.profile.data.remote.DTO.WeeklyHistory
import com.license.workguru_app.timetracking.domain.model.Statement
import com.license.workguru_app.timetracking.presentation.adapters.StatementAdapter
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.reflect.jvm.internal.impl.resolve.constants.DoubleValue
import java.util.concurrent.TimeUnit


class StatementsFragment : Fragment(), StatementAdapter.OnItemLongClickListener,
    StatementAdapter.OnItemClickListener {
    private var _binding: FragmentStatmentsBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter:StatementAdapter
    val sharedViewModel: SharedViewModel by activityViewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStatmentsBinding.inflate(inflater, container, false)

        setObservers()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        sharedViewModel.todaysHistory.observe(viewLifecycleOwner){
            val todaysHistory = sharedViewModel.todaysHistory.value!!
            //todays
            binding.trackedTimeAtStatementToday.text = convertLongToMinutes(todaysHistory.today_tracked.toDouble())
            binding.lessTrackedTimeAtStatementToday.text = convertLongToMinutes(todaysHistory.yesterday_tracked.toDouble())
            binding.percentAtStatementToday.text = todayPercentCalculator(todaysHistory.percent)
        }
        sharedViewModel.weeklyHistory.observe(viewLifecycleOwner){
            val weeklyHistory = sharedViewModel.weeklyHistory.value!!
            //weekly
            binding.trackedTimeAtStatementWeekly.text = convertLongToMinutes(weeklyHistory.this_week.toDouble())
            binding.lessTrackedTimeAtStatementWeekly.text = convertLongToMinutes(weeklyHistory.last_week.toDouble())
            binding.percentAtStatementWeekly.text = weeklyPercentCalculator(weeklyHistory.percent)
        }
        sharedViewModel.monthlyHistory.observe(viewLifecycleOwner){
            val monthlyHistory = sharedViewModel.monthlyHistory.value!!
            //monthly
            binding.trackedTimeAtStatementMonthly.text = convertLongToMinutes(monthlyHistory.this_month.toDouble())
            binding.lessTrackedTimeAtStatementMonthly.text = convertLongToMinutes(monthlyHistory.last_month.toDouble())
            binding.percentAtStatementMonthly.text = monthlyPercentCalculator(monthlyHistory.percent)
        }

        binding.progressStatement.visibility = View.GONE


    }
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    private fun todayPercentCalculator(value:Double):String{
        if (value >= 0.0){
//            binding.todayBackground.setBackgroundColor(R.color.teal_100)
            binding.todayArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_upward_24))
            return value.toString().take(4) + " %"
        }
        else
        {
//            binding.todayBackground.setBackgroundColor(R.color.red_200)
            binding.todayArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_downward_24))
            return value.toString().take(4) + " %"
        }

    }
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    private fun weeklyPercentCalculator(value:Double):String{
        if (value >= 0.0){
//            binding.weeklyBackground.setBackgroundColor(R.color.teal_100)
            binding.weeklyArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_upward_24))
            return value.toString().take(4) + " %"
        }
        else
        {
//            binding.weeklyBackground.setBackgroundColor(R.color.red_200)
            binding.weeklyArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_downward_24))
            return value.toString().take(4) + " %"
        }

    }
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    private fun monthlyPercentCalculator(value:Double):String{
        if (value >= 0.0){
//            binding.monthlyBackground.setBackgroundColor(R.color.teal_100)
            binding.monthlyArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_upward_24))
            return value.toString().take(4) + " %"
        }
        else
        {
            binding.monthlyArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_downward_24))
            return value.toString().take(4) + " %"
        }

    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertLongToMinutes(doubleValue: Double):String{
        val temp  = (doubleValue / 1)
        val value = temp.toInt()

        val hours: Int = value / 3600
        val minutes: Int = value % 3600 / 60
        val seconds: Int = value % 3600 % 60

//        Log.d("HISTORY", doubleValue.toString())
//        Log.d("HISTORY", value.toString())
        Log.d("HISTORY", "${hours} ${minutes} ${hours}")
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

    override fun onItemLongClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }


}