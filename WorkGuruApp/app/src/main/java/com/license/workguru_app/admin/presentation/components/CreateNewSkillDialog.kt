package com.license.workguru_app.admin.presentation.components

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
import com.license.workguru_app.admin.data.repository.AdminRepository
import com.license.workguru_app.admin.domain.use_cases.create_new_skill.CreateNewSkillViewModel
import com.license.workguru_app.admin.domain.use_cases.create_new_skill.CreateNewSkillViewModelFactory
import com.license.workguru_app.di.SharedViewModel
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import com.license.workguru_app.databinding.CreateNewSkillDialogBinding
import com.license.workguru_app.help_request.data.remote.DTO.Skill
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModel
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModelFactory


class CreateNewSkillDialog(
): DialogFragment() {
    private var _binding: CreateNewSkillDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var createNewSkillViewModel: CreateNewSkillViewModel
    lateinit var listSkillsViewModel: ListSkillsViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CreateNewSkillDialogBinding.inflate(inflater, container, false)
        binding.newSkillProgressBar.visibility = View.GONE
        handleThatBackPress()
        initialize()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListSkillsViewModelFactory(requireActivity(), HelpRequestRepository())
        listSkillsViewModel = ViewModelProvider(this, factory).get(ListSkillsViewModel::class.java)

        val factorySkill = CreateNewSkillViewModelFactory(requireActivity(), AdminRepository())
        createNewSkillViewModel = ViewModelProvider(this, factorySkill).get(CreateNewSkillViewModel::class.java)

        lifecycleScope.launch {
            listSkillsViewModel.listSkillsViewModel(1)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        binding.saveSkillBtn.setOnClickListener {
            binding.newSkillProgressBar.visibility = View.VISIBLE

            if (!avoidDuplicates(listSkillsViewModel.skillList.value as ArrayList<Skill>)){
                lifecycleScope.launch {
                    if(createNewSkillViewModel.createNewSkill(binding.newSkillNameTextInput.text.toString())){
                        Toast.makeText(requireActivity(), getString(R.string.new_skill_created), Toast.LENGTH_SHORT).show()
                        binding.newSkillProgressBar.visibility = View.GONE
                        dialog?.dismiss()
                    }
                }
            }
            else{
                Toast.makeText(requireActivity(), getString(R.string.skill_already_exist), Toast.LENGTH_SHORT).show()
                binding.newSkillProgressBar.visibility = View.GONE
            }
        }
        binding.createSkillCancelBtn.setOnClickListener {
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

    private fun avoidDuplicates(skillList:ArrayList<Skill>):Boolean {
        val skillName = binding.newSkillNameTextInput.text
        skillList.forEach {
            if(it.name.equals(skillName)){
                return true
            }
        }
        return false
    }
}