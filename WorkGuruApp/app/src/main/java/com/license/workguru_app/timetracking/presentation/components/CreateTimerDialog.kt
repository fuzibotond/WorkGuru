package com.license.workguru_app.timetracking.presentation.components


import android.os.Bundle
import android.util.Log
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
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.license.workguru_app.admin.presentation.components.CreateNewSkillDialog
import com.license.workguru_app.databinding.CreateTimerDialogLayoutBinding
import com.license.workguru_app.help_request.data.remote.DTO.Skill
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModel
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModelFactory
import com.license.workguru_app.timetracking.data.remote.DTO.ShortProject
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModel
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModelFactory


class CreateTimerDialog(
): DialogFragment() {
    private var _binding: CreateTimerDialogLayoutBinding? = null
    private val binding get() = _binding!!

    val chosenProject:MutableLiveData<String> = MutableLiveData()
    var chosenSkills = mutableListOf<Skill>()
    val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var startPauseStopViewModel: StartPauseStopViewModel
    lateinit var listSkillsViewModel: ListSkillsViewModel
    lateinit var listProjectsViewModel: ListProjectsViewModel

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
            if(listProjectsViewModel.listAllProjectsWithoutPagination(false)){
                val projects = listProjectsViewModel.fullList.value
                chooseCategory(projects as ArrayList<ShortProject>)
            }
        }

        val factoryListSkill = ListSkillsViewModelFactory(requireActivity(), HelpRequestRepository())
        listSkillsViewModel = ViewModelProvider(this, factoryListSkill).get(ListSkillsViewModel::class.java)
        lifecycleScope.launch {
            if(listSkillsViewModel.listSkillsViewModel(1)){
                val skills = listSkillsViewModel.skillList.value
                chooseSkill(skills as ArrayList<Skill>)
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
                    if (chosenProject.value != null && !chosenSkills.isEmpty() && binding.descriptionTextInput.text.isNullOrEmpty()){
                        val skillIds = mutableListOf<String>()
                        chosenSkills.forEach {
                            skillIds.add(it.id.toString())
                            Log.d("HELP_REQUEST", "${it}")
                        }
                        val projectId = chosenProject.value!!
                        val description = binding.descriptionTextInput.text.toString()
                        if(startPauseStopViewModel.startTimer(
                                false,
                                project_id = projectId,
                                description = description,
                                skillIds as ArrayList<String>
                            )){

                                sharedViewModel.saveSkills(chosenSkills as ArrayList<Skill>)
                            sharedViewModel.saveCurrentTimer(startPauseStopViewModel.startedTimer.value!!)
                            sharedViewModel.saveTimerState(true)
                        }
                    }else{
                        Toast.makeText(requireActivity(), "Please do not let any field empty!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(context, getString(R.string.tTimerStarted), Toast.LENGTH_SHORT).show()
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
            val manager = (requireActivity()).supportFragmentManager
            CreateProjectDialog().show(manager, "CustomManager")
        }
        if (sharedViewModel.profileRole.value == "admin"){
            binding.createNewSkillBtn.setOnClickListener {
                val manager = (requireActivity()).supportFragmentManager
                CreateNewSkillDialog().show(manager, "CustomManager")
            }
        }
        else{
            binding.createNewSkillBtn.visibility = View.GONE
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
    private fun chooseCategory(categoryList:ArrayList<ShortProject>) {

        val itemList = mutableListOf<String>()
        categoryList.forEach {
            itemList.add(it.project_name+"(${it.category_name})")
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, itemList as List<String>)
        binding.projectSpinner.setAdapter(adapter)
        binding.projectSpinner.setOnItemClickListener { adapterView, view, i, l ->
            categoryList.forEach {
                if (adapter.getItem(i).toString() == it.project_name+"(${it.category_name})"){
                    chosenProject.value = it.project_id.toString()
                }
            }

        }

    }

    private fun chooseSkill(skillList:ArrayList<Skill>) {
        val itemList = mutableListOf<String>()
        skillList.forEach {
            itemList.add(it.name)
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, itemList as List<String>)
        binding.skillSpinner.setAdapter(adapter)

       binding.skillSpinner.setOnItemClickListener { adapterView, view, i, l ->
           skillList.forEach {
               if(it.name == adapter.getItem(i)){
                   chosenSkills.add(it)
                   val chip = Chip(context)
                   chip.text = it.name
                   chip.setChipBackgroundColorResource(R.color.teal_100)
                   chip.setCloseIconResource(R.drawable.ic_baseline_cancel_24);
                   chip.setCloseIconEnabled(true)
                   binding.createTimerLanguagesChipGroup.addView(chip)
                   val temp = it
                   chip.setOnCloseIconClickListener {
                       chosenSkills.remove(temp)
                       binding.createTimerLanguagesChipGroup.removeView(chip)
                   }
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