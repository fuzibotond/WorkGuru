package com.license.workguru_app.timetracking.presentation.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModelFactory
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

import com.license.workguru_app.databinding.CreateCategoryDialogBinding
import com.license.workguru_app.timetracking.domain.use_case.create_new_category.NewCategoryViewModel
import com.license.workguru_app.timetracking.domain.use_case.create_new_category.NewCategoryViewModelFactory


class CreateCategoryDialog(
): DialogFragment() {
    private var _binding: CreateCategoryDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var listCategoriesViewModel: ListCategoriesViewModel
    lateinit var newCategoryViewModel: NewCategoryViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CreateCategoryDialogBinding.inflate(inflater, container, false)
        binding.categoryProgressBar.visibility = View.GONE
        handleThatBackPress()
        initialize()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListCategoriesViewModelFactory(requireActivity(), TimeTrackingRepository())
        listCategoriesViewModel = ViewModelProvider(this, factory).get(ListCategoriesViewModel::class.java)

        val factoryCategory = NewCategoryViewModelFactory(requireActivity(), TimeTrackingRepository())
        newCategoryViewModel = ViewModelProvider(this, factoryCategory).get(NewCategoryViewModel::class.java)
        
        lifecycleScope.launch {
            listCategoriesViewModel.listCategories()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        binding.saveCategoryBtn.setOnClickListener {
            binding.categoryProgressBar.visibility = View.VISIBLE

            if (!avoidDuplicates(listCategoriesViewModel.dataList.value as ArrayList<Category>)){
                lifecycleScope.launch {
                    if(newCategoryViewModel.createNewCategory(binding.categoryNameTextInput.text.toString())){
                        Toast.makeText(requireActivity(), getString(R.string.tNewCategoryCreated), Toast.LENGTH_SHORT).show()
                        binding.categoryProgressBar.visibility = View.GONE
                    }
                }
            }
            else{
                Toast.makeText(requireActivity(), getString(R.string.tCategoryAlreadyExist), Toast.LENGTH_SHORT).show()
                binding.categoryProgressBar.visibility = View.GONE
            }
        }
        binding.createCategoryCancelBtn.setOnClickListener {
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
    
    private fun avoidDuplicates(categoryList:ArrayList<Category>):Boolean {
        val categoryName = binding.categoryNameTextInput.text
        categoryList.forEach {
            if(it.category_name.equals(categoryName)){
                return true
            }
        }
        return false
    }
}