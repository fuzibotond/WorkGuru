package com.license.workguru_app.timetracking.presentation.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.databinding.FragmentStatmentsBinding
import com.license.workguru_app.timetracking.domain.model.Statement
import com.license.workguru_app.timetracking.presentation.adapters.StatementAdapter

class StatementsFragment : Fragment(), StatementAdapter.OnItemLongClickListener,
    StatementAdapter.OnItemClickListener {
    private var _binding: FragmentStatmentsBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter:StatementAdapter
    val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStatmentsBinding.inflate(inflater, container, false)
        fillData()
        return binding.root
    }

    private fun fillData() {
        val item = Statement("Total number of hours today compared to yesterday", "07:12", "06:20","10.02"+"%")
        val itemList:MutableList<Statement> = mutableListOf()
        itemList.add(item)
        itemList.add(item)
        itemList.add(item)

        adapter = StatementAdapter((itemList as ArrayList<Statement>),this.requireContext(),this, this, sharedViewModel)
        binding.recyclerViewStatement.adapter = adapter
        binding.recyclerViewStatement.layoutManager = LinearLayoutManager(this.context)
//        adapter.notifyDataSetChanged()

    }

    override fun onItemLongClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }


}