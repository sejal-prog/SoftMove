package com.example.softmove.ViewModel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExercisesViewModel: ViewModel() {
    var times: Int = 0;
    lateinit var sharedPreferences: SharedPreferences
    val YOGA_COUNT = "yoga_count"
    val YOGA_SECONDS = "yoga_minutes"
    val STRETCHING_COUNT = "stretching_count"
    val STRETCHING_SECONDS = "stretching_minutes"
    var test = MutableLiveData<String>()

    fun init(context: Context, prefName: String) {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    fun getYogaCount(): Int {
        return sharedPreferences.getInt(YOGA_COUNT, 0)
    }

    fun updateYogaCount() {
        sharedPreferences.edit().putInt(YOGA_COUNT, getYogaCount() + 1).apply()
    }

    fun getYogaSec(): Int {
        return sharedPreferences.getInt(YOGA_SECONDS, 0)
    }

    fun updateYogaSec(seconds: Int) {
        sharedPreferences.edit().putInt(YOGA_SECONDS, getYogaSec() + seconds).apply()
    }

    fun getStretchingCount(): Int {
        return sharedPreferences.getInt(STRETCHING_COUNT, 0)
    }

    fun updateStretchingCount() {
        sharedPreferences.edit().putInt(STRETCHING_COUNT, getStretchingCount() + 1).apply()
    }

    fun getStretchingSec(): Int {
        return sharedPreferences.getInt(STRETCHING_SECONDS, 0)
    }

    fun updateStretchingSec(seconds: Int) {
        sharedPreferences.edit().putInt(STRETCHING_SECONDS, getStretchingSec() + seconds).apply()
    }


}