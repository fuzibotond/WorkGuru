package com.license.workguru_app.timetracking.presentation.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModelFactory
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

import androidx.appcompat.app.AppCompatActivity
import com.license.workguru_app.databinding.CreateProjectDialogBinding
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.domain.use_case.create_new_project.NewProjectViewModel
import com.license.workguru_app.timetracking.domain.use_case.create_new_project.NewProjectViewModelFactory
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory


class CreateProjectDialog(
): DialogFragment() {
    private var _binding: CreateProjectDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var listCategoriesViewModel: ListCategoriesViewModel
    lateinit var newProjectViewModel: NewProjectViewModel
    lateinit var listProjectsViewModel: ListProjectsViewModel
    var choosenCategory:MutableLiveData<Category> = MutableLiveData()
    val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CreateProjectDialogBinding.inflate(inflater, container, false)
        binding.newProjectProgressBar.visibility = View.GONE
        handleThatBackPress()
        initialize()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListCategoriesViewModelFactory(requireActivity(), TimeTrackingRepository())
        listCategoriesViewModel = ViewModelProvider(this, factory).get(ListCategoriesViewModel::class.java)

        val factoryProject = NewProjectViewModelFactory(requireActivity(), TimeTrackingRepository())
        newProjectViewModel = ViewModelProvider(this, factoryProject).get(NewProjectViewModel::class.java)

        val factoryList = ListProjectsViewModelFactory(this.requireContext(), TimeTrackingRepository())
        listProjectsViewModel = ViewModelProvider(this, factoryList).get(ListProjectsViewModel::class.java)

        lifecycleScope.launch {
            listCategoriesViewModel.listCategories()
        }
        lifecycleScope.launch {
            listProjectsViewModel.listAllProjects(false, "0")
        }


    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        listCategoriesViewModel.dataList.observe(viewLifecycleOwner){
            val categories = listCategoriesViewModel.dataList.value
            choseCategory(categories as ArrayList<Category>)
        }
        binding.createNewCategoryBtn.setOnClickListener {
            val manager = (requireActivity() as AppCompatActivity).supportFragmentManager
            CreateCategoryDialog().show(manager, "CustomManager")
        }
        binding.saveNewProjectBtn.setOnClickListener {
            binding.newProjectProgressBar.visibility = View.VISIBLE
            if (!avoidDuplicates( listProjectsViewModel.dataList.value as ArrayList<Project>)){
                lifecycleScope.launch {
                    if(newProjectViewModel.createNewProject(binding.newCategorySpinner.text.toString(), binding.newProjectNameTextInput.text.toString())){
                        Toast.makeText(requireActivity(), getString(R.string.tNewProjectCreated), Toast.LENGTH_SHORT).show()
                        binding.newProjectProgressBar.visibility = View.GONE

                    }
                }
            }
            else{
                Toast.makeText(requireActivity(), getString(R.string.tProjectAlreadyExist), Toast.LENGTH_SHORT).show()
                binding.newProjectProgressBar.visibility = View.GONE
            }
        }
        binding.createTimerCancelBtn.setOnClickListener {
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
    private fun choseCategory(categoryList:ArrayList<Category>) {

        val itemList = mutableListOf<String>()
        categoryList.forEach {
            itemList.add(it.category_name)
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, itemList as List<String>)
        binding.newCategorySpinner.setAdapter(adapter)

        binding.newCategorySpinner.setOnItemClickListener { adapterView, view, i, l ->

            val selectedItem = adapterView.adapter.getItem(i)
            categoryList.forEach {
                if (it.category_name == selectedItem){
                    choosenCategory.value = it
                }
            }
        }
    }
    private fun avoidDuplicates(projectList:ArrayList<Project>):Boolean {
        val projectName = binding.newProjectNameTextInput.text
        projectList.forEach {
            if(it.name.equals(projectName) ){

                return true
            }
        }
        return false
    }
}