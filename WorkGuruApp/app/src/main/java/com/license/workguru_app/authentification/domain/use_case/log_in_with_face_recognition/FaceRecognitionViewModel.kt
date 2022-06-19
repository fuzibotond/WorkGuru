package com.license.workguru_app.authentification.domain.use_case.log_in_with_face_recognition

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import android.widget.Toast
import com.license.workguru_app.profile.data.repository.ProfileRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.os.Environment
import java.io.File
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.license.workguru_app.authentification.data.remote.DTO.LoginWithFaceIdRequest
import com.license.workguru_app.authentification.data.repository.AuthRepository
import com.license.workguru_app.utils.ProfileUtil
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream


class FaceRecognitionViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    var access_token: MutableLiveData<String> = MutableLiveData()

    suspend fun loginWithFace(
        avatar: String?,
        email:String?
    ):Boolean {

        try {

            if (avatar != null && email != null){

//                val path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DCIM
//                )
//
//                val file = File( getRealPathFromUri(context, avatar))
//                val bmp = BitmapFactory.decodeFile(file.getAbsolutePath())
//                val bos = ByteArrayOutputStream()
//                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
//                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bos.toByteArray())
//                val part = MultipartBody.Part.createFormData("avatar",file.name , requestBody)

                val path = avatar
                val part = path?.let { getMultipartBody(it) }
                val emailRequestBody: RequestBody =
                    RequestBody.create("application/json".toMediaTypeOrNull(), email)
//                val request = LoginWithFaceIdRequest( "hcollins@gmail.com", "/home/szabi/Desktop/Images for Face API/rosh3.jpg", )
                val result = repository.loginWithFace( part, emailRequestBody )
                access_token.value = result.access_token
                Log.d("FACE_API", "${result}")
                ProfileUtil.saveUserStringData(context,result.access_token,"ACCESS_TOKEN")
                ProfileUtil.saveUserStringData(context, email.takeWhile { it!='@' },"USER_NAME")
                ProfileUtil.saveUserStringData(context, email,"USER_EMAIL")
                ProfileUtil.saveUserStringData(context,result.expires_at,"EXPIRES_AT")
//                Toast.makeText(context, "${result.message}", Toast.LENGTH_SHORT).show()
            }




            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.d("FACE_API", "FaceRecognitionViewModel - exception: ${e.toString()}")
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
            Log.e("PROFILE", "picturePath : $picturePath")
            cursor.close()
        }
        return picturePath
    }

    private fun getMultipartBody(filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("face_image", file.name, requestBody)
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
}