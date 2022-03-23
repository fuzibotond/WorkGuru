package com.license.workguru_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.license.workguru_app.authentification.presentation.SharedViewModel
import kotlinx.android.synthetic.main.activity_authorized.*
import kotlinx.android.synthetic.main.activity_authorized.view.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AuthorizedActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
    lateinit var topAppBar: MaterialToolbar
    lateinit var profileMennu: ActionMenuItemView
    lateinit var bottomAppBar: BottomAppBar
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    val sharedViewModel: SharedViewModel by viewModels()
    val name:MutableLiveData<String> = MutableLiveData()
    val email:MutableLiveData<String> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorized)
        loadData()
        initializeView()
        initMenu()
    }
    private fun initializeView() {
        navigationView = findViewById(R.id.menu_navigation_view)
        topAppBar = findViewById(R.id.topAppBar)
        profileMennu = findViewById(R.id.profile)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        appBarConfig = AppBarConfiguration.Builder(R.id.auth_nav_host_fragment)
            .setDrawerLayout(drawerLayout)
            .build()
        setSupportActionBar(bottomAppBar)
        navController = findNavController(R.id.auth_nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfig)
        navigationView.setupWithNavController(navController)
        bottomAppBar.setupWithNavController(navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
            val header = navigationView.getHeaderView(0)
            val nickName:TextView = header.findViewById(R.id.name)
            val emailTextView:TextView = header.findViewById(R.id.sub_name)
            if (email.value!=null){
                nickName.setText(name.value)
                emailTextView.setText(email.value)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        email.value = sharedPreferences.getString("USER_EMAIL", null)
        name.value = sharedPreferences.getString("USER_NAME", null)
        Toast.makeText(this, "${email.value} ${name.value}", Toast.LENGTH_SHORT).show()
    }
}