package com.license.workguru_app.profile.presentation.components

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.license.workguru_app.R
import com.license.workguru_app.databinding.FragmentColleguesBinding
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.profile.data.remote.DTO.Colleague
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_all_colleagues.ListColleaguesViewModel
import com.license.workguru_app.profile.domain.use_case.display_all_colleagues.ListColleaguesViewModelFactory
import com.license.workguru_app.profile.presentation.adapetrs.ColleagueAdaptor
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class ColleaguesFragment : Fragment() {
    private var _binding: FragmentColleguesBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: ColleagueAdaptor
    lateinit var listColleaguesViewModel: ListColleaguesViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()
    val itemList:ArrayList<Colleague> = arrayListOf()
    var loading = true
    var pastItemsVisible = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var page = 1



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentColleguesBinding.inflate(inflater, container, false)
        btnListeners()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(itemList: ArrayList<Colleague>) {
        adapter = ColleagueAdaptor(itemList,this.requireContext(), sharedViewModel)
        binding.recyclerViewForColleagues.adapter = adapter
        binding.recyclerViewForColleagues.layoutManager = LinearLayoutManager(this.context)
        adapter.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun btnListeners() {
        binding.colleagueListSwipeRefreshLayout.setOnRefreshListener {
            sharedViewModel.saveColleagueFilterCriteria("", 0, "",false)
            page = 1
            itemList.clear()
            getNextPage()
            binding.colleagueListSwipeRefreshLayout.isRefreshing = false

        }

        binding.recyclerViewForColleagues.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = binding.recyclerViewForColleagues.layoutManager!!.getChildCount()
                    totalItemCount = binding.recyclerViewForColleagues.layoutManager!!.getItemCount()
                    pastItemsVisible = (binding.recyclerViewForColleagues.layoutManager!! as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (loading && sharedViewModel.isColleaguesFilterActive.value == false && sharedViewModel.searchingKeyword.value.isNullOrEmpty()) {
                        if (visibleItemCount + pastItemsVisible >= totalItemCount) {
                            loading = false
                            Log.d("LOADING", "Last Item !")
                            binding.colleagueProgressBar.visibility = View.VISIBLE
                            getNextPage()

                        }
                    }
                }
            }
        })

       binding.openFilterModalBtn.setOnClickListener {
           val manager = requireActivity().supportFragmentManager
           FilterColleaguesDialog().show(manager, "CustomManager")
       }

        sharedViewModel.searchingKeyword.observe(viewLifecycleOwner){
            if(sharedViewModel.searchingKeyword.value!!.length < 1){
                Toast.makeText(requireActivity(), "Empty key", Toast.LENGTH_SHORT).show()
                adapter.setData(itemList)
                adapter.notifyDataSetChanged()
            }else{
                val searchResultList = arrayListOf<Colleague>()
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
                val filteredResultList = arrayListOf<Colleague>()
//                itemList.forEach {
//                    if (
//                        it.members >= sharedViewModel.numberOfWantedMembers.value!!
//                        && it.category_name.equals(sharedViewModel.choosenCategory.value!!.category_name)
//                        && formatDate(it.start_date) >= sharedViewModel.startedAtDate.value!!
//                    ){
//                        filteredResultList.add(it)
//                    }
//                }
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
            if(listColleaguesViewModel.listColleagues(page)){
                binding.colleagueProgressBar.visibility = View.GONE
                page+=1
                itemList.addAll(listColleaguesViewModel.colleagues.value!!)
                adapter.setData(itemList)
                adapter.notifyDataSetChanged()
                loading = true
            }
        }
    }

    private fun setupOrder(itemList:ArrayList<Colleague>) {
        binding.colleagueOrder?.adapter = activity?.let { ArrayAdapter(it.applicationContext, R.layout.ordering_item_layout,
            listOf("Order by name", "Order by start date", "Order by category") ) } as SpinnerAdapter

        binding.colleagueOrder?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                if (type == "Order by status (only who is available)"){
                    val temp = itemList.filter{it.role == "admin"} // TODO: update to availability
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == "Order by tracked time"){
                    itemList.sortBy { it.tracked }
                }
                adapter.notifyDataSetChanged()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListColleaguesViewModelFactory(this.requireContext(), ProfileRepository())
        listColleaguesViewModel = ViewModelProvider(this, factory).get(ListColleaguesViewModel::class.java)
        getData()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
        lifecycleScope.launch {
            if(listColleaguesViewModel.listColleagues(page)){
                itemList.addAll((listColleaguesViewModel.colleagues.value as ArrayList<Colleague>?)!!)
                setAdapter(itemList = itemList)
                setupOrder(itemList)
                binding.colleagueProgressBar.visibility = View.GONE
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