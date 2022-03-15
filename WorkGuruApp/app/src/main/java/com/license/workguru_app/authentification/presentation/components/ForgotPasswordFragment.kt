package com.license.workguru_app.authentification.presentation.components

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.reset_your_password.ForgotPasswordViewModel
import com.license.workguru_app.authentification.domain.use_case.reset_your_password.ForgotPasswordViewModelFactory
import com.license.workguru_app.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.launch


class ForgotPasswordFragment : Fragment() {
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        settingListeners()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ForgotPasswordViewModelFactory(this.requireContext(), AuthRepository())
        forgotPasswordViewModel = ViewModelProvider(this, factory).get(ForgotPasswordViewModel::class.java)
    }

    private fun settingListeners() {
        binding.signInLinkResetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_signInFragment)
        }
        var isCorrectEmail = true
        binding.emailAddressResetPasswordInput.doOnTextChanged { text, start, before, count ->
            isCorrectEmail = true
            val emailTocheck = (text!!.reversed()).takeWhile { it=='.' }
            if (Patterns.EMAIL_ADDRESS.matcher(emailTocheck.toString()).matches() ){
                Log.d("email", emailTocheck.toString());
                binding.emailAddressResetPasswordLayoutInput.helperText = "Not an email type!"
                binding.emailAddressResetPasswordLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                binding.emailAddressResetPasswordLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                isCorrectEmail = false
            }
            if (text!!.toString().isEmpty()){
                binding.emailAddressResetPasswordLayoutInput.helperText = "This field is required!"
                binding.emailAddressResetPasswordLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                binding.emailAddressResetPasswordLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                binding.emailAddressResetPasswordLayoutInput.setEndIconTintList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                binding.emailAddressResetPasswordLayoutInput.setEndIconDrawable(R.drawable.ic_baseline_error_24)
            }
            else{
                binding.emailAddressResetPasswordLayoutInput.helperText = ""
                binding.emailAddressResetPasswordLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke)
                binding.emailAddressResetPasswordLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke))
                binding.emailAddressResetPasswordLayoutInput.setEndIconTintList(resources.getColorStateList(R.color.text_input_box_stroke))
                binding.emailAddressResetPasswordLayoutInput.setEndIconDrawable(R.drawable.ic_baseline_fact_check_24)
            }
        }
        binding.continueResetPasswordBtn.setOnClickListener {
                if (binding.emailAddressResetPasswordInput.text.toString().isNotEmpty() && isCorrectEmail){
                    lifecycleScope.launch {
                        binding.continueResetPasswordBtn.isEnabled = !forgotPasswordViewModel.forgotPassword(binding.emailAddressResetPasswordInput.text.toString())
                    }
                    binding.emailAddressResetPasswordLayoutInput.helperText = "One more chance left... just joking :D\nIf you didn't get any email from us check it twice or try again this flow."
                    binding.emailAddressResetPasswordLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke)
                    binding.emailAddressResetPasswordLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke))
                }
                else
                {
                    binding.emailAddressResetPasswordLayoutInput.helperText = "This field is required!"
                    binding.emailAddressResetPasswordLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                    binding.emailAddressResetPasswordLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                    binding.emailAddressResetPasswordLayoutInput.setEndIconTintList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                    binding.emailAddressResetPasswordLayoutInput.setEndIconDrawable(R.drawable.ic_baseline_error_24)
                }
            }


        }
    }
