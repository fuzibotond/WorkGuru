package com.license.workguru_app.authentification.presentation.components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.license.workguru_app.R
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.register_manually.RegisterViewModel
import com.license.workguru_app.authentification.domain.use_case.register_manually.RegisterViewModelFactory
import com.license.workguru_app.authentification.domain.use_case.register_with_google.GoogleRegisterViewModel
import com.license.workguru_app.authentification.domain.use_case.register_with_google.GoogleRegisterViewModelFactory
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.databinding.FragmentSignUpBinding
import com.license.workguru_app.utils.Constants
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    lateinit var registerViewModel: RegisterViewModel
    lateinit var googleRegisterViewModel: GoogleRegisterViewModel
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
        settingListenersToGoogleAuth()
        return binding.root
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = RegisterViewModelFactory(this.requireContext(), AuthRepository())
        registerViewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)
        val googleFactory = GoogleRegisterViewModelFactory(this.requireContext(), AuthRepository())
        googleRegisterViewModel = ViewModelProvider(this, googleFactory).get(GoogleRegisterViewModel::class.java)

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
    private fun settingListenersToGoogleAuth() {
        // Configure sign-in to request the user's ID, email address,  and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(Constants.CLIENT_ID_GOOGLE)
            .build()
        // Build a GoogleSignInClient with the options specified by gso.

        val mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);
        binding.signUpButton.visibility = View.VISIBLE
        binding.signUpButton.setSize(SignInButton.SIZE_WIDE)
        binding.signUpButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        val acct = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (acct != null) {
            Log.d("GOOGLE-SIGN-IN", "signInResult is successful ${acct.idToken}")
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
            Toast.makeText(requireContext(), "Hello ${personName}!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            googleRegisterViewModel.access_token.value = account.idToken
            // Signed in successfully, show authenticated UI.
            Log.d("GOOGLE-SIGN-IN", "signInResult is successful")
            binding.signUpButton.visibility = View.VISIBLE
            binding.signUpButton.visibility = View.GONE
            googleRegisterViewModel.access_token.observe(viewLifecycleOwner){
                lifecycleScope.launch {
                    googleRegisterViewModel.googleRegister()
                }
            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("GOOGLE-SIGN-IN", "signInResult:failed code=" + e.statusCode)
            binding.signUpButton.visibility = View.VISIBLE
        }
    }
}