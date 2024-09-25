package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.ac.kpu.green_us.databinding.ActivityFindPwBinding

// 비밀번호 찾기 - 이메일 입력 시 비밀번호 바꾸는 메일 전송
class FindPwActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindPwBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var email_msg = ""

        // 뒤로가기 버튼 클릭 시 로그인 화면으로
        binding.btnEsc.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.sendEmail.isEnabled = false
        binding.sendEmail.setAlpha(0.5f)
        binding.email.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                email_msg = binding.email.getText().toString()
                if(email_msg.isNotEmpty()) {
                    binding.sendEmail.isEnabled = true
                    binding.sendEmail.setAlpha(1f)
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                email_msg = binding.email.getText().toString()
                if(email_msg.isNotEmpty()){
                    binding.sendEmail.isEnabled = true
                    binding.sendEmail.setAlpha(1f)
                }
                else{
                    binding.sendEmail.isEnabled = false
                    binding.sendEmail.setAlpha(0.5f)
                }
            }
        })
        
        binding.sendEmail.setOnClickListener {
            val email = binding.email.getText().toString().trim()
            if(email.isNotEmpty()){
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FindPwActivity", "비밀번호 재설정 Email 전송완료.")
                            binding.showText.setText("메일이 전송되었습니다")
                            Handler(Looper.getMainLooper()).postDelayed({
                                // 5초 뒤 이전 화면으로
                                finish()
                            }, 5000)
                        }else{
                            Log.e("FindPwActivity", "비밀번호 재설정 Email 전송 실패.")
                            Toast.makeText(this, "회원을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this, "회원을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

    }
}