package com.license.workguru_app.pomodoro.presentation

import android.content.Context
import android.content.SharedPreferences
import com.license.workguru_app.databinding.PomodoroSettingsDialogBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModel
import com.license.workguru_app.timetracking.domain.use_case.list_projects.ListProjectsViewModelFactory

class PomodoroSettingsDialog(
): DialogFragment() {
    private var _binding: PomodoroSettingsDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var listCategoriesViewModel: ListCategoriesViewModel
    lateinit var listProjectsViewModel:ListProjectsViewModel

    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = PomodoroSettingsDialogBinding.inflate(inflater, container, false)
        handleThatBackPress()
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.pomodoroSaveBtn.setOnClickListener {
            sharedViewModel.savePomodoroState(binding.pomodoroOnOffSwitcher.isChecked)
            sharedViewModel.savePomodoroNotification(binding.notificationSwitcher.isChecked)
            saveUserData(
                requireActivity(),
                binding.numberSessionNumberPicker.value,
                binding.sessionDurationNumberPicker.value,
                binding.pomodoroOnOffSwitcher.isChecked,
                binding.notificationSwitcher.isChecked
            )
            dialog?.dismiss()
        }

        binding.pomodoroCancelBtn.setOnClickListener{
            dialog?.dismiss()
        }

        binding.numberSessionNumberPicker.maxValue = 5
        binding.numberSessionNumberPicker.minValue = 1

        binding.sessionDurationNumberPicker.maxValue = 40
        binding.sessionDurationNumberPicker.minValue = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ListCategoriesViewModelFactory(requireActivity(), TimeTrackingRepository())
        listCategoriesViewModel = ViewModelProvider(this, factory).get(ListCategoriesViewModel::class.java)

        val factoryProjects = ListProjectsViewModelFactory(requireActivity(), TimeTrackingRepository())
        listProjectsViewModel = ViewModelProvider(this, factoryProjects).get(ListProjectsViewModel::class.java)
        lifecycleScope.launch {
            listCategoriesViewModel.listCategories()
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
    private fun saveUserData(context: Context, numberOfSessions:Int, durationOfSession:Int, pomodoroIsOn:Boolean, sendNotifications:Boolean ){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.apply{
            putInt("NUMBER_OF_SESSIONS", numberOfSessions)
            putInt("DURATION_OF_SESSION", durationOfSession)
            putBoolean("POMODORO_IS_ON", pomodoroIsOn)
            putBoolean("SEND_NOTIFICATIONS_BY_POMODORO", sendNotifications)
        }.apply()
        Log.d("POMODORO", "Saved pomodoro states data! Pomodoro is on:${pomodoroIsOn}")
    }

}