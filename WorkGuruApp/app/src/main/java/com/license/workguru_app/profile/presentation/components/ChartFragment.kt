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
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_insights.UserHistoryViewModel


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()
    var names = mutableListOf<String>()
    var numbers = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Kotlin DSL example

        _binding = FragmentChartBinding.inflate(inflater, container, false)
        binding.chartProgressBar.visibility = View.VISIBLE
        setObservers()


        return binding.root
    }

    private fun setObservers() {
        sharedViewModel.chartData.observe(viewLifecycleOwner){
            names.clear()
            numbers.clear()
            sharedViewModel.chartData.value?.forEach {
                names.add(it.name)
                numbers.add(it.project_id)
            }
            binding.chartProgressBar.visibility = View.GONE
            setupPieChart()
        }
    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun setupPieChart() {


        val pie = AnyChart.pie()

        val data = ArrayList<DataEntry>()

        for(i in names.indices)
            data.add(ValueDataEntry(names[i],numbers[i]))

        pie.data(data)

        val nightModeFlags = context!!.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                pie.title("Tracked - Last 30 Days dark")
                pie.background("@color/what_wrapped")
                pie.tooltip().fontColor("grey")
                pie.tooltip().title().fontColor("grey")
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
                pie.title("Tracked - Last 30 Days no dark")
                pie.title().fontFamily("Inter")
                pie.title().fontColor("#000")
                pie.labels().position("outside")
                pie.legend().title("Legend")
                pie.legend().enabled(true)
                pie.legend().title().padding(0.0,0.0,10.0,0.0)
                pie.legend()
                    .position("bottom")
                    .align(Align.CENTER)
                    .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                    .iconSize(10)
                pie.credits(false)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                pie.title("Tracked - Last 30 Days undefined")
                pie.title().fontFamily("Inter")
                pie.title().fontColor("#000")
                pie.labels().position("outside")
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

        binding.chartView.setChart(pie)


    }

}