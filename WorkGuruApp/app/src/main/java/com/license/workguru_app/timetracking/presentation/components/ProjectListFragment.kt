package com.license.workguru_app.timetracking.presentation.components

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.databinding.FragmentProjectListBinding
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory
import com.license.workguru_app.timetracking.presentation.adapters.ProjectAdapter
import kotlinx.android.synthetic.main.activity_authorized.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class ProjectListFragment : Fragment(){
    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter:ProjectAdapter
    private lateinit var listProjectsViewModel: ListProjectsViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()
    val itemList:MutableList<Project> = mutableListOf()
    val searchResultList:MutableList<Project> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        setSearchingListeners()
        setOrderingListener()
        setupFilter()
        fillData()
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.searchWithKeyword("")
        getData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSearchingListeners() {
        sharedViewModel.searchingKeyword.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "Changed ${sharedViewModel.searchingKeyword.value}", Toast.LENGTH_SHORT).show()
            searchResultList.clear()
            if (sharedViewModel.searchingKeyword.value!=""){
                itemList.forEach {
                    if (it.name.contains(sharedViewModel.searchingKeyword.value!!, ignoreCase = true)){
                        searchResultList.add(it)
                    }
                }
                adapter.setData(searchResultList as ArrayList<Project>)
                adapter.notifyDataSetChanged()
            }
            else{
                adapter.setData(itemList as ArrayList<Project>)
                adapter.notifyDataSetChanged()
            }

        }
    }

    private fun setupFilter() {
        binding.openFilterModal.setOnClickListener {
            val manager = (context as AppCompatActivity).supportFragmentManager
            FilterDialog().show(manager, "CustomManager")
        }
        sharedViewModel.choosenCategory.observe(viewLifecycleOwner){
            lifecycleScope.launch {
                listProjectsViewModel.listProjects(false, sharedViewModel.choosenCategory.value!!.id.toString())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupOrder(itemList:ArrayList<Project>) {
        binding.projectOrder?.adapter = activity?.let { ArrayAdapter(it.applicationContext, R.layout.ordering_item_layout,
            listOf("Order by name", "Order by start date", "Order by category") ) } as SpinnerAdapter

        binding.projectOrder?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                if (type == "Order by name"){
                    val temp = itemList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name }))
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == "Order by category"){
                    val temp = itemList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.category_name }))
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == "Order by start date"){
                    itemList.sortBy { it.start_date }
                }
                adapter.notifyDataSetChanged()

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListProjectsViewModelFactory(this.requireContext(), TimeTrackingRepository())
        listProjectsViewModel = ViewModelProvider(this, factory).get(ListProjectsViewModel::class.java)
        getData()

    }


    private fun getData() {
       lifecycleScope.launch {
           listProjectsViewModel.listProjects(false, "1")
       }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun fillData() {
        adapter = ProjectAdapter((itemList as ArrayList<Project>),this.requireContext(), sharedViewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        setupOrder(itemList)



    }
    @SuppressLint("NotifyDataSetChanged")
    private fun setOrderingListener(){
        listProjectsViewModel.dataList.observe(viewLifecycleOwner){
            itemList.clear()
            val data = listProjectsViewModel.dataList.value

            if (data != null) {
                if (sharedViewModel.startedAtDate.value!=null){

                    data.forEach {
                        Log.d("TIME", "Selected ${it}")
                        if (it.members<= sharedViewModel.numberOfWantedMembers.value!! &&
                            it.start_date>=convertLongToTime(sharedViewModel.startedAtDate.value) &&
                            it.category_name.equals(
                                sharedViewModel.choosenCategory.value!!.category_name) )
                            itemList.add(it)
                    }
                }else{
                    data.forEach {
                        itemList.add(it)
                    }
                }

                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateToLong(date: String): Long {
        val localDateTime: LocalDateTime = LocalDateTime.parse(date.substring(0,19));

        val dateTime = localDateTime.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

        val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return df.parse(dateTime.toString()).time
    }

}


