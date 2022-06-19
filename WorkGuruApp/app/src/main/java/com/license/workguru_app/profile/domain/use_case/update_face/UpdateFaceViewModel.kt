package com.license.workguru_app.profile.domain.use_case.update_face

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.StoreFaceRequest
import com.license.workguru_app.profile.data.repository.ProfileRepository
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class UpdateFaceViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    suspend fun updateFaceImage(avatar: String?):Boolean {
        val access_token = getToken()
        try {

            if (avatar != null){

//                val path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DCIM
//                )
//
//                val file = File(getRealPathFromUri(context, avatar))
//                val bmp = BitmapFactory.decodeFile(file.getAbsolutePath())
//                val bos = ByteArrayOutputStream()
//                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
//                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bos.toByteArray())
//                val part = MultipartBody.Part.createFormData("avatar",file.name , requestBody)
                val path = avatar
                val part = path.let { getMultipartBody(it) }
                val methodRequestBody: RequestBody =
                    RequestBody.create("application/json".toMediaTypeOrNull(), "PUT")
                val result = repository.updateFace("Bearer " + access_token, part, methodRequestBody)
                Log.d("PROFILE", "${result}")
                val code = result.code()
                Log.d("PROFILE", "StoreFaceViewModel - exception: ${code}")

                when(code){
                    422 -> Toast.makeText(context, "Can't detect a face on the image", Toast.LENGTH_SHORT).show()
                    400 -> Toast.makeText(context, "You already uploaded an image!", Toast.LENGTH_SHORT).show()
                    404 -> Toast.makeText(context, "Something went wrong at the uploading!", Toast.LENGTH_SHORT).show()
                }
            }
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("PROFILE", "UpdateFaceViewModel - exception: ${e.toString()}")
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