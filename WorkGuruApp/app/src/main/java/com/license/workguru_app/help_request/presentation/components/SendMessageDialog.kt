package com.license.workguru_app.help_request.presentation.components

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.di.SharedViewModel
import kotlinx.coroutines.launch

import com.license.workguru_app.databinding.SendMessageDialogBinding
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.send_message.SendMessageViewModel
import com.license.workguru_app.help_request.domain.use_cases.send_message.SendMessageViewModelFactory


class SendMessageDialog(
): DialogFragment() {
    private var _binding: SendMessageDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var sendMessageViewModel: SendMessageViewModel
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SendMessageDialogBinding.inflate(inflater, container, false)
        binding.sendMessageProgressBar.visibility = View.GONE
        handleThatBackPress()
        initialize()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SendMessageViewModelFactory(requireActivity(), HelpRequestRepository())
        sendMessageViewModel = ViewModelProvider(this, factory).get(SendMessageViewModel::class.java)

    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        binding.messageTextInput.doOnTextChanged { text, start, before, count ->
            binding.sendMessageTextInputLayout.helperText = ""
        }
        binding.messageSenderBtn.setOnClickListener {
            binding.sendMessageProgressBar.visibility = View.VISIBLE
            if (!binding.messageTextInput.text.isNullOrEmpty()){
                lifecycleScope.launch {
                    if(sendMessageViewModel.sendMessage(binding.messageTextInput.text.toString(),
                        sharedViewModel.messageColleagueUserId.value!!)){
                        binding.sendMessageProgressBar.visibility = View.GONE
                        dialog?.dismiss()
                    }
                    else{
                        Toast.makeText(context, getString(R.string.we_couldnt_sen_message), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                binding.sendMessageTextInputLayout.helperText = context!!.getString(R.string.htRequiredField)
                binding.sendMessageTextInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))
            }
        }
        binding.sendMessageTextInputLayout.setEndIconOnClickListener {
            binding.messageTextInput.setText("")
        }
        binding.messageCancelBtn.setOnClickListener {
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

}