package com.license.workguru_app.profile.domain.repository

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.*
import okhttp3.MultipartBody

class ProfileRepository {
    suspend fun listUserHistory(access_token:String): List<Any> {
        return RetrofitInstance.apiProfile.listUserHistory(access_token)
    }

    suspend fun listCountries(access_token:String): CountriesResponse {
        return RetrofitInstance.apiProfile.listCountries(access_token)
    }

    suspend fun listStates(access_token:String, country_name:String): StatesResponse {
        return RetrofitInstance.apiProfile.listStates(access_token, country_name)
    }

    suspend fun listCities(access_token:String, state_id:Int): CitiesResponse{
        return RetrofitInstance.apiProfile.listCities(access_token,state_id )
    }

    suspend fun getUserProfile(access_token:String): ProfileResponse{
        return RetrofitInstance.apiProfile.getUserProfile(access_token )
    }

    suspend fun changeProfileData(
        access_token:String,
        city:String?,
        street_address:String?,
        country: String?,
        avatar: MultipartBody.Part,
        _method:String,
        state:String
    ): MessageResponse{
        return RetrofitInstance.apiProfile.changeProfileData(
            access_token,
            city,
            street_address,
            country,
            avatar,
            _method,
            state
        )
    }

//    suspend fun changeProfileData(
//        access_token:String,
//        city:String?,
//        street_address:String?,
//        country: String?,
//        avatar: String,
//        _method:String,
//        state:String
//    ): MessageResponse{
//        return RetrofitInstance.apiProfile.changeProfileData(access_token, ProfileRequest(city!!, street_address!!, country!!, avatar, _method, state))
//    }

}