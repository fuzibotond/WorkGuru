package com.license.workguru_app.profile.presentation.components

import com.license.workguru_app.databinding.FilterColleaguesDialogBinding


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.help_request.data.remote.DTO.Skill
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModel
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModelFactory
import com.license.workguru_app.help_request.domain.use_cases.list_statuses.ListStatusesViewModel
import com.license.workguru_app.help_request.domain.use_cases.list_statuses.ListStatusesViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FilterColleaguesDialog(
): DialogFragment() {
    private var _binding: FilterColleaguesDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var listSkillsViewModel: ListSkillsViewModel
    lateinit var listStatusesViewModel: ListStatusesViewModel

    var skill: String = ""
    var status: String = ""
    var minNumberOfWorkHours: Int = 0

    val sharedViewModel: SharedViewModel by activityViewModels()

    var statusList = MutableLiveData<List<Skill>>()
    var skillList = MutableLiveData<List<Skill>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = FilterColleaguesDialogBinding.inflate(inflater, container, false)
        handleThatBackPress()
        initialize()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListSkillsViewModelFactory(requireActivity(), HelpRequestRepository())
        listSkillsViewModel = ViewModelProvider(this, factory).get(ListSkillsViewModel::class.java)

        val factoryStatuses = ListStatusesViewModelFactory(requireActivity(), HelpRequestRepository())
        listStatusesViewModel = ViewModelProvider(this, factoryStatuses).get(ListStatusesViewModel::class.java)

        lifecycleScope.launch {

            if (listSkillsViewModel.listSkillsViewModel(0)){
                skillList.value = listSkillsViewModel.skillList.value!!
            }

            if (listStatusesViewModel.listStatuses(0)) {
                statusList.value = listStatusesViewModel.statusList.value
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        binding.minColleagueWorkTimeInHour.maxValue = 999
        binding.minColleagueWorkTimeInHour.minValue = 1

        skillList.observe(viewLifecycleOwner){
            val temp = mutableListOf<String>()
            skillList.value?.forEach {
                temp.add(it.name)
            }
            chooseSkill(temp as ArrayList<String>)
        }
        statusList.observe(viewLifecycleOwner){
            val temp = mutableListOf<String>()
            statusList.value!!.forEach {
                temp.add(it.name)
            }
            chooseStatus(temp as ArrayList<String>)
        }

        binding.colleagueFilterBtn.setOnClickListener {
            sharedViewModel.saveColleagueFilterCriteria(skill, binding.minColleagueWorkTimeInHour.value, status, true)
            dialog?.dismiss()
        }
        binding.termsAndCondCancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

    }

    private fun chooseSkill(arrayList: ArrayList<String>) {

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, arrayList as List<String>)
        binding.colleagueLanguagesSpinner.setAdapter(adapter)

        binding.colleagueLanguagesSpinner.setOnItemClickListener { adapterView, view, i, l ->
            skill = adapterView.adapter.getItem(i) as String
        }

    }

    private fun chooseStatus(arrayList: ArrayList<String>) {

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, arrayList as List<String>)
        binding.colleagueStatusSpinner.setAdapter(adapter)

        binding.colleagueStatusSpinner.setOnItemClickListener { adapterView, view, i, l ->
            status = adapterView.adapter.getItem(i) as String
        }

    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun handleThatBackPress(){
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.projectListFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}