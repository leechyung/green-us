package kr.ac.kpu.green_us.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {
    private val prefer : SharedPreferences = context.getSharedPreferences("prefers", Context.MODE_PRIVATE)

    fun getString(key: String, value: String):String{
        return prefer.getString(key,value).toString()
    }

    fun setString(key: String, value: String){
        prefer.edit().putString(key, value).apply()
    }


}