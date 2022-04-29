package com.license.workguru_app.profile.presentation.components

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.license.workguru_app.databinding.FragmentProfileBinding
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.license.workguru_app.R
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.license.workguru_app.profile.data.remote.DTO.States
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.change_user_profile_data.ChangeProfileDataViewModel
import com.license.workguru_app.profile.domain.use_case.change_user_profile_data.ChangeProfileDataViewModelFactory
import com.license.workguru_app.profile.domain.use_case.display_cities.ListCitiesViewModel
import com.license.workguru_app.profile.domain.use_case.display_cities.ListCitiesViewModelFactory
import com.license.workguru_app.profile.domain.use_case.display_countries.ListCountriesViewModel
import com.license.workguru_app.profile.domain.use_case.display_countries.ListCountriesViewModelFactory
import com.license.workguru_app.profile.domain.use_case.display_states.ListStatesViewModel
import com.license.workguru_app.profile.domain.use_case.display_states.ListStatesViewModelFactory
import com.license.workguru_app.profile.domain.use_case.display_user_profile.UserProfileViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_profile.UserProfileViewModelFactory
import com.license.workguru_app.profile.presentation.adapetrs.StateAdapter
import com.license.workguru_app.utils.Constants
import kotlinx.android.synthetic.main.activity_authorized.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    val REQUEST_CODE = 100

    val countries:MutableLiveData<List<String>> = MutableLiveData()
    val statesWithId:MutableLiveData<List<States>> = MutableLiveData()
    val cities:MutableLiveData<List<String>> = MutableLiveData()

    val uploadedImage:MutableLiveData<Uri> = MutableLiveData()
    val filePath:MutableLiveData<String> = MutableLiveData("")
    val bitmap:MutableLiveData<Bitmap> = MutableLiveData()

    lateinit var listCitiesViewModel: ListCitiesViewModel
    lateinit var listStatesViewModel: ListStatesViewModel
    lateinit var listCountriesViewModel: ListCountriesViewModel
    lateinit var getUserProfileViewModel: UserProfileViewModel
    lateinit var changeProfileDataViewModel: ChangeProfileDataViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        settingListeners()
        initializeData()
        handleThatBackPress()
        verifyInputData()
        return binding.root
    }

    private fun verifyInputData():Boolean {
        return true
    }

    private fun initializeData() {
        setAdapters()
        setProfileData()
    }

    private fun setProfileData() {

        getUserProfileViewModel.data.observe(viewLifecycleOwner){
            val user = getUserProfileViewModel.data.value!!
            binding.emailAddressAndUsername.setText(user.email)
            binding.fullNameInput.setText(user.name)
            if (user.phone_number != null) {
                binding.phoneNumberInputProfile.setText(user.phone_number)
            }
            if (user.avatar != null) {
                val imgUri = Uri.parse(Constants.VUE_APP_USER_AVATAR_URL+user.avatar)
                Glide.with(this)
                    .load(imgUri)
                    .centerCrop()
                    .into(binding.profileImage);
            }
            else{
                val acct = GoogleSignIn.getLastSignedInAccount(requireActivity())
                if (acct != null) {
                    val personPhoto:Uri? = acct.photoUrl
                    Glide.with(this)
                        .load(personPhoto)
                        .centerCrop()
                        .into(binding.profileImage);
                }
            }
            if (user.country != null) {
                binding.countrySpinnerProfile.setText(user.country)
            }
            if (user.street_address != null) {
                binding.streetAddressInputProfile.setText(user.street_address)
            }
            if (user.city != null) {
                binding.citySpinnerProfile.setText(user.city)
            }
            if (user.state != null) {
                binding.stateSpinnerProfile.setText(user.state)
            }
            if (user.zip != null) {
            binding.zipInputProfile.setText(user.zip)
            }
            uploadedImage.value = Uri.parse(Constants.VUE_APP_USER_AVATAR_URL + user.avatar)
            //TODO: "active_timer": null,"id": 54,"role": "admin" - never used
        }
    }
    @BindingAdapter("imageUrl")
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri =
                imgUrl.toUri().buildUpon().scheme("https").build()
            imgView.setImageURI(imgUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val countryFactory = ListCountriesViewModelFactory(requireActivity(), ProfileRepository())
        listCountriesViewModel = ViewModelProvider(this, countryFactory).get(ListCountriesViewModel::class.java)
        lifecycleScope.launch {
            listCountriesViewModel.listCountries()
        }
        val stateFactory = ListStatesViewModelFactory(requireActivity(), ProfileRepository())
        listStatesViewModel = ViewModelProvider(this, stateFactory).get(ListStatesViewModel::class.java)

        val cityFactory = ListCitiesViewModelFactory(requireActivity(), ProfileRepository())
        listCitiesViewModel = ViewModelProvider(this, cityFactory).get(ListCitiesViewModel::class.java)

        val userProfileFactory = UserProfileViewModelFactory(requireActivity(), ProfileRepository())
        getUserProfileViewModel = ViewModelProvider(this, userProfileFactory).get(UserProfileViewModel::class.java)

        lifecycleScope.launch {
            getUserProfileViewModel.getUserProfileInfo()
        }

        val changeProfileDataViewModelFactory = ChangeProfileDataViewModelFactory(requireActivity(), ProfileRepository())
        changeProfileDataViewModel = ViewModelProvider(this, changeProfileDataViewModelFactory).get(ChangeProfileDataViewModel::class.java)


    }

    private fun setAdapters() {
        countries.observe(viewLifecycleOwner){
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, countries.value as List<String>)
            binding.countrySpinnerProfile.setAdapter(adapter)
        }

        listCitiesViewModel.cities.observe(viewLifecycleOwner){
            val items = mutableListOf<String>()
            listCitiesViewModel.cities.value?.forEach { items.add(it.name) }
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_list_item, items as List<String>)
            binding.citySpinnerProfile.setAdapter(adapter)
        }

        listCountriesViewModel.countries.observe(viewLifecycleOwner){
            countries.value = listCountriesViewModel.countries.value?.map { it -> it.country_name }
        }
        listStatesViewModel.states.observe(viewLifecycleOwner){
            val items = mutableListOf<States>()
            listStatesViewModel.states.value?.forEach { items.add(it) }
            Log.d("STATES", "Profile ${items}")
            val customDropDownAdapter = StateAdapter(requireActivity(), R.layout.custom_list_item, items)
            binding.stateSpinnerProfile.setAdapter(customDropDownAdapter)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun settingListeners() {
        binding.photoChangeBtn.setOnClickListener {
            openGalleryForImage()
        }
        binding.photoRemove.setOnClickListener {
            removeUploadedPhoto()
        }
        binding.profileSaveBtn.setOnClickListener { 
            if (verifyInputData()){
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Save")
                builder.setMessage("Are you sure you want save changes?")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    val path:String
                    if (filePath.value != ""){
                        path = filePath.value.toString()
                    }else{
                        path = uploadedImage.value.toString()
                    }
                    lifecycleScope.launch {

                            changeProfileDataViewModel.changeData(
                                binding.citySpinnerProfile.text.toString(),
                                binding.streetAddressInputProfile.text.toString(),
                                binding.countrySpinnerProfile.text.toString(),
                                path,
                                "PUT",
                                binding.stateSpinnerProfile.text.toString()
                                )
                        }

//                    uploadImage()

                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()


            }else{
                Toast.makeText(requireActivity(), "Please correct the specific data!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.countrySpinnerProfile.setOnItemClickListener { adapterView, view, i, l ->

            lifecycleScope.launch {
                listStatesViewModel.listStates(adapterView.adapter.getItem(i) as String)
            }
        }
        binding.stateSpinnerProfile.setOnItemClickListener { adapterView, view, i, l ->
            binding.stateSpinnerProfile.setText((adapterView.adapter.getItem(i) as States).name )
            lifecycleScope.launch {
                (adapterView.adapter.getItem(i) as States).id?.let {
                    listCitiesViewModel.listCities(
                        it
                    )
                }
            }
        }


    }
    private fun uploadImage(){
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.value?.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val imageInByte = byteArrayOutputStream.toByteArray()

        val encodeImage = Base64.encodeToString(imageInByte, Base64.DEFAULT)

        Toast.makeText(requireActivity(), encodeImage, Toast.LENGTH_SHORT).show()

    }

    private fun removeUploadedPhoto() {
        binding.profileImage.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_person_24))
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            uploadedImage.value = data!!.data
            binding.profileImage.setImageURI(data?.data) // handle chosen image
            filePath.value = data.data.toString()
            bitmap.value = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,data?.data )
        }
    }
    private fun handleThatBackPress(){
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Exit")
                builder.setMessage("Are you sure you want to leave without saving?")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    findNavController().navigate(R.id.dashboardFragment)
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}