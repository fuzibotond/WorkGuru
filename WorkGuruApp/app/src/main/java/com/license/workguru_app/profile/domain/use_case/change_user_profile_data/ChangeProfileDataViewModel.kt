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
import okhttp3.MediaType
import java.io.ByteArrayOutputStream


class ChangeProfileDataViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    suspend fun changeData(
        city:String?,
        street_address:String?,
        country: String?,
        avatar: Uri?,
        _method:String,
        state:String,
        isRemoving:Boolean,
        phone_number:String,
        zip:String
    ):Boolean {


        val access_token = getToken()
        try {

            if (avatar != null){

                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                )

                val file = File( getRealPathFromUri(context, avatar))
                val bmp = BitmapFactory.decodeFile(file.getAbsolutePath())
                val bos = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bos.toByteArray())
                val part = MultipartBody.Part.createFormData("avatar",file.name , requestBody)

                Log.d("PROFILE", "${city} ${street_address} ${country} ${part.headers()?.names()?.size} ${_method} ${state} ${isRemoving} ${phone_number} ${zip}")

                val result = repository.changeProfileData("Bearer " + access_token,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "0734484839",
                    null,
//                    city,
//                    "Szezam utca",
//                    country,
//                    part,
//                    _method,
//                    state,
//                    isRemoving,
//                    phone_number,
//                    zip
                )

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
//            else{
//                val path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DCIM
//                )
//
//
//                val result = repository.changeProfileData("Bearer " + access_token,
//                    city,
//                    "Szezam utca",
//                    country,
//                    null,
//                    _method,
//                    state,
//                    true,
//                    phone_number,
//                    zip
//                )
//                Log.d("PROFILE", "${result.message}")
//                Toast.makeText(context, "${result.message}", Toast.LENGTH_SHORT).show()
//            }




            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
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
}