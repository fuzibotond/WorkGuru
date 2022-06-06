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
import com.license.workguru_app.authentification.data.remote.DTO.LoginWithFaceIdRequest
import com.license.workguru_app.authentification.data.repository.AuthRepository
import okhttp3.MediaType
import java.io.ByteArrayOutputStream


class FaceRecognitionViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    suspend fun loginWithFace(
        avatar: Uri?,
        email:String?
    ):Boolean {

        try {

            if (avatar != null && email != null){

                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                )

                val file = File( getRealPathFromUri(context, avatar))
                val bmp = BitmapFactory.decodeFile(file.getAbsolutePath())
                val bos = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bos.toByteArray())
                val part = MultipartBody.Part.createFormData("avatar",file.name , requestBody)

                Log.d("FACE_API", "${part.headers()?.names()?.size} ")
                val request = LoginWithFaceIdRequest(email = email, file.path)
                val result = repository.loginWithFace( request )
                Log.d("FACE_API", "${result.message}")
                Toast.makeText(context, "${result.message}", Toast.LENGTH_SHORT).show()
            }




            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
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
            Log.e("", "picturePath : $picturePath")
            cursor.close()
        }
        return picturePath
    }
}