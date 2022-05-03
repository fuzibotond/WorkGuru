package com.license.workguru_app

import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.net.Uri
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.navigation.NavigationView
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModel
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModelFactory
import com.license.workguru_app.di.SharedViewModel
import kotlinx.coroutines.launch
import android.provider.MediaStore

import android.graphics.Bitmap
import android.graphics.Color
import android.os.CountDownTimer
import androidx.appcompat.widget.AppCompatImageView
import com.anychart.standalones.ColorRange
import com.bumptech.glide.Glide
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_user_insights.userHistoryViewModelFactory
import com.license.workguru_app.profile.domain.use_case.display_user_profile.UserProfileViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_profile.UserProfileViewModelFactory
import com.license.workguru_app.timetracking.data.remote.DTO.StartTimerResponse
import com.license.workguru_app.timetracking.data.source.services.TimerService
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModel
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModelFactory
import com.license.workguru_app.timetracking.presentation.components.CreateTimerDialog
import com.license.workguru_app.timetracking.presentation.components.FilterDialog
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_authorized.*
import kotlinx.android.synthetic.main.project_item_layout.*
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.math.roundToInt


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
    lateinit var getUserProfileViewModel:UserProfileViewModel
    lateinit var startPauseStopViewModel: StartPauseStopViewModel

    val email:MutableLiveData<String> = MutableLiveData()
    val name:MutableLiveData<String> = MutableLiveData()

    private lateinit var serviceIntent: Intent
    private var time = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorized)

        val factory = LogoutViewModelFactory(this, AuthRepository())
        logoutViewModel = ViewModelProvider(this, factory).get(LogoutViewModel::class.java)

        val timerFactory = StartPauseStopViewModelFactory(this, TimeTrackingRepository())
        startPauseStopViewModel = ViewModelProvider(this,timerFactory ).get(StartPauseStopViewModel::class.java)

        val userProfileFactory = UserProfileViewModelFactory(this, ProfileRepository())
        getUserProfileViewModel = ViewModelProvider(this, userProfileFactory).get(
            UserProfileViewModel::class.java)

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        initializeView()
        initMenu()
        getCurrentTimer()
        setListeners()
        loadData()
    }

    private fun getCurrentTimer() {
        getUserProfileViewModel.data.observe(this){
            val userInfo = getUserProfileViewModel.data.value
            if (userInfo?.active_timer != null){
                current_timer.text = getTimeStringFromDouble(userInfo.active_timer.elapsed_seconds.toDouble() )
                time = userInfo.active_timer.elapsed_seconds.toDouble()
                sharedViewModel.saveTimerState(true)
                sharedViewModel.saveCurrentTimer(StartTimerResponse(
                    userInfo.active_timer.project_id.toString(),
                    userInfo.active_timer.project_name,
                    userInfo.active_timer.started_at,
                    userInfo.active_timer.task,
                    userInfo.active_timer.project_id
                ))
            }
            if(time==0.0){
                timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_24))
            }else{
                timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24))
            }
        }

    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            current_timer.text = getTimeStringFromDouble(time)

        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d", hour, min, sec)

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
                R.id.colleagues -> findNavController(R.id.auth_nav_host_fragment).navigate(R.id.colleaguesFragment)
                R.id.sign_out->{
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
                                finish()
                                startActivity(intent)
                            }
                        }
                        else{
                            Toast.makeText( this@AuthorizedActivity, "Not Logged in ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            drawerLayout.close()
            true
        }

        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
            val header = navigationView.getHeaderView(0)
            val userEmail: TextView = header.findViewById(R.id.sub_name)
            val userName: TextView = header.findViewById(R.id.name)
            val userPhoto: CircleImageView = header.findViewById(R.id.avatar)
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl
                userEmail.setText(personEmail)
                userName.setText(personName)
                Glide.with(this)
                    .load(personPhoto)
                    .centerCrop()
                    .into(userPhoto);

            }else{
                email.observe(this){
                userEmail.setText(email.value!!)
                userName.setText(name.value!!)

                }
            }
        }

    }

    private fun setListeners(){
        timer_launcher_float_button.setOnClickListener{
            if (time == 0.0 ){
                val manager = (this as AppCompatActivity).supportFragmentManager
                CreateTimerDialog().show(manager, "CustomManager")
            }else if(sharedViewModel.isTimerStarted.value == false){
                sharedViewModel.saveTimerState(true)
            }else{
                sharedViewModel.saveTimerState(false)
            }

        }
        timer_launcher_float_button.setOnLongClickListener {
            resetTimer()

            return@setOnLongClickListener true
        }

        sharedViewModel.isTimerStarted.observe(this){
            startStopTimer()
        }

        startPauseStopViewModel.startedTimer.observe(this){
            startPauseStopViewModel.startedTimer.value?.let { it1 ->
                sharedViewModel.saveCurrentTimer(
                    it1
                )
            }
        }


    }

    private fun resetTimer() {
        stopTimer()
        if (sharedViewModel.currentProject.value != null){
            lifecycleScope.launch {
                startPauseStopViewModel.stopTimer(true, sharedViewModel.currentProject.value!!.project_id)
            }
            timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_24))
            sharedViewModel.saveTimerState(false)
        }
        time = 0.0
        current_timer.text = getTimeStringFromDouble(time) 

    }

    private fun startStopTimer() {
        if(sharedViewModel.isTimerStarted.value == false)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24))
    }


    private fun stopTimer() {
        stopService(serviceIntent)
        timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_24))
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            getUserProfileViewModel.getUserProfileInfo()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
    }

    private fun clearDate() {
        val sharedPreferences:SharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }

    private fun convertLongToMinutes(doubleValue: Int):String{

        val value = doubleValue

        val hours: Int = value / 3600
        val minutes: Int = value % 3600 / 60
        if (value == 0){
            return "00:00"
        }

        else if (minutes.toInt() % 60 == 0){
            if (minutes<10){
                return "00:0"+ minutes
            }else{
                return "00:"+minutes
            }
        }else{
            if (hours<10){
                if (minutes<10){
                    return "0"+hours+":0"+minutes
                }else{
                    return "0"+hours+":"+minutes
                }

            }else{
                if (minutes<10){
                    return ""+hours+":0"+minutes
                }else{
                    return ""+hours+":"+minutes
                }
            }
        }
    }
}