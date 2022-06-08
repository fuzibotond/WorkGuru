package com.license.workguru_app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.license.workguru_app.MainActivity

import androidx.annotation.NonNull
import com.wonderkiln.camerakit.Permissions
import java.security.Permission
import android.os.Build





class PermissionUtil {
    companion object{
        fun hasWriteExternalStoragePermission(context:Context) =
            ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        fun hasReadExternalStoragePermission(context:Context) =
            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        fun hasCameraPermission(context:Context) =
            ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        fun requestPermissions(context: Context){
            var permissionToRequest = mutableListOf<String>()
            if (!hasWriteExternalStoragePermission(context)) {
                permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!hasReadExternalStoragePermission(context)) {
                permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (!hasCameraPermission(context)) {
                permissionToRequest.add(Manifest.permission.CAMERA)
            }

            if (permissionToRequest.isNotEmpty()){
                ActivityCompat.requestPermissions(context as Activity, permissionToRequest.toTypedArray(), 0)
            }


        }
    }
}