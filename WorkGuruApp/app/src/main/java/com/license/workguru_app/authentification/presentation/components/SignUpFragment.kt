package com.example.workguru_app.authentification.presentation.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.workguru_app.R
import com.example.workguru_app.authentification.presentation.SharedViewModel
import com.example.workguru_app.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private var _binding:FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        settingListeners()
        settingStates()
        return binding.root
    }

    private fun settingStates() {
        sharedViewModel.isTermsAndConditionsAccepted.observe(viewLifecycleOwner){
            binding.termsAndCondAcceptChxbx.isChecked = sharedViewModel.isTermsAndConditionsAccepted.value!!
        }
    }

    private fun settingListeners() {
        binding.signUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.termsAndCondAcceptChxbx.setOnClickListener {
            val manager = (context as AppCompatActivity).supportFragmentManager
            TermsAndConditionsDialog().show(manager, "CustomManager")
        }
    }
}