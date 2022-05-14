package com.license.workguru_app.timetracking.presentation.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.databinding.FilterDialogBinding
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import android.widget.CalendarView.OnDateChangeListener
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory


class FilterDialog(
): DialogFragment() {
    private var _binding: FilterDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var listCategoriesViewModel: ListCategoriesViewModel
    lateinit var listProjectsViewModel:ListProjectsViewModel

    var choosenCategory:Category? = null
    var numOfWantedMembers:Int = 1
    var startDate:MutableLiveData<Long> = MutableLiveData()
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = FilterDialogBinding.inflate(inflater, container, false)
        handleThatBackPress()
        initialize()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListCategoriesViewModelFactory(requireActivity(), TimeTrackingRepository())
        listCategoriesViewModel = ViewModelProvider(this, factory).get(ListCategoriesViewModel::class.java)

        val factoryProjects = ListProjectsViewModelFactory(requireActivity(), TimeTrackingRepository())
        listProjectsViewModel = ViewModelProvider(this, factoryProjects).get(ListProjectsViewModel::class.java)
        lifecycleScope.launch {
            listCategoriesViewModel.listCategories()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        val calendar = Calendar.getInstance()

        binding.startedAfterCalendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(year,month,dayOfMonth)
        })
        numOfWantedMembers = chooseNumberOfMembers()
        listCategoriesViewModel.dataList.observe(viewLifecycleOwner){
             chooseCategory(listCategoriesViewModel.dataList.value as ArrayList<Category>)
        }

        binding.filterBtn.setOnClickListener {
            if (choosenCategory == null){
                choosenCategory = listCategoriesViewModel.dataList.value?.first()
            }
            lifecycleScope.launch {
                sharedViewModel.saveFilterResult(choosenCategory!!, numOfWantedMembers, calendar.timeInMillis, true)
                dialog?.dismiss()
            }

        }
        binding.termsAndCondCancelBtn.setOnClickListener {
            dialog?.dismiss()
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
    private fun chooseCategory(categoryList:ArrayList<Category>) {

        val itemList = mutableListOf<String>()
        categoryList.forEach {
            itemList.add(it.category_name)
        }

        binding.categorySpinner?.adapter = activity?.let { ArrayAdapter(it.applicationContext, R.layout.ordering_item_layout,
            itemList ) } as SpinnerAdapter

        binding.categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireActivity(), "You not selected any category", Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val type = parent?.getItemAtPosition(position).toString()
                categoryList.forEach {
                    if (it.category_name == type){
                        choosenCategory = it
                        Log.d("LIST", "${it}")
                    }

                }

            }
        }

    }
    private fun chooseNumberOfMembers():Int {
        var numMembers:Int = 0

        binding.memberSpinner?.adapter = activity?.let { ArrayAdapter(it.applicationContext, R.layout.ordering_item_layout,
            listOf(1, 2, 5, 10, 30, 50, 100 ) ) } as SpinnerAdapter

        binding.memberSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireActivity(), "You not selected any category", Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val type = parent?.getItemAtPosition(position).toString()
                numMembers = type.toInt()

            }
        }
        return numMembers
    }

    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}