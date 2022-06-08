package com.license.workguru_app.profile.domain.use_case.store_face

import android.R.attr
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.StoreFaceRequest
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.data.source.utils.FileUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.graphics.Bitmap

import android.R.attr.bitmap
import android.content.ContextWrapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.*
import java.util.*


class StoreFaceViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    suspend fun storeFaceImage(avatar: String?):Boolean {

        val access_token = getToken()
        try {

            if (avatar != null){

//                val path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DCIM
//                )
//                val path = getRealPathFromUri(context, avatar)
//                val file = File(avatar.path)
//                val path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DCIM
//                )
//                val file = File(path, "IMG_20220607_182538.jpg")
//                val bmp = BitmapFactory.decodeFile(file.getAbsolutePath())
//                val bos = ByteArrayOutputStream()
//                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
//                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
//                    file?.absoluteFile
//                )
//                val part = MultipartBody.Part.createFormData("face_image", file.name , requestBody)
                val path = avatar
                val part = path.let { getMultipartBody(it) }
                val result = repository.storeFace("Bearer " + access_token,part )
//                val byteArrayOutputStream = ByteArrayOutputStream()
//                avatar.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//                val encodedImage =
//                    Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

//                val request = StoreFaceRequest(encodedImage)
//                val result = repository.storeFace("Bearer " + access_token,request )

                Log.d("PROFILE", " ${result}")
                val code = result.code()

                when(code){
                    422 -> Toast.makeText(context, "Can't detect a face on the image", Toast.LENGTH_SHORT).show()
                    400 -> Toast.makeText(context, "You already uploaded an image!", Toast.LENGTH_SHORT).show()
                    404 -> Toast.makeText(context, "Something went wrong at the uploading!", Toast.LENGTH_SHORT).show()
                }

            }

            return true
        } catch (e: Exception) {
            val code = e.message?.takeLast(3)
            Log.d("PROFILE", "StoreFaceViewModel - exception: ${code}")

            if (code != null) {
                when(code.toInt()){
                    422 -> Toast.makeText(context, "Can't detect a face on the image", Toast.LENGTH_SHORT).show()
                    400 -> Toast.makeText(context, "You already uploaded an image!", Toast.LENGTH_SHORT).show()
                    404 -> Toast.makeText(context, "Something went wrong at the uploading!", Toast.LENGTH_SHORT).show()
                }
            }
            Log.d("PROFILE", "StoreFaceViewModel - exception: ${e.toString()}")
            return false
        }
    }

    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
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

}