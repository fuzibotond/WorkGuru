package com.license.workguru_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.navigation.NavigationView
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModel
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModelFactory
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModel
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModelFactory
import com.license.workguru_app.authentification.presentation.SharedViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class AuthorizedActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
    lateinit var topAppBar: MaterialToolbar
    lateinit var topAppBarLayout: AppBarLayout
    lateinit var profileMennu: ActionMenuItemView
    lateinit var bottomAppBar: BottomAppBar
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var logoutViewModel: LogoutViewModel
    val email:MutableLiveData<String> = MutableLiveData()
    val name:MutableLiveData<String> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorized)

        val factory = LogoutViewModelFactory(this, AuthRepository())
        logoutViewModel = ViewModelProvider(this, factory).get(LogoutViewModel::class.java)

        initializeView()
        initMenu()
        loadData()
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        email.value = sharedPreferences.getString("USER_EMAIL", null)
        name.value = sharedPreferences.getString("USER_NAME", null)
    }

    @SuppressLint("UseSupportActionBar")
    private fun initializeView() {
        navigationView = findViewById(R.id.menu_navigation_view)
        topAppBarLayout = findViewById(R.id.topAppBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        profileMennu = findViewById(R.id.profile)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        appBarConfig = AppBarConfiguration.Builder(R.id.auth_nav_host_fragment)
            .setDrawerLayout(drawerLayout)
            .build()
        setSupportActionBar(bottomAppBar)
//        setSupportActionBar(topAppBar)
        navController = findNavController(R.id.auth_nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfig)
        navigationView.setupWithNavController(navController)
        bottomAppBar.setupWithNavController(navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        topAppBarLayout.setExpanded(true)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_app_bar,menu)
        val searchItem = menu?.findItem(R.id.search_action)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Type for searching..."
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        sharedViewModel.searchWithKeyword(query)
                    }
                    //show submitted text for testing purposes.
                    Toast.makeText(applicationContext, "Looking for $query", Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        sharedViewModel.searchWithKeyword(newText)
                    }
                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists", "WrongConstant")
    private fun initMenu(){

        bottomAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            Toast.makeText(this, "Icon", Toast.LENGTH_SHORT).show()
        }

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    // Handle more item (inside overflow menu) press
                    true
                }
                R.id.pomodoro -> {
                    Toast.makeText(this, "Pomodoro", Toast.LENGTH_SHORT).show()
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }
        }

        topAppBar.getChildAt(0).setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    lifecycleScope.launch {
                        val sharedPreferences: SharedPreferences = this@AuthorizedActivity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)

                        if (savedToken != null) {
                            if(logoutViewModel.logout(savedToken)){
                                val intent = Intent(this@AuthorizedActivity, MainActivity::class.java).apply {
                                    putExtra(AlarmClock.EXTRA_MESSAGE, "You are logged out!")
                                    Toast.makeText( this@AuthorizedActivity, "Logged out", Toast.LENGTH_SHORT).show()
                                    clearDate()
                                }
                                startActivity(intent)
                            }
                        }
                        else{
                            Toast.makeText( this@AuthorizedActivity, "Not Logged in ", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->

            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.dashboard -> findNavController(R.id.auth_nav_host_fragment).navigate(R.id.dashboardFragment)
                R.id.profile -> findNavController(R.id.auth_nav_host_fragment).navigate(R.id.profileFragment)
                R.id.projects -> findNavController(R.id.auth_nav_host_fragment).navigate(R.id.projectListFragment)
            }

            drawerLayout.close()
            true
        }

        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
            val header = navigationView.getHeaderView(0)
            val userEmail: TextView = header.findViewById(R.id.sub_name)
            val userName: TextView = header.findViewById(R.id.name)

            email.observe(this){
                userEmail.setText(email.value!!)
                userName.setText(name.value!!)
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
    }
    private fun clearDate() {
        val sharedPreferences:SharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }
}