package com.license.workguru_app.help_request.presentation.components

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
import com.license.workguru_app.databinding.FragmentMessageListBinding
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.help_request.data.remote.DTO.Message
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.get_messages_related_to_a_user.ListMessagesRelatedToAUser
import com.license.workguru_app.help_request.domain.use_cases.get_messages_related_to_a_user.ListMessagesRelatedToAUserFactory
import com.license.workguru_app.help_request.presentation.adapters.MessageAdapterX
import com.license.workguru_app.timetracking.presentation.components.FilterDialog
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class MessageListFragment : Fragment() {
    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()
    val itemList:ArrayList<Message> = arrayListOf()
    var loading = true
    var pastItemsVisible = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var page = 2
    lateinit var adapterX:MessageAdapterX
    lateinit var listMessagesRelatedToAUser: ListMessagesRelatedToAUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMessageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(itemList: ArrayList<Message>) {
        adapterX = MessageAdapterX(itemList,this.requireContext(), sharedViewModel)
        binding.messagesRecyclerView.adapter = adapterX
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this.context)
        adapterX.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun btnListeners() {
        binding.messagesListSwipeRefreshLayout.setOnRefreshListener {
            sharedViewModel.saveFilterResult(null, 0, 0L,false)
            page = 1
            itemList.clear()
            getNextPage()
            binding.messagesListSwipeRefreshLayout.isRefreshing = false

        }

        binding.messagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = binding.messagesRecyclerView.layoutManager!!.getChildCount()
                    totalItemCount = binding.messagesRecyclerView.layoutManager!!.getItemCount()
                    pastItemsVisible = (binding.messagesRecyclerView.layoutManager!! as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (loading && sharedViewModel.isFiltered.value == false) {
                        if (visibleItemCount + pastItemsVisible >= totalItemCount) {
                            loading = false
                            Log.d("LOADING", "Last Item !")
                            binding.messagesProgressBar.visibility = View.VISIBLE
                            getNextPage()

                        }
                    }
                }
            }
        })

        binding.openMessagesFilterModal.setOnClickListener {
            val manager = (context as AppCompatActivity).supportFragmentManager
            FilterDialog().show(manager, "CustomManager")
        }
        sharedViewModel.searchingKeyword.observe(viewLifecycleOwner){
            if(sharedViewModel.searchingKeyword.value!!.length < 1){
//                Toast.makeText(requireActivity(), "Empty key", Toast.LENGTH_SHORT).show()
                adapterX.setData(itemList)
                adapterX.notifyDataSetChanged()
            }else{
                val searchResultList = arrayListOf<Message>()
                itemList.forEach {
                    if (it.message.contains(sharedViewModel.searchingKeyword.value!!, ignoreCase = true)){
                        searchResultList.add(it)
                    }
                }
                adapterX.setData(searchResultList)
//                choseOrder()
                setupOrder(searchResultList)
            }

        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getNextPage(){
        lifecycleScope.launch {
            if(listMessagesRelatedToAUser.listMessagesRelatedToAUser(getUserId()!!, page)){
                binding.messagesProgressBar.visibility = View.GONE
                page+=1
                itemList.addAll(listMessagesRelatedToAUser.messageList.value!!)
                adapterX.setData(itemList)
                adapterX.notifyDataSetChanged()
                loading = true
            }
        }
    }

    private fun setupOrder(itemList:ArrayList<Message>) {
        binding.messagesOrder?.adapter = activity?.let { ArrayAdapter(it.applicationContext, R.layout.ordering_item_layout,
            listOf(getString(R.string.orderByName), getString(R.string.orderByStartDate), getString(R.string.orderByCategory)) ) } as SpinnerAdapter

        binding.messagesOrder?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    val temp = itemList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.message })) //TODO: make criteria real
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == getString(R.string.orderByCategory)){
                    val temp = itemList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.message })) //TODO: make criteria real
                    itemList.clear()
                    itemList.addAll(temp)
                }
                if (type == getString(R.string.orderByStartDate)){
                    itemList.sortBy { it.created_at }
                }
                adapterX.notifyDataSetChanged()
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListMessagesRelatedToAUserFactory(this.requireContext(), HelpRequestRepository())
        listMessagesRelatedToAUser = ViewModelProvider(this, factory).get(ListMessagesRelatedToAUser::class.java)
        getData()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
        lifecycleScope.launch {
            val id = getUserId()
            if (id != null) {
                if (listMessagesRelatedToAUser.listMessagesRelatedToAUser(id, page)) {
                    itemList.addAll(listMessagesRelatedToAUser.messageList.value!!)
                    setAdapter(itemList = itemList)
                    setupOrder(itemList)
                    binding.messagesProgressBar.visibility = View.GONE
                }
                else{
                    Toast.makeText(requireActivity(), "You haven't got any request yet.", Toast.LENGTH_SHORT).show()
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