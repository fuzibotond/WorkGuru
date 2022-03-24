package com.license.workguru_app.timetracking.presentation.components

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.license.workguru_app.authentification.presentation.SharedViewModel
import com.license.workguru_app.databinding.FragmentProjectListBinding
import com.license.workguru_app.databinding.FragmentSignInBinding
import com.license.workguru_app.databinding.ProjectItemLayoutBinding
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.presentation.adapters.ProjectAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProjectListFragment : Fragment(), ProjectAdapter.OnItemLongClickListener,ProjectAdapter.OnItemClickListener{
    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter:ProjectAdapter
    lateinit var recycler_view: RecyclerView
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        fillData()
        return binding.root

    }

    private fun fillData() {
        val item = Project(id = 43, name = "Tuna", category_name = "Awesome Plastic Chips", start_date = "2022-03-23T20:57:34.000000", tasks = 0, tracked = 0,members = 0)
        val itemList:MutableList<Project> = mutableListOf()
        itemList.add(item)
        itemList.add(item)
        itemList.add(item)

        adapter = ProjectAdapter((itemList as ArrayList<Project>),this.requireContext(),this, this, sharedViewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
//        adapter.notifyDataSetChanged()

    }

    override fun onItemLongClick(position: Int) {
        TODO("Not yet implemented")
    }
    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }


}