package com.license.workguru_app.profile.domain.use_case.display_user_insights

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.google.gson.GsonBuilder
import com.license.workguru_app.R
import com.license.workguru_app.profile.data.remote.DTO.*

class UserHistoryViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    val dataList:MutableLiveData<List<ProjectHistory>> = MutableLiveData()
    val todaysHistory:MutableLiveData<TodaysHistory> = MutableLiveData()
    val weeklyHistory:MutableLiveData<WeeklyHistory> = MutableLiveData()
    val monthlyHistory:MutableLiveData<MonthlyHistory> = MutableLiveData()
    suspend fun listUserHistory( ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.listUserHistory("Bearer " + access_token)
            //converting ANY data so much fun
            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (!result.isEmpty()){
                val items = mutableListOf<ProjectHistory>()
                for (it in result[0] as ArrayList<*>){
                    if (!items.contains(it)){
                        items.add(fromStringToProjectHistory(it.toString()))
                    }


//                    val item:ProjectHistory = gson.fromJson(it.toString(), ProjectHistory::class.java)
//                    val projectHistory = ProjectHistory(item.name, item.project_id, item.result)
//                    items.add(projectHistory)
                }

                dataList.value = items

                val todayH:TodaysHistory
                Log.d("HISTORY", "${result[1]}")
                if(result[1].toString().reversed()[1] == 'A'  ){
                    Log.d("HISTORY", result[1].toString().takeWhile { it!='p' }.dropLast(2)+"}")
                    val TodayItem = gson.fromJson(result[1].toString().takeWhile { it!='p' }.dropLast(2)+"}",TodaysHistoryShort::class.java)
                    todayH = TodaysHistory( TodayItem.today_tracked, TodayItem.yesterday_tracked, 0.0)
//                    Log.d("HISTORY", "${todayH}")
                }
                else{
                    val TodayItem = gson.fromJson(result[1].toString(),TodaysHistory::class.java)
                    todayH = TodaysHistory( TodayItem.today_tracked, TodayItem.yesterday_tracked, TodayItem.percent)
                }
                todaysHistory.value = todayH

                val weeklyH:WeeklyHistory
                Log.d("HISTORY", result[2].toString())
                if(result[2].toString().reversed()[1] == 'A'  ){
//                    Log.d("HISTORY", result[2].toString().takeWhile { it!='p' })
                    val WeeklyItem = gson.fromJson(result[2].toString().takeWhile { it!='p' }.dropLast(2)+"}",WeeklyHistoryShort::class.java)
                    weeklyH = WeeklyHistory( WeeklyItem.this_week, WeeklyItem.last_week, 0.0)
                    Log.d("HISTORY", "${weeklyH}")
                }
                else{
                    val WeeklyItem = gson.fromJson(result[2].toString(),WeeklyHistory::class.java)
                    weeklyH = WeeklyHistory( WeeklyItem.this_week, WeeklyItem.last_week, WeeklyItem.percent)
                }
                weeklyHistory.value = weeklyH

                val monthlyH:MonthlyHistory
                Log.d("HISTORY", "${result[3]}")
                if(result[3].toString().reversed()[1] == 'A'  ){
//                    Log.d("HISTORY", result[3].toString().takeWhile { it!='p' })
                    val MonthlyItem = gson.fromJson(result[3].toString().takeWhile { it!='p' }.dropLast(2)+"}",MonthlyHistoryShort::class.java)
                    monthlyH = MonthlyHistory( MonthlyItem.this_month, MonthlyItem.last_month, 0.0)
                    Log.d("HISTORY", "${weeklyH}")
                }
                else{
                    val MonthlyItem = gson.fromJson(result[3].toString(),MonthlyHistory::class.java)
                    monthlyH = MonthlyHistory( MonthlyItem.this_month, MonthlyItem.last_month, MonthlyItem.percent)
                }
                monthlyHistory.value = monthlyH
            }else{

                Toast.makeText(context, context.getString(R.string.you_havent_start_any_project_yet), Toast.LENGTH_SHORT).show()
                dataList.value = arrayListOf()
                return false
            }


            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("HISTORY", "userHistoryVM - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
    private fun fromStringToProjectHistory(value:String):ProjectHistory{
        val n = value.length
        val temp  = value.takeLast(n-1).dropLast(1)
        val items = temp.split(',')
        val project_id = items[0].takeLastWhile { it!='=' }.takeWhile { it!='.' }.toInt()
        val result = items[1].takeLastWhile { it!='=' }
        val name = items[2].takeLastWhile { it!='=' }
        return ProjectHistory(project_id, result, name)
    }
}



