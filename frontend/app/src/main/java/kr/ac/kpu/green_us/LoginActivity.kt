package kr.ac.kpu.green_us

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kr.ac.kpu.green_us.databinding.ActivityLoginBinding

// 로그인 화면 - 이메일, 비밀번호 입력, 자동로그인 지원, 이메일 찾기, 비밀번호 찾기, 회원가입 화면으로 이동 가능
class LoginActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var lastBackPressedTime = 0L
    private val BackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (lastBackPressedTime > System.currentTimeMillis() - 1500 ){
                this@LoginActivity.finishAffinity()
            }else{
                Toast.makeText(this@LoginActivity,"앱을 종료하려면 뒤로 가기를 한 번 더 눌러주세요",Toast.LENGTH_SHORT).show()
                lastBackPressedTime = System.currentTimeMillis()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        auth = Firebase.auth

        // 휴대폰 이전 버튼
        onBackPressedDispatcher.addCallback(this, BackPressedCallback)

        // 진입시 자동로그인버튼 초기화
        btnAutoInit()

        var id_msg = ""
        var pw_msg = ""

        // 아이디, 비밀번호 입력 시 로그인 버튼 활성화
        binding.login.isEnabled = false
        binding.login.setAlpha(0.5f)
        binding.id.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                id_msg = binding.id.getText().toString()
                pw_msg = binding.pw.getText().toString()
                if(id_msg.isNotEmpty()&&pw_msg.isNotEmpty()) {
                    binding.login.isEnabled = true
                    binding.login.setAlpha(1f)
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                id_msg = binding.id.getText().toString()
                pw_msg = binding.pw.getText().toString()
                if(id_msg.isNotEmpty()&&pw_msg.isNotEmpty()) {
                    binding.login.isEnabled = true
                    binding.login.setAlpha(1f)
                }
                else{
                    binding.login.isEnabled = false
                    binding.login.setAlpha(0.5f)
                }
            }
        })

        binding.pw.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                id_msg = binding.id.getText().toString()
                pw_msg = binding.pw.getText().toString()
                if(id_msg.isNotEmpty()&&pw_msg.isNotEmpty()) {
                    binding.login.isEnabled = true
                    binding.login.setAlpha(1f)
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                id_msg = binding.id.getText().toString()
                pw_msg = binding.pw.getText().toString()
                if(id_msg.isNotEmpty()&&pw_msg.isNotEmpty()) {
                    binding.login.isEnabled = true
                    binding.login.setAlpha(1f)
                }
                else{
                    binding.login.isEnabled = false
                    binding.login.setAlpha(0.5f)
                }
            }
        })


        // 로그인 버튼 클릭 시 이메일 주소와 비밀번호를 가져와 유효성을 검사한 후 로그인
        binding.login.setOnClickListener {
            val email = binding.id.text.toString()
            val password = binding.pw.text.toString()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공
                        Log.d("로그인", "성공")
                        Log.d("로그인한 이메일", email)
                        Log.d("로그인한 비번", password)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "로그인에 실패했습니다.\n이메일 및 비밀번호를 다시 확인해주세요.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        // 아이디 찾기 버튼 클릭 시
        binding.findEmail.setOnClickListener {
            val intent = Intent(this, FindEmailActivity::class.java)
            startActivity(intent)
        }

        //비밀번호 찾기 버튼 클릭 시
        binding.findPw.setOnClickListener {
            val intent = Intent(this, FindPwActivity::class.java)
            startActivity(intent)
        }

        //회원가입 버튼 클릭 시
        binding.join.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        //자동로그인 버튼 클릭
        binding.autoLoginBtn.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch){
                Log.d("switch","on")
                PreferApplication.prefer.setString("switch","on")
            }
            else{
                Log.d("switch","off")
                PreferApplication.prefer.setString("switch","off")
            }
        }
    }
    private fun btnAutoInit(){
        PreferApplication.prefer.setString("switch","off")
    }
}
