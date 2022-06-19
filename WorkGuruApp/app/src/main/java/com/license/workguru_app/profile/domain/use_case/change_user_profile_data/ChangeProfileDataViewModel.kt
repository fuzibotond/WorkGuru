package com.license.workguru_app.profile.domain.use_case.change_user_profile_data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.repository.ProfileRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.os.Environment
import java.io.File
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64OutputStream
import com.license.workguru_app.R
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream


class ChangeProfileDataViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    suspend fun changeData(
        city:String?,
        street_address:String?,
        country: String?,
        avatar: String?,
        state:String?,
        isRemoving:Boolean?,
        phone_number:String?,
        zip:String?
    ):Boolean {


        val access_token = getToken()
        try {

            if (avatar != null){

                val path = avatar
                val part = path.let { getMultipartBody(it) }
                val cityRequestBody: RequestBody? =
                    city?.let { RequestBody.create("application/json".toMediaTypeOrNull(), it) }
                val streetAddressRequestBody: RequestBody? =
                    street_address?.let {
                        RequestBody.create("application/json".toMediaTypeOrNull(),
                            it
                        )
                    }
                val countryRequestBody: RequestBody? =
                    country?.let { RequestBody.create("application/json".toMediaTypeOrNull(), it) }
                val stateRequestBody: RequestBody? =
                    state?.let { RequestBody.create("application/json".toMediaTypeOrNull(), it) }
                val isRemovingRequestBody: RequestBody? =
                    RequestBody.create("application/json".toMediaTypeOrNull(), isRemoving.toString())
                val phoneNumberRequestBody: RequestBody? =
                    phone_number?.let {
                        RequestBody.create("application/json".toMediaTypeOrNull(),
                            it
                        )
                    }
                val zipRequestBody: RequestBody? =
                    zip?.let { RequestBody.create("application/json".toMediaTypeOrNull(), it) }

                val result = repository.changeProfileData(
                    "Bearer " + access_token,
                    cityRequestBody,
                    streetAddressRequestBody,
                    countryRequestBody,
                    part,
                    stateRequestBody,
                    isRemovingRequestBody,
                    phoneNumberRequestBody,
                    zipRequestBody
                )

//                val result = repository.changeProfileData(
//                    "Bearer " + access_token,
//                    city,
//                    street_address,
//                    country,
//                    part,
//                    state,
//                    isRemoving,
//                    phone_number,
//                    zip
//                )
//
//                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
//                builder.addFormDataPart("city", "City")
//                    .addFormDataPart("street_address", "Street Address")
//                    .addFormDataPart("country", "Country")
//                    .addFormDataPart("_method", "PUT")
//                    .addFormDataPart("state", "State")
//                    .addFormDataPart("phone_number", "123456")
//                    .addFormDataPart("zip", "043434")
//
//                builder.addFormDataPart(
//                    "avatar",
//                    file.name,
//                    RequestBody.create(MultipartBody.FORM, bos.toByteArray())
//                )
//                val requestBody: RequestBody = builder.build()
//                val result = RetrofitInstance.apiProfile.getPostCreateBodyResponse("multipart/form-data", access_token,requestBody)
                Log.d("PROFILE", "${result.message}")
                Toast.makeText(context, "${result.message}", Toast.LENGTH_SHORT).show()
            }




            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("PROFILE", "ChangeProfileDataViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
    fun getRealPathFromUri(ctx: Context, uri: Uri?): String? {
        val filePathColumn = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor: Cursor? = uri?.let {
            ctx.contentResolver.query(
                it, filePathColumn,
                null, null, null
            )
        }
        var picturePath:String? = null
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            picturePath = cursor.getString(columnIndex)
            Log.e("", "picturePath : $picturePath")
            cursor.close()
        }
        return picturePath
    }
    fun convertImageFileToBase64(imageFile: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor = context.getContentResolver().query(contentURI, null, null, null, null)!!
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
    private fun getMultipartBody(filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("avatar", file.name, requestBody)
    }
}