package com.license.workguru_app.profile.presentation.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.license.workguru_app.databinding.FragmentChartBinding
import com.anychart.enums.LegendLayout

import com.anychart.chart.common.dataentry.ValueDataEntry

import com.anychart.chart.common.dataentry.DataEntry

import com.anychart.AnyChart


import com.anychart.enums.Align
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.anychart.enums.Position
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_insights.UserHistoryViewModel


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()
    var namesForToday = mutableListOf<String>()
    var numbersForToday = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Kotlin DSL example

        _binding = FragmentChartBinding.inflate(inflater, container, false)
        binding.chartProgressBarForToday.visibility = View.VISIBLE
        setObservers()


        return binding.root
    }

    private fun setObservers() {
        sharedViewModel.chartData.observe(viewLifecycleOwner){
            namesForToday.clear()

            numbersForToday.clear()

            sharedViewModel.chartData.value?.forEach {
                namesForToday.add(it.name)
                numbersForToday.add(it.result.toInt())
            }
            binding.chartProgressBarForToday.visibility = View.GONE

            setupPieChart()
        }
    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun setupPieChart() {


        val pie = AnyChart.pie()

        val data = ArrayList<DataEntry>()

        for(i in namesForToday.indices)
            data.add(ValueDataEntry(namesForToday[i],numbersForToday[i]))

        pie.data(data)


        val nightModeFlags = context!!.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                pie.tooltip().background("#000")

                pie.title(getString(R.string.mTrackedLastDays))
                pie.title().fontFamily("Inter")
                pie.title().fontColor("#000")
                pie.title().fontSize(18)
                pie.background("@color/what_wrapped")
                pie.tooltip().fontColor("grey")
                pie.tooltip().title().fontColor("grey")
                pie.tooltip().format("function() {" +
                        "    const sec = parseInt(this.value, 10); // convert value to number if it's string\n" +
                        "    let hours   = Math.floor(sec / 3600); // get hours\n" +
                        "    let minutes = Math.floor((sec - (hours * 3600)) / 60); // get minutes\n" +
                        "    let seconds = sec - (hours * 3600) - (minutes * 60); //  get seconds\n" +
                        "    // add 0 if value < 10; Example: 2 => 02\n" +
                        "    if (hours   < 10) {hours   = \"0\"+hours;}\n" +
                        "    if (minutes < 10) {minutes = \"0\"+minutes;}\n" +
                        "    if (seconds < 10) {seconds = \"0\"+seconds;}\n" +
                        "    return hours+':'+minutes+':'+seconds; // Return is HH : MM : SS\n" +
                        "}")
                pie.title().fontFamily("Inter")
                pie.labels().position("outside")
                pie.legend().title("Legend")
                pie.legend().enabled(true)
                pie.legend().title().padding(0.0,0.0,10.0,0.0)
                pie.legend()
                    .position("bottom")
                    .align(Align.CENTER)
                    .itemsLayout(LegendLayout.VERTICAL)
                    .iconSize(15)
                pie.credits(false)

            }
            Configuration.UI_MODE_NIGHT_NO -> {
                pie.tooltip().background("#000")
                pie.title(getString(R.string.mTrackedLastDays))
                pie.title().fontFamily("Inter")
                pie.title().fontColor("#000")
                pie.title().fontSize(18)
                pie.labels().position("outside")
                pie.tooltip().format("function() {" +
                        "    const sec = parseInt(this.value, 10); // convert value to number if it's string\n" +
                        "    let hours   = Math.floor(sec / 3600); // get hours\n" +
                        "    let minutes = Math.floor((sec - (hours * 3600)) / 60); // get minutes\n" +
                        "    let seconds = sec - (hours * 3600) - (minutes * 60); //  get seconds\n" +
                        "    // add 0 if value < 10; Example: 2 => 02\n" +
                        "    if (hours   < 10) {hours   = \"0\"+hours;}\n" +
                        "    if (minutes < 10) {minutes = \"0\"+minutes;}\n" +
                        "    if (seconds < 10) {seconds = \"0\"+seconds;}\n" +
                        "    return hours+':'+minutes+':'+seconds; // Return is HH : MM : SS\n" +
                        "}")
                pie.legend().title("Legend")
                pie.legend().enabled(true)
                pie.legend().title().padding(0.0,0.0,10.0,0.0)
                pie.legend()
                    .position("bottom")
                    .align(Align.CENTER)
                    .itemsLayout(LegendLayout.VERTICAL_EXPANDABLE)
                    .iconSize(10)
                pie.credits(false)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                pie.title(getString(R.string.mTrackedLastDays))
                pie.title().fontFamily("Inter")
                pie.title().fontColor("#000")
                pie.title().fontSize(18)
                pie.labels().position("outside")
                pie.tooltip().format("function() {" +
                        "    const sec = parseInt(this.value, 10); // convert value to number if it's string\n" +
                        "    let hours   = Math.floor(sec / 3600); // get hours\n" +
                        "    let minutes = Math.floor((sec - (hours * 3600)) / 60); // get minutes\n" +
                        "    let seconds = sec - (hours * 3600) - (minutes * 60); //  get seconds\n" +
                        "    // add 0 if value < 10; Example: 2 => 02\n" +
                        "    if (hours   < 10) {hours   = \"0\"+hours;}\n" +
                        "    if (minutes < 10) {minutes = \"0\"+minutes;}\n" +
                        "    if (seconds < 10) {seconds = \"0\"+seconds;}\n" +
                        "    return hours+':'+minutes+':'+seconds; // Return is HH : MM : SS\n" +
                        "}")
                pie.legend().title("Legend")
                pie.legend().enabled(true)
                pie.legend().title().padding(0.0,0.0,10.0,0.0)
                pie.legend()
                    .position("bottom")
                    .align(Align.CENTER)
                    .itemsLayout(LegendLayout.VERTICAL_EXPANDABLE)
                    .iconSize(10)
                pie.credits(false)
            }

        }
        pie.palette(arrayOf("#DDFFE7", "#98D7C2", "#167D7F", "#29A0B1","#05445E","#189AB4", "#75E6DA", "#D4F1F4", "#5885AF", "#FF03DAC5", "#FF018786", "#134E4A"))

        binding.chartViewForToday.setChart(pie)


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