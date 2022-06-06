package com.license.workguru_app.profile.presentation.components

import android.widget.RadioButton
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.license.workguru_app.databinding.ChangeStatusDialogBinding
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.change_status.ChangeStatusViewModel
import com.license.workguru_app.help_request.domain.use_cases.change_status.ChangeStatusViewModelFactory
import com.license.workguru_app.help_request.domain.use_cases.list_statuses.ListStatusesViewModel
import com.license.workguru_app.help_request.domain.use_cases.list_statuses.ListStatusesViewModelFactory
import kotlinx.android.synthetic.main.custom_spinner_item.view.*
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast

import android.widget.RadioGroup
import com.license.workguru_app.help_request.data.remote.DTO.Skill


class ChangeStatusDialog(
): DialogFragment() {
    private var _binding: ChangeStatusDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var listStatusesViewModel: ListStatusesViewModel
    lateinit var changeStatusViewModel: ChangeStatusViewModel
    var status: Skill = Skill(1, "Available")
    var minNumberOfWorkHours: Int = 0

    val sharedViewModel: SharedViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = ChangeStatusDialogBinding.inflate(inflater, container, false)
        handleThatBackPress()
        initialize()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListStatusesViewModelFactory(requireActivity(), HelpRequestRepository())
        listStatusesViewModel = ViewModelProvider(this, factory).get(ListStatusesViewModel::class.java)

        lifecycleScope.launch {
            if(listStatusesViewModel.listStatuses(1)){
                listStatusesViewModel.statusList.value?.forEach {
                    val radioBtn = RadioButton(requireActivity())
                    radioBtn.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    radioBtn.setText(it.name)
                    val temp = it
                    radioBtn.setOnClickListener {
                        status = temp
                    }
                    binding.statusRadioBtn.addView(radioBtn)

                }
                binding.statusProgressBar.visibility = View.GONE
            }
        }
        val factoryStatus = ChangeStatusViewModelFactory(requireActivity(), HelpRequestRepository())
        changeStatusViewModel = ViewModelProvider(this, factoryStatus).get(ChangeStatusViewModel::class.java)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun initialize() {

        binding.saveStatusBtn.setOnClickListener {
            binding.statusProgressBar.visibility = View.VISIBLE
            listStatusesViewModel.statusList.value?.forEach {
                if (it.name == status.name){
                    lifecycleScope.launch {
                        if (changeStatusViewModel.changeStatus(it.id)){
                            sharedViewModel.saveStatus(it)
                            dialog?.dismiss()
                        }
                        else{
                            binding.statusProgressBar.visibility = View.GONE
                            Toast.makeText(requireActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
        binding.createStatusCancelBtn.setOnClickListener {
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


    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}