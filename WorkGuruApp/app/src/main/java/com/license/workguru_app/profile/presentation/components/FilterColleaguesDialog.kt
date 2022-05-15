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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FilterColleaguesDialog(
): DialogFragment() {
    private var _binding: FilterColleaguesDialogBinding? = null
    private val binding get() = _binding!!
//    lateinit var listCategoriesViewModel: ListCategoriesViewModel
//    lateinit var listProjectsViewModel:ListProjectsViewModel

    var skill: String = ""
    var status: String = ""
    var minNumberOfWorkHours: Int = 0

    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = FilterColleaguesDialogBinding.inflate(inflater, container, false)
        handleThatBackPress()
        initialize()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val factory = ListCategoriesViewModelFactory(requireActivity(), TimeTrackingRepository())
//        listCategoriesViewModel = ViewModelProvider(this, factory).get(ListCategoriesViewModel::class.java)
//
//        val factoryProjects = ListProjectsViewModelFactory(requireActivity(), TimeTrackingRepository())
//        listProjectsViewModel = ViewModelProvider(this, factoryProjects).get(ListProjectsViewModel::class.java)
//
//        lifecycleScope.launch {
//            listCategoriesViewModel.listCategories()
//        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        binding.minColleagueWorkTimeInHour.maxValue = 999
        binding.minColleagueWorkTimeInHour.minValue = 1
        chooseSkill(arrayListOf("C++", "Kotlin", "PHP", "Vue.js", ".NET"))
        chooseStatus(arrayListOf("Available", "Busy", "Out of House", "On Vacation", "Available but can't help") )

//        listCategoriesViewModel.dataList.observe(viewLifecycleOwner){
//            //TODO: Request for languages and statuses
//
//
//        }

        binding.colleagueFilterBtn.setOnClickListener {
            sharedViewModel.saveColleagueFilterCriteria(skill,binding.minColleagueWorkTimeInHour.value, status, true)
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