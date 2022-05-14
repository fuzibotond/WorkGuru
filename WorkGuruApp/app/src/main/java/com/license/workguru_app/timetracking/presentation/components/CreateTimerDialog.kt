package com.license.workguru_app.timetracking.presentation.components


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
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.appcompat.app.AppCompatActivity
import com.license.workguru_app.databinding.CreateTimerDialogLayoutBinding
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModel
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModelFactory


class CreateTimerDialog(
): DialogFragment() {
    private var _binding: CreateTimerDialogLayoutBinding? = null
    private val binding get() = _binding!!
    lateinit var listProjectsViewModel: ListProjectsViewModel
    val choosenProject:MutableLiveData<Project> = MutableLiveData()
    val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var startPauseStopViewModel: StartPauseStopViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = CreateTimerDialogLayoutBinding.inflate(inflater, container, false)
        handleThatBackPress()
        initialize()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListProjectsViewModelFactory(requireActivity(), TimeTrackingRepository())
        listProjectsViewModel = ViewModelProvider(this, factory).get(ListProjectsViewModel::class.java)
        lifecycleScope.launch {
            if(listProjectsViewModel.listAllProjects(false, "0")){
                val projects = listProjectsViewModel.dataList.value
                choseCategory(projects as ArrayList<Project>)
            }
        }

        val timerFactory = StartPauseStopViewModelFactory(requireActivity(), TimeTrackingRepository())
        startPauseStopViewModel = ViewModelProvider(this,timerFactory ).get(StartPauseStopViewModel::class.java)

    }

    private fun initialize() {
        binding.createTimerCancelBtn.setOnClickListener {
            dialog?.dismiss()
        }
        binding.startTrackingBtn.setOnClickListener {
            if (sharedViewModel.isTimerStarted.value == false){
                lifecycleScope.launch {
                    if (choosenProject.value != null){
                        val projectId = choosenProject.value?.id.toString()
                        val description = binding.descriptionTextInput.text.toString()
                        if(startPauseStopViewModel.startTimer(false, project_id = projectId, description = description)){
                            sharedViewModel.saveTimerState(true)
                        }
                    }

                }
            }
            else{
                Toast.makeText(context, "A timer is started on project ${sharedViewModel.currentProject.value!!.project_name} at ${sharedViewModel.currentProject.value!!.started_at}!", Toast.LENGTH_SHORT).show()
            }
        }
        startPauseStopViewModel.startedTimer.observe(viewLifecycleOwner){
            startPauseStopViewModel.startedTimer.value?.let { it1 ->
                sharedViewModel.saveCurrentTimer(
                    it1
                )
            }
        }
        binding.createNewProjectBtn.setOnClickListener {
            val manager = (this as AppCompatActivity).supportFragmentManager
            CreateProjectDialog().show(manager, "CustomManager")
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
    private fun choseCategory(categoryList:ArrayList<Project>) {

        val itemList = mutableListOf<String>()
        categoryList.forEach {
            itemList.add(it.name+"(${it.category_name})")
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, itemList as List<String>)
        binding.projectSpinner.setAdapter(adapter)

        binding.projectSpinner.setOnItemClickListener { adapterView, view, i, l ->

            val selectedItem = adapterView.adapter.getItem(i)
            categoryList.forEach {
                if (it.name+"(${it.category_name})" == selectedItem){
                    choosenProject.value = it
                }
            }

        }

    }


    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}