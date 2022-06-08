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
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.AuthorizedActivity
import com.license.workguru_app.authentification.data.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModel
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModelFactory
import com.license.workguru_app.databinding.FragmentSignInBinding
import com.license.workguru_app.utils.NetworkHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
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
import com.license.workguru_app.di.SessionManager
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.utils.Constants
import com.license.workguru_app.utils.ProfileUtil
import java.text.SimpleDateFormat
import java.util.*


const val RC_SIGN_IN = 100

class SignInFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var googleLoginViewModel: GoogleLoginViewModel
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    val token:MutableLiveData<String> = MutableLiveData<String>()
    val sharedViewModel:SharedViewModel by activityViewModels()

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        handleThatBackPress()
//        clearDate()
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
//            token.value = acct.serverAuthCode

            val am = acct.idToken
            Log.d("GOOGLE-SIGN-IN", "signInResult is successful ${am}")
            Toast.makeText(requireContext(), "Hello ${personName}!", Toast.LENGTH_SHORT).show()
            token.value = acct.idToken
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
            Log.d("GOOGLE-SIGN-IN", "Login with google ${googleLoginViewModel.access_token.value}")
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
                        if (loginViewModel.login(binding.emailAddressInput.text.toString(),
                                binding.emailAddressInput.text.toString()
                            )){
                            binding.signInBtn.isEnabled = false
                        }else{
                            binding.signInBtn.isEnabled = true
                            binding.signInProgressBar.visibility = View.GONE
                        }

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
                    binding.emailAddressLayoutInput.helperText = getString(R.string.htRequiredField)
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
                    binding.passwordInputLayout.helperText = getString(R.string.htNotValidEmail)
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
            if(ProfileUtil.getStringPref(requireActivity(), "USER_EMAIL") == null){
                if (binding.emailAddressInput.text.toString().isNotEmpty()){
                    sharedViewModel.saveFaceEmail(binding.emailAddressInput.text.toString())
                    findNavController().navigate(R.id.action_signInFragment_to_faceRecFragment)
                }else{
                    Toast.makeText(requireActivity(), "Please add your email first!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                sharedViewModel.saveFaceEmail(ProfileUtil.getStringPref(requireActivity(), "USER_EMAIL")!!)
                findNavController().navigate(R.id.action_signInFragment_to_faceRecFragment)
            }


        }

        token.observe(viewLifecycleOwner){
            lifecycleScope.launch {
                if(googleLoginViewModel.googleLogin(token.value!!)){
                    val intent = Intent(context, AuthorizedActivity::class.java).apply {
                        putExtra(AlarmClock.EXTRA_MESSAGE, "You are logged in!${googleLoginViewModel.access_token.value}")
                    }
                    startActivity(intent)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        val expiresAt = sharedPreferences.getString("EXPIRES_AT", null)
        Log.d("Time", "Exp original: ${expiresAt?.substring(0,19)}")
        if (expiresAt != null){
            val localDateTime: LocalDateTime = LocalDateTime.parse(expiresAt.substring(0,19));

            val date = localDateTime.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
            val nowdate = System.currentTimeMillis()

            Log.d("Time", "Exp: ${date} and now: ${nowdate}")
            if (date != null) {
                if (date > nowdate){
                    loginViewModel.access_token.value = savedToken
                }else{
                    sharedPreferences.edit().clear().commit()
                    Toast.makeText(requireContext(), getString(R.string.tSessionExpired), Toast.LENGTH_SHORT).show()
                }
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
            Log.d("GOOGLE-SIGN-IN", "signInResult  is successful: ${account.idToken}")

            token.value = account.idToken

            binding.signInButton.visibility = View.VISIBLE
            binding.signInProgressBar.visibility = View.GONE


        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("GOOGLE-SIGN-IN", "signInResult:failed code=" + e.statusCode)
            binding.signInButton.visibility = View.VISIBLE
        }
    }

    private fun handleThatBackPress(){
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Exit")
                builder.setMessage(getString(R.string.mAreYouSureExit))
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    requireActivity().finish()
                }

                builder.setNegativeButton(getString(R.string.bCancel)) { dialog, which ->

                }


                builder.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return df.parse(date).time
    }

}