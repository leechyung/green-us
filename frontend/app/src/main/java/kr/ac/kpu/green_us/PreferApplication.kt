package kr.ac.kpu.green_us

import android.app.Application
import kr.ac.kpu.green_us.util.SharedPreferencesUtil

class PreferApplication:Application() {
    companion object{
        lateinit var prefer : SharedPreferencesUtil
    }
    override fun onCreate() {
        prefer = SharedPreferencesUtil(applicationContext)
        super.onCreate()
    }
}