package com.license.workguru_app.admin.presentation.components

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import com.license.workguru_app.admin.domain.repository.AdminRepository
import com.license.workguru_app.admin.domain.use_cases.invite_a_new_user.InviteUserViewModel
import com.license.workguru_app.admin.domain.use_cases.invite_a_new_user.InviteUserViewModelFactory
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModel
import com.license.workguru_app.databinding.FragmentInviteUserBinding
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class InviteUserFragment : Fragment() {
    private var _binding: FragmentInviteUserBinding? = null
    private val binding get() = _binding!!
    lateinit var inviteUserViewModel: InviteUserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInviteUserBinding.inflate(inflater, container, false)
        initialize()
        setListener()
        return binding.root
    }

    private fun setListener() {
        binding.continueInviteUserBtn.setOnClickListener {
            binding.inviteUserProgressBar.visibility = View.VISIBLE
            if (binding.emailAddressInviteUserInput.text.toString().isEmpty()){
                binding.emailAddressInviteUserLayoutInput.helperText = "This field is required!"
                binding.emailAddressInviteUserLayoutInput.setHelperTextColor( ColorStateList.valueOf(Color.RED))
                binding.inviteUserProgressBar.visibility = View.GONE
            }else if (!Patterns.EMAIL_ADDRESS.matcher( binding.emailAddressInviteUserInput.text).matches()){
                binding.emailAddressInviteUserLayoutInput.helperText = "Not a valid email!"
                binding.emailAddressInviteUserLayoutInput.setHelperTextColor( ColorStateList.valueOf(Color.RED))
                binding.inviteUserProgressBar.visibility = View.GONE
            }else{
                lifecycleScope.launch {
                    if (inviteUserViewModel.inviteUser(binding.emailAddressInviteUserInput.text.toString())){

                        findNavController().navigate(R.id.dashboardFragment)
                    }
                    binding.inviteUserProgressBar.visibility = View.GONE
                }

            }

        }
        binding.dashboardInviteUser.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }
        binding.emailAddressInviteUserInput.doOnTextChanged { text, start, before, count ->
            binding.emailAddressInviteUserLayoutInput.helperText = ""
        }
    }

    private fun initialize() {
        binding.inviteUserProgressBar.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = InviteUserViewModelFactory(requireActivity(), AdminRepository())
        inviteUserViewModel = ViewModelProvider(this, factory).get(InviteUserViewModel::class.java)

    }
}