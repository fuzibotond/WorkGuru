package com.license.workguru_app.authentification.presentation.components

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.AuthorizedActivity
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModel
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModelFactory
import com.license.workguru_app.databinding.FragmentSignInBinding
import com.license.workguru_app.utils.NetworkHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import com.google.android.gms.common.SignInButton
import com.license.workguru_app.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.license.workguru_app.authentification.domain.use_case.log_in_with_google.GoogleLoginViewModel
import com.license.workguru_app.authentification.domain.use_case.log_in_with_google.GoogleLoginViewModelFactory
import com.license.workguru_app.utils.Constants


const val RC_SIGN_IN = 100

class SignInFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var googleLoginViewModel: GoogleLoginViewModel
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    val token:MutableLiveData<String> = MutableLiveData<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        clearDate()
        settingListeners()
        settingListenersToGoogleAuth()
        initialize()
        return binding.root
    }

    private fun settingListenersToGoogleAuth() {
        binding.signInProgressBar.visibility = View.VISIBLE
        // Configure sign-in to request the user's ID, email address,  and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(Constants.CLIENT_ID_GOOGLE)
            .build()
        // Build a GoogleSignInClient with the options specified by gso.

        val mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);
        binding.signInButton.visibility = View.VISIBLE
        binding.signInButton.setSize(SignInButton.SIZE_WIDE)
        binding.signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        val acct = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
            token.value = acct.idToken
            Log.d("GOOGLE-SIGN-IN", "signInResult is successful ${acct.idToken}")
            Toast.makeText(requireContext(), "Hello ${personName}!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialize() {
        binding.signInProgressBar.visibility = View.GONE
        loginViewModel.access_token.observe(viewLifecycleOwner){
            Log.d("AUTH", "Login with email")
            val intent = Intent(context, AuthorizedActivity::class.java).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "You are logged in!${loginViewModel.access_token.value}")
            }
            startActivity(intent)
        }
        googleLoginViewModel.access_token.observe(viewLifecycleOwner){
            Log.d("AUTH", "Login with google ${googleLoginViewModel.access_token.value}")
            val intent = Intent(context, AuthorizedActivity::class.java).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "You are logged in!${googleLoginViewModel.access_token.value}")
            }
            startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = LoginViewModelFactory(this.requireContext(), AuthRepository())
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        val googleFactory = GoogleLoginViewModelFactory(this.requireContext(), AuthRepository())
        googleLoginViewModel = ViewModelProvider(this, googleFactory).get(GoogleLoginViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun settingListeners() {
        var buttonClickCounter = 0
        binding.signInBtn.setOnClickListener {
            binding.signInProgressBar.visibility = View.VISIBLE
            buttonClickCounter++
            loginViewModel.email.value = binding.emailAddressInput.text.toString()
            loginViewModel.password.value = binding.passwordInput.text.toString()
            loginViewModel.remember_me.value = binding.rememberMeChxbx.isChecked

            if (NetworkHelper.isNetworkConnected(this.requireActivity())){
                if (binding.emailAddressInput.text.toString().isNotEmpty() && binding.emailAddressInput.text.toString().isNotEmpty()){
                    lifecycleScope.launch {
                        binding.signInBtn.isEnabled = !loginViewModel.login()
                    }
                }else{
                    binding.emailAddressLayoutInput.helperText = "This field is required!"
                    binding.emailAddressLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                    binding.emailAddressLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                    binding.emailAddressLayoutInput.setEndIconDrawable(R.drawable.ic_baseline_error_outline_24)
                    binding.emailAddressLayoutInput.setEndIconTintList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                    binding.passwordInputLayout.helperText = "This field is required"
                    binding.passwordInputLayout.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                    binding.passwordInputLayout.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                    binding.signInProgressBar.visibility = View.GONE
                }
            }else{
                lifecycleScope.launch {
                    delay(1000)
                    Toast.makeText(requireActivity(), "Check your connection!", Toast.LENGTH_SHORT).show()
                    binding.signInProgressBar.visibility = View.GONE
                }
            }
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.forgotYourPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }

        binding.emailAddressInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (binding.emailAddressInput.text!!.toString().isEmpty()){
                    binding.emailAddressLayoutInput.helperText = "This field is required!"
                    binding.emailAddressLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                    binding.emailAddressLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                    binding.emailAddressLayoutInput.setEndIconDrawable(R.drawable.ic_baseline_error_outline_24)
                    binding.emailAddressLayoutInput.setEndIconTintList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                }else {
                    binding.emailAddressLayoutInput.helperText = ""
                    binding.emailAddressLayoutInput.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke)
                    binding.emailAddressLayoutInput.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke))
                    binding.emailAddressLayoutInput.setEndIconDrawable(R.drawable.ic_baseline_check_circle_outline_24)
                }
            }
        }
        binding.passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (binding.passwordInput.text.toString().isEmpty()){
                    binding.passwordInputLayout.helperText = "This field is required"
                    binding.passwordInputLayout.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke_error)
                    binding.passwordInputLayout.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke_error))
                }else {
                    binding.passwordInputLayout.helperText = ""
                    binding.passwordInputLayout.hintTextColor = context?.resources?.getColorStateList(R.color.text_input_box_stroke)
                    binding.passwordInputLayout.setBoxStrokeColorStateList(resources.getColorStateList(R.color.text_input_box_stroke))
                }
            }
        }
        binding.signInWithFaceBtn.setOnClickListener {
           findNavController().navigate(R.id.action_signInFragment_to_faceRecFragment)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        val expiresAt = sharedPreferences.getString("EXPIRES_AT", null)
        Log.d("Time", "Exp original: ${expiresAt?.substring(0,19)}")
        if (expiresAt != null){
            val localDateTime:LocalDateTime  = LocalDateTime.parse(expiresAt.substring(0,19));
            val  formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            val date = localDateTime.atZone(ZoneOffset.UTC).toEpochSecond()
            val nowdate = LocalDateTime.now().atZone(ZoneOffset.UTC).toEpochSecond()
            Log.d("Time", "Exp: ${date} and now: ${nowdate}")
            if (date > nowdate){
                loginViewModel.access_token.value = savedToken
            }else{
                sharedPreferences.edit().clear().commit()
                Toast.makeText(requireContext(), "Your session has been expired! You need to log in again...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun clearDate() {
        val sharedPreferences:SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()

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
            // Signed in successfully, show authenticated UI.
            Log.d("GOOGLE-SIGN-IN", "signInResult is successful")
            binding.signInButton.visibility = View.VISIBLE
            binding.signInProgressBar.visibility = View.GONE
            token.observe(viewLifecycleOwner){
                lifecycleScope.launch {
                    googleLoginViewModel.googleLogin(token.value!!)
                }
            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("GOOGLE-SIGN-IN", "signInResult:failed code=" + e.statusCode)
            binding.signInButton.visibility = View.VISIBLE
        }
    }
}