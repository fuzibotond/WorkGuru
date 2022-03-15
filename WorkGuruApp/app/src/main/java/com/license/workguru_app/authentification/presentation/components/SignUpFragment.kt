package com.license.workguru_app.authentification.presentation.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.authentification.data.remote.DTO.RegisterRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.register_manually.RegisterViewModel
import com.license.workguru_app.authentification.domain.use_case.register_manually.RegisterViewModelFactory
import com.license.workguru_app.authentification.domain.use_case.reset_your_password.ForgotPasswordViewModel
import com.license.workguru_app.authentification.domain.use_case.reset_your_password.ForgotPasswordViewModelFactory
import com.license.workguru_app.authentification.presentation.SharedViewModel
import com.license.workguru_app.databinding.FragmentSignUpBinding
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    lateinit var registerViewModel: RegisterViewModel
    private var _binding:FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: SharedViewModel by activityViewModels()
    val waiting:MutableLiveData<Boolean> = MutableLiveData()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.signUpProgressBar.visibility = View.GONE
        settingListeners()
        settingStates()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = RegisterViewModelFactory(this.requireContext(), AuthRepository())
        registerViewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

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
        binding.signUpBtn.setOnClickListener {
            binding.signUpProgressBar.visibility = View.VISIBLE
            if (binding.termsAndCondAcceptChxbx.isChecked &&
                    binding.emailAddressSignUpInput.text.toString().isNotEmpty() &&
                    binding.fullNameSignUpInput.text.toString().isNotEmpty() &&
                    binding.passwordSignUpInput.text.toString().isNotEmpty()){
                binding.signUpProgressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    waiting.value = !registerViewModel.signUp(
                        binding.emailAddressSignUpInput.text.toString(),
                        binding.fullNameSignUpInput.text.toString(),
                        binding.passwordSignUpInput.text.toString())
                    binding.signUpBtn.isEnabled = waiting.value == true
                }
            }
        }
        waiting.observe(viewLifecycleOwner){
            binding.signUpProgressBar.visibility = View.GONE
        }

    }
}