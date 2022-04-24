package com.license.workguru_app.profile.presentation.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.license.workguru_app.R
import com.license.workguru_app.databinding.FragmentDashboardBinding
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_user_insights.UserHistoryViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_insights.userHistoryViewModelFactory
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.presentation.adapters.TabAdapter
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    lateinit var userHistoryViewModel: UserHistoryViewModel

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
                    tab.text ="Charts"
                }
                1->{
                    tab.text ="Statements"
                }
            }
        }.attach()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = userHistoryViewModelFactory(requireActivity(), ProfileRepository())
        userHistoryViewModel = ViewModelProvider(this, factory).get(UserHistoryViewModel::class.java)
        lifecycleScope.launch {
            userHistoryViewModel.listUserHistory()
        }
    }

}