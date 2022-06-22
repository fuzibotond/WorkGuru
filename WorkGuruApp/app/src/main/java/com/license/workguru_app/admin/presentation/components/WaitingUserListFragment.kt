package com.license.workguru_app.admin.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import com.license.workguru_app.admin.data.remote.DTO.WaitingUser
import com.license.workguru_app.admin.data.repository.AdminRepository
import com.license.workguru_app.admin.domain.use_cases.accept_user_by_id.AcceptUserViewModel
import com.license.workguru_app.admin.domain.use_cases.accept_user_by_id.AcceptUserViewModelFactory
import com.license.workguru_app.admin.domain.use_cases.list_users_who_are_waiting.ListUsersWhoAreWaitingViewModel
import com.license.workguru_app.admin.domain.use_cases.list_users_who_are_waiting.ListUsersWhoAreWaitingViewModelFactory
import com.license.workguru_app.admin.presentation.adapters.WaitingUserAdapter
import com.license.workguru_app.databinding.WaitingUserListFragmentBinding
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.timetracking.presentation.components.FilterDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class WaitingUserListFragment : Fragment() {
    private var _binding: WaitingUserListFragmentBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()
    val itemList:ArrayList<WaitingUser> = arrayListOf()
    var loading = true
    var pastItemsVisible = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var page = 2
    lateinit var adapter:WaitingUserAdapter
    lateinit var listUsersWhoAreWaitingViewModel: ListUsersWhoAreWaitingViewModel
    lateinit var acceptUserViewModel: AcceptUserViewModel


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = WaitingUserListFragmentBinding.inflate(inflater, container, false)

        btnListeners()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(itemList: ArrayList<WaitingUser>) {
        adapter = WaitingUserAdapter(itemList,this.requireContext(), sharedViewModel, acceptUserViewModel)
        binding.waitingUsersRecyclerView.adapter = adapter
        binding.waitingUsersRecyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun btnListeners() {
        binding.waitingUsersListSwipeRefreshLayout.setOnRefreshListener {
            sharedViewModel.saveFilterResult(null, 0, 0L,false)
            page = 0
            itemList.clear()
            getNextPage()
            binding.waitingUsersListSwipeRefreshLayout.isRefreshing = false

        }

        binding.waitingUsersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = binding.waitingUsersRecyclerView.layoutManager!!.getChildCount()
                    totalItemCount = binding.waitingUsersRecyclerView.layoutManager!!.getItemCount()
                    pastItemsVisible = (binding.waitingUsersRecyclerView.layoutManager!! as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (loading && sharedViewModel.isFiltered.value == false) {
                        if (visibleItemCount + pastItemsVisible >= totalItemCount) {
                            loading = false
                            Log.d("LOADING", "Last Item !")
                            binding.waitingUsersProgressBar.visibility = View.VISIBLE
                            getNextPage()

                        }
                    }
                }
            }
        })

        binding.openWaitingUsersFilterModal.setOnClickListener {
            val manager = (context as AppCompatActivity).supportFragmentManager
            FilterDialog().show(manager, "CustomManager")
        }
        sharedViewModel.searchingKeyword.observe(viewLifecycleOwner){
            if(sharedViewModel.searchingKeyword.value!!.length < 1){
//                Toast.makeText(requireActivity(), "Empty key", Toast.LENGTH_SHORT).show()
                adapter.setData(itemList)
                adapter.notifyDataSetChanged()
            }else{
                val searchResultList = arrayListOf<WaitingUser>()
                itemList.forEach {
                    if (it.email.contains(sharedViewModel.searchingKeyword.value!!, ignoreCase = true)){
                        searchResultList.add(it)
                    }
                }
                adapter.setData(searchResultList)
//                choseOrder()
                setupOrder(searchResultList)
            }

        }

        binding.waitingUsersListSwipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                delay(2000)
                binding.waitingUsersListSwipeRefreshLayout.isRefreshing = false
                binding.waitingUsersListSwipeRefreshLayout.setProgressViewEndTarget(false, 0)

            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getNextPage(){
        lifecycleScope.launch {
            if(listUsersWhoAreWaitingViewModel.listUsersWhoAreWaiting(page)){
                binding.waitingUsersProgressBar.visibility = View.GONE
                page+=1
                itemList.addAll(listUsersWhoAreWaitingViewModel.userList.value!!)
                adapter.setData(itemList)
                adapter.notifyDataSetChanged()
                loading = true
            }
            else{
                binding.waitingUsersProgressBar.visibility = View.GONE
            }
        }

    }

    private fun setupOrder(itemList:ArrayList<WaitingUser>) {
        binding.waitingUsersOrder?.adapter = activity?.let { ArrayAdapter(it.applicationContext, R.layout.ordering_item_layout,
            listOf(getString(R.string.orderByName), getString(R.string.orderByStartDate), getString(R.string.orderByCategory)) ) } as SpinnerAdapter

        binding.waitingUsersOrder?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireActivity(), getString(R.string.tYouNotSelectedAnyCategory), Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val type = parent?.getItemAtPosition(position).toString()
                if (type == getString(R.string.orderByName)){
                    val temp = itemList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.created_at })) //TODO: make criteria real
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == getString(R.string.orderByCategory)){
                    val temp = itemList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.updated_at })) //TODO: make criteria real
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == getString(R.string.orderByStartDate)){
                    itemList.sortBy { it.created_at }
                }
                adapter.notifyDataSetChanged()
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListUsersWhoAreWaitingViewModelFactory(this.requireContext(), AdminRepository())
        listUsersWhoAreWaitingViewModel = ViewModelProvider(this, factory).get(ListUsersWhoAreWaitingViewModel::class.java)
        val factoryAccept = AcceptUserViewModelFactory(this.requireContext(), AdminRepository())
        acceptUserViewModel = ViewModelProvider(this, factoryAccept).get(AcceptUserViewModel::class.java)
        getData()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
        lifecycleScope.launch {
            val id = getUserId()
            if (id != null) {
                if (listUsersWhoAreWaitingViewModel.listUsersWhoAreWaiting(page)) {
                    itemList.addAll(listUsersWhoAreWaitingViewModel.userList.value!!)
                    setAdapter(itemList = itemList)
                    setupOrder(itemList)
                    binding.waitingUsersProgressBar.visibility = View.GONE
                }
                else{
                    Toast.makeText(requireActivity(), getString(R.string.you_havent_got_any_request_yet), Toast.LENGTH_SHORT).show()
                }
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
    fun getUserId(): Int? {
        val sharedPreferences: SharedPreferences = context!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedId = sharedPreferences.getInt("USER_ID", 0)
        return savedId
    }
}