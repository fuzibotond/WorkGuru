package com.license.workguru_app.help_request.presentation.components

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.license.workguru_app.R
import com.license.workguru_app.databinding.MessageFragmentBinding
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.help_request.presentation.adapters.MessageTabAdapter
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_user_insights.UserHistoryViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_insights.userHistoryViewModelFactory
import com.license.workguru_app.timetracking.presentation.adapters.TabAdapter
import kotlinx.coroutines.launch

class MessageFragment : Fragment() {
    private var _binding: MessageFragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var userHistoryViewModel: UserHistoryViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = MessageFragmentBinding.inflate(inflater, container, false)
        val adapter = MessageTabAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.messagesPageLayout.adapter = adapter
        TabLayoutMediator(binding.messagesLayout,  binding.messagesPageLayout){ tab, position->
            when(position){
                0->{
                    tab.text = getString(R.string.received_messages)
                }
                1->{
                    tab.text = getString(R.string.sent_messages)
                }
            }
        }.attach()

        return binding.root
    }



}