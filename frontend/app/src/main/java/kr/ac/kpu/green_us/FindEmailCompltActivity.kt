package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kpu.green_us.databinding.ActivityFindEmailCompltBinding

// 이메일 찾기 - 가입한 이메일 띄움
class FindEmailCompltActivity:AppCompatActivity() {
    private lateinit var binding: ActivityFindEmailCompltBinding
    private lateinit var searchedUserEmail:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindEmailCompltBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchedUserEmail = intent.getStringExtra("userEmail").toString()
        binding.searchedEmail.text = "가입하신 이메일은 "+searchedUserEmail+"입니다."

        binding.goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}