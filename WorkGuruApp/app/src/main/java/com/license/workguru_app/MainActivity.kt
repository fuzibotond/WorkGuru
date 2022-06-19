package com.license.workguru_app

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.utils.PermissionUtil
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WorkGuruApp)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.teal_900)); //status bar or the time bar at the top (see example image1)

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal_900)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series  (see example image2)
        }
        PermissionUtil.requestPermissions(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()){
            for (i in grantResults.indices){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSIONS", "${permissions[i]} granted")
                }
            }
        }
    }


}