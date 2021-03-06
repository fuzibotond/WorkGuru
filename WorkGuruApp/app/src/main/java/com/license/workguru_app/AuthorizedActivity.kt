package com.license.workguru_app

import android.annotation.SuppressLint
import android.app.*
import android.app.ProgressDialog.show
import android.content.*
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.navigation.NavigationView
import com.license.workguru_app.authentification.data.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModel
import com.license.workguru_app.authentification.domain.use_case.log_out.LogoutViewModelFactory
import com.license.workguru_app.di.SharedViewModel
import kotlinx.coroutines.launch
import android.graphics.Color
import android.graphics.Color.GREEN
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.view.*
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.license.workguru_app.pomodoro.data.source.receivers.TimerExpiredReceiver
import com.license.workguru_app.pomodoro.data.source.utils.PrefUtil
import com.license.workguru_app.pomodoro.presentation.PomodoroSettingsDialog
import com.license.workguru_app.profile.data.remote.DTO.ActiveTimer
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_user_profile.UserProfileViewModel
import com.license.workguru_app.profile.domain.use_case.display_user_profile.UserProfileViewModelFactory
import com.license.workguru_app.profile.presentation.components.ChangeStatusDialog
import com.license.workguru_app.profile.presentation.components.FilterColleaguesDialog
import com.license.workguru_app.timetracking.data.remote.DTO.StartTimerResponse
import com.license.workguru_app.timetracking.data.source.services.TimerService
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModel
import com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer.StartPauseStopViewModelFactory
import com.license.workguru_app.timetracking.presentation.components.CreateProjectDialog
import com.license.workguru_app.timetracking.presentation.components.CreateTimerDialog
import com.license.workguru_app.utils.Constants
import com.license.workguru_app.utils.NotificationUtil
import com.license.workguru_app.utils.NotificationUtil.Companion.showTimerExpired
import com.license.workguru_app.utils.NotificationUtil.Companion.showTimerPaused
import com.license.workguru_app.utils.NotificationUtil.Companion.showTimerRunning
import kotlinx.android.synthetic.main.activity_authorized.*
import kotlinx.android.synthetic.main.pomodoro_settings_dialog.*
import kotlinx.android.synthetic.main.project_item_layout.*
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.license.workguru_app.databinding.ActivityAuthorizedBinding
import com.license.workguru_app.utils.NetworkHelper
import com.license.workguru_app.utils.ProfileUtil
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong


class AuthorizedActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityAuthorizedBinding
    private lateinit var navigationView: NavigationView
    lateinit var topAppBar: MaterialToolbar
    lateinit var topAppBarLayout: AppBarLayout
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
    val photo:MutableLiveData<String> = MutableLiveData()

    var activeTimer: ActiveTimer? = null

    val numberOfSessions:MutableLiveData<Int> = MutableLiveData()
    val durationOfSessions:MutableLiveData<Int> = MutableLiveData(0)

    private lateinit var serviceIntent: Intent
    private var time = 0.0

    enum class TimerState{
        Stopped, Paused, Running
    }

    private var timerLengthSeconds: Long = 0
    val timerState:MutableLiveData<TimerState> = MutableLiveData(TimerState.Stopped)
    private var secondsRemaining: Long = 0

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthorizedBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                this@AuthorizedActivity.runOnUiThread {
                    no_network_progress_bar.visibility = View.GONE
                    Toast.makeText(this@AuthorizedActivity, getString(R.string.connected), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                this@AuthorizedActivity.runOnUiThread {
                    no_network_progress_bar.visibility = View.VISIBLE
                    Toast.makeText(this@AuthorizedActivity, getString(R.string.lost_internet_connection), Toast.LENGTH_SHORT).show()

                }

            }

        })


        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.teal_900)); //status bar or the time bar at the top (see example image1)

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal_900)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series  (see example image2)



        btn_add_new_project.visibility = View.GONE

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
        setListeners()
        loadData()

        fab_pause.visibility = View.GONE
        fab_start.visibility = View.GONE
        fab_stop.visibility = View.GONE

        current_pomodoro.visibility = View.GONE

        Toast.makeText(this, getString(R.string.you_logged_in), Toast.LENGTH_SHORT).show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        getCurrentTimer()
    }
    override fun onPause() {
        super.onPause()
        val passedSession = PrefUtil.getIntDataFromPref(this@AuthorizedActivity,"sharedPrefs", "POMODORO_SESSION_PASSED")
        val duration = PrefUtil.getIntDataFromPref(this@AuthorizedActivity,"sharedPrefs", "DURATION_OF_SESSION")
        if (timerState.value == TimerState.Running){
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            val temp = ProfileUtil.getLongPref(this,"USER_TIMER_START_DATE")
            if (temp != null) {
                showTimerRunning(this, temp+TimeUnit.MINUTES.toMillis(duration.toLong()*passedSession))

            }
        }
        else if (timerState.value == TimerState.Paused){
            showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
//        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        timerState.value?.let { PrefUtil.setTimerState(it, this) }

    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        current_pomodoro.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
    }


    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            val passedSession = PrefUtil.getIntDataFromPref(this@AuthorizedActivity,"sharedPrefs", "POMODORO_SESSION_PASSED")
            val duration = PrefUtil.getIntDataFromPref(this@AuthorizedActivity,"sharedPrefs", "DURATION_OF_SESSION")
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            current_timer.text = getTimeStringFromDouble(time)
            current_pomodoro.text = getTimeStringFromDouble(passedSession*duration*60 - time)
            actual_timer.setText(getTimeStringFromDouble(time))

//            Log.d("TIMER", "${passedSession} ${time}")
            if (sharedViewModel.pomodoroIsON.value == true && passedSession-1 != sharedViewModel.numOfPomodoroSession.value!!){
                if (((passedSession * duration * 60) - time) <= 0.0){
//                    Log.d("TIMER", "${passedSession * duration * 60} ${time}")
//                    Toast.makeText(this@AuthorizedActivity, "Pomodoro", Toast.LENGTH_SHORT).show()
                    if(sharedViewModel.pomodoroNotification.value == true){
                        showTimerExpired(this@AuthorizedActivity)
                    }
                    pauseAllTimer()
                    val temp = passedSession+1
                    PrefUtil.saveIntDataOnDifferentPref(this@AuthorizedActivity,"sharedPrefs",temp,"POMODORO_SESSION_PASSED" )
                }
            }
        }
    }

    private fun saveUserData(context: Context, user_id:Int ){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply{
            putInt("USER_ID", user_id)
        }.apply()
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
        sharedViewModel.savePomodoroNotification(sharedPreferences.getBoolean("SEND_NOTIFICATIONS_BY_POMODORO", false))
        sharedViewModel.savePomodoroState(sharedPreferences.getBoolean("POMODORO_IS_ON", false))
        sharedViewModel.saveNumberOfSession(sharedPreferences.getInt("NUMBER_OF_SESSIONS", 0))
        sharedViewModel.saveDurationOfAPomodoroSession(sharedPreferences.getInt("DURATION_OF_SESSION", 0))

    }

    @SuppressLint("UseSupportActionBar")
    private fun initializeView() {
        navigationView = findViewById(R.id.menu_navigation_view)
        topAppBarLayout = findViewById(R.id.topAppBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        appBarConfig = AppBarConfiguration.Builder(R.id.auth_nav_host_fragment)
            .setDrawerLayout(drawerLayout)
            .build()
        navController = findNavController(R.id.auth_nav_host_fragment)
        navigationView.setupWithNavController(navController)

        disableSearching(true)



    }

    @SuppressLint("UseCompatLoadingForColorStateLists", "WrongConstant")
    private fun initMenu(){
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
//                R.id.settings -> {
////                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
//                    // Handle more item (inside overflow menu) press
//                    true
//                }
                R.id.pomodoro -> {
                    val manager = (this).supportFragmentManager
                    PomodoroSettingsDialog().show(manager, "CustomManager")
                    true
                }
                else -> false
            }
        }

        topAppBar.getChildAt(0).setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)

            active_timer_project_name.setText(sharedViewModel.currentProject.value?.project_name)
            active_timer_task_name.setText(sharedViewModel.currentProject.value?.timer_description)
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {

                    // Handle more item (inside overflow menu) press
                    true
                }
                R.id.search_action -> {

                    val searchView = menuItem?.actionView as SearchView
                    searchView.queryHint = getString(R.string.tTypSomething)

                    searchView.setOnQueryTextListener(this)
                    if(!isUsingNightModeResources()){
                        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                        editText.setTextColor(getResources().getColor(R.color.white));
                        editText.setHintTextColor(getResources().getColor(R.color.white));
                    }
                    searchView.setOnQueryTextFocusChangeListener { view, b ->
                        if (!b){
                            sharedViewModel.searchWithKeyword("")
                        }
                    }
                    true
                }
                else -> false
            }
        }


        navigationView.setNavigationItemSelectedListener { menuItem ->

            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.dashboard ->{
                    btn_add_new_project.visibility = View.GONE
//                    disableSearching(true)
                    findNavController(R.id.auth_nav_host_fragment).navigate(R.id.dashboardFragment)

                }
                R.id.profile -> {
                    disableSearching(true)
                    btn_add_new_project.visibility = View.GONE
                    findNavController(R.id.auth_nav_host_fragment).navigate(R.id.profileFragment)
                }
                R.id.projects ->{
                    disableSearching(false)
                    btn_add_new_project.visibility = View.VISIBLE
                    findNavController(R.id.auth_nav_host_fragment).navigate(R.id.projectListFragment)
                }
                R.id.colleagues -> {
                    disableSearching(false)
                    btn_add_new_project.visibility = View.GONE
                    findNavController(R.id.auth_nav_host_fragment).navigate(R.id.colleaguesFragment)
                }
//                R.id.help_requests -> {
//                    disableSearching(false)
//                    btn_add_new_project.visibility = View.GONE
//                    findNavController(R.id.auth_nav_host_fragment).navigate(R.id.messageFragment)
//                }
                R.id.invite_user ->{
                    if (getUserProfileViewModel.data.value?.role == "admin"){
                        disableSearching(true)
                        btn_add_new_project.visibility = View.GONE
                        findNavController(R.id.auth_nav_host_fragment).navigate(R.id.inviteUserFragment)
                    }else{
                        Toast.makeText(this, getString(R.string.tRoleException), Toast.LENGTH_SHORT).show()
                    }
                   
                }
                R.id.waiting_users_list ->{
                    if (getUserProfileViewModel.data.value?.role == "admin"){
                        disableSearching(true)
                        btn_add_new_project.visibility = View.GONE
                        findNavController(R.id.auth_nav_host_fragment).navigate(R.id.waitingUserListFragment)
                    }else{
                        Toast.makeText(this, getString(R.string.tRoleException), Toast.LENGTH_SHORT).show()
                    }

                }
                R.id.sign_out->{
                    disableSearching(true)
                    btn_add_new_project.visibility = View.GONE
                    lifecycleScope.launch {
                        val sharedPreferences: SharedPreferences = this@AuthorizedActivity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)

                        if (savedToken != null) {
                            if(logoutViewModel.logout(savedToken)){
                                val intent = Intent(this@AuthorizedActivity, MainActivity::class.java).apply {
//                                    putExtra(AlarmClock.EXTRA_MESSAGE, "You are logged out!")
                                    Toast.makeText( this@AuthorizedActivity, getString(R.string.tloggedOut), Toast.LENGTH_SHORT).show()
                                    clearDate()
                                }
                                finish()
                                startActivity(intent)
                            }
                        }
                        else{
                            Toast.makeText( this@AuthorizedActivity, getString(R.string.notLoggedInException), Toast.LENGTH_SHORT).show()
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
            val userPhoto: ImageView = header.findViewById(R.id.avatar)
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
                photo.observe(this){
                    val imgUri = Uri.parse(Constants.VUE_APP_USER_AVATAR_URL+photo.value)
                    Glide.with(this)
                        .load(imgUri)
                        .circleCrop()
                        .into(userPhoto);
                }
            }
        }

    }

    private fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTimer(){
        lifecycleScope.launch {
            if (getUserProfileViewModel.getUserProfileInfo()){
                if (getUserProfileViewModel.data.value?.active_timer != null){
                    val state = getUserProfileViewModel.data.value?.active_timer?.state
                    val timer = getUserProfileViewModel.data.value?.active_timer
                    activeTimer = timer
                    time = getUserProfileViewModel.data.value?.active_timer?.elapsed_seconds?.toDouble()!!
                    current_timer.text = getTimeStringFromDouble(time)
                    actual_timer.text = getTimeStringFromDouble(time)
                    active_timer_project_name.setText(timer?.project_name)
                    active_timer_task_name.setText(timer?.task)
                    when (state){
                        "paused" -> {
                            timerState.value = TimerState.Paused
                            sharedViewModel.saveCurrentTimer(StartTimerResponse(timer?.project_id.toString(),
                                timer?.project_name,timer?.started_at,timer?.task, timer?.project_id))
                        }
                        "active" -> {
                            timerState.value = TimerState.Running
                            sharedViewModel.saveCurrentTimer(StartTimerResponse(timer?.project_id.toString(),
                                timer?.project_name,timer?.started_at,timer?.task, timer?.project_id))
                            serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
                            startService(serviceIntent)
                        }
                        else -> {
                            timerState.value = TimerState.Stopped
                            PrefUtil.saveIntDataOnDifferentPref(this@AuthorizedActivity,"sharedPrefs",1,"POMODORO_SESSION_PASSED" )
                        }
                    }
                }
                else{
                    time = 0.0
                    current_timer.text = getTimeStringFromDouble(time)
                    actual_timer.text = getTimeStringFromDouble(time)
                }

                val profileMenu = topAppBar.menu.getItem(1)
                profileMenu.setOnMenuItemClickListener {
                    val manager = this@AuthorizedActivity.supportFragmentManager
                    ChangeStatusDialog().show(manager, "CustomManager")
                    return@setOnMenuItemClickListener true
                }
                sharedViewModel.saveProfileRole(getUserProfileViewModel.data.value!!.role!!)
                saveUserData(this@AuthorizedActivity, getUserProfileViewModel.data.value!!.id)
                Log.d("HELP_REQUEST", getUserProfileViewModel.data.value!!.id.toString())
                photo.value = getUserProfileViewModel.data.value?.avatar
                //Actual timer
                val activeTimer = getUserProfileViewModel.data.value?.active_timer
                if(activeTimer == null){
                    active_timer_project_name.setText(getString(R.string.no_timer))
                    active_timer_task_name.setText("")
                }
            }
        }
        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAllTimer(){
        getCurrentTimer()
        if(timerState.value == TimerState.Stopped){
            val manager = (this as AppCompatActivity).supportFragmentManager
            CreateTimerDialog().show(manager, "CustomManager")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pauseAllTimer(){
        getCurrentTimer()
        if (timerState.value == TimerState.Running) {
            pauseTimer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopAllTimer(){
        getCurrentTimer()
        if (timerState.value == TimerState.Running || timerState.value == TimerState.Paused) {
            resetTimer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun resumeAllTimer(){
        getCurrentTimer()
        if (timerState.value == TimerState.Paused){
            startTimer()
        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners(){
        timerState.observe(this){
            when(timerState.value){
                TimerState.Running ->{
                    timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24))
                }
                TimerState.Stopped ->{
                    timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_24))
                }
                TimerState.Paused->{
                    timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_circle_24))
                }
            }
        }

        sharedViewModel.pomodoroIsON.observe(this){
            if (sharedViewModel.pomodoroIsON.value == true){
                bottomAppBar.menu.getItem(0).setIcon(resources.getDrawable(R.drawable.ic_baseline_alarm_on_24))
                bottomAppBar.menu.getItem(0).setIconTintList(ColorStateList.valueOf(Color.CYAN))

            }else{
                bottomAppBar.menu.getItem(0).setIcon(resources.getDrawable(R.drawable.ic_baseline_alarm_24))
                bottomAppBar.menu.getItem(0).setIconTintList(ColorStateList.valueOf(resources.getColor(R.color.teal_100)))

            }
        }
        btn_add_new_project.setOnClickListener {
            val manager = (this as AppCompatActivity).supportFragmentManager
            CreateProjectDialog().show(manager, "CustomManager")
        }
        timer_launcher_float_button.setOnClickListener{
            if (time == 0.0 && timerState.value == TimerState.Stopped){
                //start
                startAllTimer()
//                Toast.makeText(this, "Start Timer", Toast.LENGTH_SHORT).show()
            }else if(timerState.value == TimerState.Paused){
                // resume
                resumeAllTimer()
//                Toast.makeText(this, "Resume Timer", Toast.LENGTH_SHORT).show()

            }else if (timerState.value == TimerState.Running){
                // pause
                pauseAllTimer()
//                Toast.makeText(this, "Pause Timer", Toast.LENGTH_SHORT).show()
            }
        }
        timer_launcher_float_button.setOnLongClickListener {
            //stop
            stopAllTimer()
            return@setOnLongClickListener true
        }

        sharedViewModel.isTimerStarted.observe(this){
            if (sharedViewModel.isTimerStarted.value == true){
                timerState.value = TimerState.Running
                serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
                startService(serviceIntent)
                timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24))
            }
        }

        startPauseStopViewModel.startedTimer.observe(this){
            startPauseStopViewModel.startedTimer.value?.let { it1 ->
                sharedViewModel.saveCurrentTimer(
                    it1
                )
            }
        }

        sharedViewModel.profileStatus.observe(this){
            val profileMenu = topAppBar.menu.getItem(1)
            if(sharedViewModel.profileStatus.value?.name == "Available" ||
                sharedViewModel.profileStatus.value?.name == "El??rhet??"){
                profileMenu.icon.setTintList(ColorStateList.valueOf(Color.rgb(38, 166, 154)))
            }
            if(sharedViewModel.profileStatus.value?.name == "Busy" ||
                sharedViewModel.profileStatus.value?.name == "Foglalt"){
                profileMenu.icon.setTintList(ColorStateList.valueOf(Color.rgb(189, 189, 189)))
            }
            if(sharedViewModel.profileStatus.value?.name == "Offline" ||
                sharedViewModel.profileStatus.value?.name == "H??zon k??v??l"){
                profileMenu.icon.setTintList(ColorStateList.valueOf(Color.rgb(183, 28, 28)))
            }
        }
    }

    private fun resetTimer() {
        if (sharedViewModel.currentProject.value != null){
            lifecycleScope.launch {
                if(startPauseStopViewModel.stopTimer(true, sharedViewModel.currentProject.value!!.project_id)){
                    timerState.value = TimerState.Stopped
                    timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_24))
                    sharedViewModel.saveTimerState(false)
                    stopService(serviceIntent)
                    time = 0.0
                    current_timer.text = getTimeStringFromDouble(time)
                    actual_timer.text = getTimeStringFromDouble(time)
                    PrefUtil.saveIntDataOnDifferentPref(this@AuthorizedActivity,"sharedPrefs",1,"POMODORO_SESSION_PASSED" )
                }
            }

        }
    }


    private fun startTimer() {
        if (sharedViewModel.currentProject.value != null){
            lifecycleScope.launch {
                val skills = mutableListOf<String>()
                val skillList = ProfileUtil.getStringPref(this@AuthorizedActivity, "USER_SKILLS_LIST")
                println(skillList)
                val temp = skillList?.split('|')
                temp?.forEach {
                    skills.add(it)
                    Log.d("TIMER", "${it}")
                }
//                sharedViewModel.actualSkills.value?.forEach {
//                    skills.add(it.id.toString())
//                }
                if (skills.isNotEmpty() == true){
                    if(startPauseStopViewModel.startTimer(
                            true, sharedViewModel.currentProject.value!!.project_id,
                            sharedViewModel.currentProject.value!!.timer_description!!,
                            skills
                        )){
                        timerState.value = TimerState.Running
                        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
                        startService(serviceIntent)
                        timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24))
                    }
                }
                else{
                    Toast.makeText(this@AuthorizedActivity, "Ain't no skill here...", Toast.LENGTH_SHORT).show()
                }
                
            }
        }

    }

    private fun pauseTimer() {
        if (sharedViewModel.currentProject.value != null){
            lifecycleScope.launch {
                if(startPauseStopViewModel.pauseTimer(false, sharedViewModel.currentProject.value!!.project_id,"")){
                    timerState.value = TimerState.Paused
                    stopService(serviceIntent)
                    timer_launcher_float_button.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_circle_24))
                    PrefUtil.setTimerState(TimerState.Paused,this@AuthorizedActivity)
                    NotificationUtil.showTimerPaused(this@AuthorizedActivity)
                }
            }
        }

    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
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

    override fun onQueryTextSubmit(query: String?): Boolean {

        //TODO: need to request for all of the projects without pagination, searching, and passing the result to sharedviewmodel
        if (query != null) {
            sharedViewModel.searchWithKeyword(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        lifecycleScope.launch {
            delay(1000)
        }
        if (newText != null && newText.length>0) {
            sharedViewModel.searchWithKeyword(newText)
        }
        return true
    }

    fun disableSearching(disable:Boolean){
        val item: MenuItem = topAppBar.menu.findItem(com.license.workguru_app.R.id.search_action)
        item.setVisible(!disable)
    }


}