package kr.ac.kpu.green_us

import kr.ac.kpu.green_us.util.FirebaseAuthUtils
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kr.ac.kpu.green_us.util.SharedPreferencesUtil

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //이전에 로그인한 사용자 정보를 가져옴
        val user = Firebase.auth.currentUser

        // 이전에 자동로그인 버튼 클릭 여부를 확인함
        PreferApplication.prefer = SharedPreferencesUtil(applicationContext)
        val switchStatus = PreferApplication.prefer.getString("switch","")
        Log.d("switchStatus",switchStatus)

        // 자동 로그인 기능 구현 코드
        if (switchStatus == "on"){ // 자동로그인을 클릭했었다면
            if(user != null){ // 로그인한 이력이 있다면 로그인처리 함
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                },2000)
            }
            else{ // 자동로그인 클릭은 했으나 로그인한 적 없을 시 로그인 화면으로 이동함
                Toast.makeText(
                    baseContext,
                    "로그인 이력이 없습니다.\n 로그인을 먼저 해주세요",
                    Toast.LENGTH_SHORT,
                ).show()
                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                },2000)
            }
        }else{ // 자동로그인 클릭 안 했었다면 로그인 화면으로 이동함
            Handler().postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            },2000)
        }


//        if (user == null){ // 로그인한 적이 없다면 로그인 및 회원가입을 할 수 있는 ui를 반환
//            Handler().postDelayed({
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                startActivity(intent)
//                finish()
//            },2000)
//        }
//        else{ // 로그인한 적이 있다면 자동로그인하며 메인화면 반환
//            Handler().postDelayed({
////                Log.d("uid",uid)
//                val intent = Intent(this, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                startActivity(intent)
//                finish()
//            },2000)
//        }
//        Handler().postDelayed({
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                startActivity(intent)
//                finish()
//            },2000)
    }

}