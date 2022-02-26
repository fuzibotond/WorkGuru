package com.example.workguru_app.authentification.presentation.components

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
import androidx.navigation.fragment.findNavController
import com.example.workguru_app.R
import com.example.workguru_app.authentification.presentation.SharedViewModel
import com.example.workguru_app.databinding.TermsAndConditionsModelBinding
import kotlinx.coroutines.GlobalScope

class TermsAndConditionsDialog(
): DialogFragment() {
    private var _binding: TermsAndConditionsModelBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.terms_and_conditions_icon_foreground);
        _binding = TermsAndConditionsModelBinding.inflate(inflater, container, false)
        handleThatBackPress()
        initialize()
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    private fun initialize() {
        binding.termsAndCondAcceptBtn.setOnClickListener {

            Toast.makeText(context,"You just accepted the Terms and Conditions" , Toast.LENGTH_SHORT).show()
            sharedViewModel.acceptTermsAndConditions(true)
            dialog?.dismiss()
        }
        binding.termsAndCondCancelBtn.setOnClickListener {
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
                findNavController().navigate(R.id.signUpFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}