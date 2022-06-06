package com.license.workguru_app.profile.presentation.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.license.workguru_app.databinding.FragmentDashboardBinding
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_user_insights.UserHistoryViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_insights.userHistoryViewModelFactory
import com.license.workguru_app.timetracking.presentation.adapters.TabAdapter
import kotlinx.coroutines.launch
import android.view.*
import com.license.workguru_app.R


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    lateinit var userHistoryViewModel: UserHistoryViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val adapter = TabAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.myFaresViewpager.adapter = adapter
        TabLayoutMediator(binding.ongoingSalesAndOrders,  binding.myFaresViewpager){ tab, position->
            when(position){
                0->{
                    tab.text = getString(R.string.charts)
                }
                1->{
                    tab.text = getString(R.string.statements)
                }
            }
        }.attach()


        setObservers()
        return binding.root
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = userHistoryViewModelFactory(requireActivity(), ProfileRepository())
        userHistoryViewModel = ViewModelProvider(this, factory).get(UserHistoryViewModel::class.java)
        lifecycleScope.launch {
            userHistoryViewModel.listUserHistory()
        }
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu);
        val item: MenuItem = menu.findItem(R.id.search_action)
        item.setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun setObservers() {
        userHistoryViewModel.dataList.observe(viewLifecycleOwner){
            val chartData = userHistoryViewModel.dataList.value!!
            sharedViewModel.saveChartData(chartData)
        }
        userHistoryViewModel.todaysHistory.observe(viewLifecycleOwner){
            userHistoryViewModel.todaysHistory.value?.let { it1 ->
                sharedViewModel.saveTodaysData(
                    it1
                )
            }
        }
        userHistoryViewModel.monthlyHistory.observe(viewLifecycleOwner){
            userHistoryViewModel.monthlyHistory.value?.let { it1 ->
                sharedViewModel.saveMonthlyData(
                    it1
                )
            }
        }
        userHistoryViewModel.weeklyHistory.observe(viewLifecycleOwner){
            userHistoryViewModel.weeklyHistory.value?.let { it1 ->
                sharedViewModel.saveWeeklyData(
                    it1
                )
            }
        }
    }

}