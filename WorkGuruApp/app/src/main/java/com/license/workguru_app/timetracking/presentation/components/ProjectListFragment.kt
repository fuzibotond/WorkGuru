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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.databinding.FragmentProjectListBinding
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory
import com.license.workguru_app.timetracking.presentation.adapters.ProjectAdapter
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class ProjectListFragment : Fragment() {
    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter:ProjectAdapter
    private lateinit var listProjectsViewModel: ListProjectsViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()
    val itemList:ArrayList<Project> = arrayListOf()
    var loading = true
    var pastItemsVisible = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var page = 2


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)

        btnListeners()
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(itemList: ArrayList<Project>) {
        adapter = ProjectAdapter(itemList,this.requireContext(), sharedViewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun btnListeners() {
        binding.projectListSwipeRefreshLayout.setOnRefreshListener {
            sharedViewModel.saveFilterResult(null, 0, 0L,false)
            page = 1
            itemList.clear()
            getNextPage()
            binding.projectListSwipeRefreshLayout.isRefreshing = false

        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = binding.recyclerView.layoutManager!!.getChildCount()
                    totalItemCount = binding.recyclerView.layoutManager!!.getItemCount()
                    pastItemsVisible = (binding.recyclerView.layoutManager!! as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (loading && sharedViewModel.isFiltered.value == false) {
                        if (visibleItemCount + pastItemsVisible >= totalItemCount) {
                            loading = false
                            Log.d("LOADING", "Last Item !")
                            binding.progressBar.visibility = View.VISIBLE
                            getNextPage()

                        }
                    }
                }
            }
        })

        binding.openFilterModal.setOnClickListener {
            val manager = (context as AppCompatActivity).supportFragmentManager
            FilterDialog().show(manager, "CustomManager")
        }
        sharedViewModel.searchingKeyword.observe(viewLifecycleOwner){
            if(sharedViewModel.searchingKeyword.value!!.length < 1){
                Toast.makeText(requireActivity(), "Empty key", Toast.LENGTH_SHORT).show()
                adapter.setData(itemList)
                adapter.notifyDataSetChanged()
            }else{
                val searchResultList = arrayListOf<Project>()
                itemList.forEach {
                    if (it.name.contains(sharedViewModel.searchingKeyword.value!!, ignoreCase = true)){
                        searchResultList.add(it)
                    }
                }
                adapter.setData(searchResultList)
                setupOrder(searchResultList)
            }

        }

        sharedViewModel.isFiltered.observe(viewLifecycleOwner){
            if (sharedViewModel.isFiltered.value == true ){
                val filteredResultList = arrayListOf<Project>()
                itemList.forEach {
                    if (
                        it.members >= sharedViewModel.numberOfWantedMembers.value!!
                        && it.category_name.equals(sharedViewModel.choosenCategory.value!!.category_name)
                      && formatDate(it.start_date) >= sharedViewModel.startedAtDate.value!!
                    ){
                        filteredResultList.add(it)
                    }
                }
                if (filteredResultList.isEmpty()){
                    Toast.makeText(requireActivity(), "Oops! Not a single match...", Toast.LENGTH_SHORT).show()
                }else{
                    adapter.setData(filteredResultList)
                    setupOrder(filteredResultList)
                }

            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getNextPage(){
        lifecycleScope.launch {
            if(listProjectsViewModel.listProjectsByPage(page)){
                binding.progressBar.visibility = View.GONE
                page+=1
                itemList.addAll(listProjectsViewModel.dataList.value!!)
                adapter.setData(itemList)
                adapter.notifyDataSetChanged()
                loading = true
            }
        }
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListProjectsViewModelFactory(this.requireContext(), TimeTrackingRepository())
        listProjectsViewModel = ViewModelProvider(this, factory).get(ListProjectsViewModel::class.java)
        getData()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
       lifecycleScope.launch {
           if(listProjectsViewModel.listAllProjects(false, "0")){
               itemList.addAll((listProjectsViewModel.dataList.value as ArrayList<Project>?)!!)
               setAdapter(itemList = itemList)
               setupOrder(itemList)
               binding.progressBar.visibility = View.GONE
           }
       }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateToLong(date: String): Long {
        val localDateTime: LocalDateTime = LocalDateTime.parse(date.substring(0,19));

        val dateTime = localDateTime.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

        val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return df.parse(dateTime.toString()).time
    }
    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(string:String):Long {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date = format.parse(string.take(19))
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

       return 0


    }


}
