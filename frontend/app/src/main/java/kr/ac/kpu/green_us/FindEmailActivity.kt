package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.OpenForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.ActivityFindEmailBinding
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.TimeUnit

// 이메일 찾기 - 휴대전화번호 입력
class FindEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindEmailBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId =""
    private lateinit var phone_num:String
    private lateinit var userEmail:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인스턴스 초기화
        auth = Firebase.auth
        // reCAPTCHA로 강제 적용
//        auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)

        // 인증번호 받기 버튼 비활성화
        binding.btn.isEnabled = false
        binding.btn.setAlpha(0.5f)

        // 뒤로가기 버튼 클릭 시 로그인 화면으로
        binding.btnEsc.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // 휴대전화 입력 전까지 인증 번호 발송 버튼 비활성화
        binding.phone.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val num = binding.phone.getText().toString().trim()
                if((num.length>1).and(num.length<=11)) {
                    binding.btn.isEnabled = true
                    binding.btn.setAlpha(1f)
                }else{
                    binding.btn.isEnabled = false
                    binding.btn.setAlpha(0.5f)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.warn.isVisible = false // 경고문구 초기화
                val num = binding.phone.getText().toString().trim()
                if((num.length>1).and(num.length<=11)) {
                    binding.btn.isEnabled = true
                    binding.btn.setAlpha(1f)
                }else{
                    binding.btn.isEnabled = false
                    binding.btn.setAlpha(0.5f)
                }
            }
        })

        binding.btn.setOnClickListener {
            if (binding.btn.text == "인증번호 받기"){
                phone_num = binding.phone.text.toString().trim()
                checkPhone() // retrofit2 통신후 뷰 처리
                binding.numCertifi.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                    override fun afterTextChanged(p0: Editable?) {
                        val num = binding.numCertifi.getText().toString().trim()
                        if(num.length < 1) {
                            binding.btn.isEnabled = false
                            binding.btn.setAlpha(0.5f)
                        }else{
                            binding.btn.isEnabled = true
                            binding.btn.setAlpha(1f)
                        }
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        binding.warnCertifi.isGone = true
                        val num = binding.numCertifi.getText().toString().trim()
                        if(num.length < 1) {
                            binding.btn.isEnabled = false
                            binding.btn.setAlpha(0.5f)
                        }else{
                            binding.btn.isEnabled = true
                            binding.btn.setAlpha(1f)
                        }
                    }
                })
            }else if (binding.btn.text == "인증번호 확인"){
                // 인증이 유효한지 확인
                val codeInput = binding.numCertifi.text.toString().trim()
                val credential = PhoneAuthProvider.getCredential(verificationId, codeInput)
                signInWithPhoneAuthCredential(credential)
            }else if (binding.btn.text == "재요청"){
                binding.btn.text = "인증번호 확인"
                binding.warnCertifi.isGone = false
                binding.numCertifi.setText(null)
                binding.numCertifi.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                    override fun afterTextChanged(p0: Editable?) {
                        val num = binding.numCertifi.getText().toString().trim()
                        if(num.length < 1) {
                            binding.btn.isEnabled = false
                            binding.btn.setAlpha(0.5f)
                        }else{
                            binding.btn.isEnabled = true
                            binding.btn.setAlpha(1f)
                        }
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        binding.warnCertifi.isGone = true
                        val num = binding.numCertifi.getText().toString().trim()
                        if(num.length < 1) {
                            binding.btn.isEnabled = false
                            binding.btn.setAlpha(0.5f)
                        }else{
                            binding.btn.isEnabled = true
                            binding.btn.setAlpha(1f)
                        }
                    }
                })
            }
        }
    }
    private fun checkPhone(){
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getUserByPhone(this.phone_num).enqueue(object :retrofit2.Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful){ // 해당 전화번호로 조회(디비에 해당번호가 있으면 USER반환하는) 성공하면
                    val user = response.body()
                    if (user != null){
                        userEmail = user.userEmail.toString()
                        if (user.userPhone?.isNotEmpty() == true){
                            Log.d("FindEmailActivity","찾은 전화번호: "+user.userPhone.toString())
                            /// 인증 번호 입력전까지 다시 비활성화
                            binding.btn.isEnabled = false
                            binding.btn.setAlpha(0.5f)
                            /// 버튼 텍스트 바꿈
                            binding.btn.text = "인증번호 확인"
                            /// 인증번호 입력칸 띄우기
                            binding.layoutCertificationForm.isVisible = true
                            // 뷰셋팅끝나면 인증번호 발송
                            sendCode()
                        }else{
                            Log.d("FindEmailActivity","찾은 전화번호: "+user.userPhone.toString())
                            binding.warn.isGone = false
                        }
                    }else{
                        Log.d("FindEmailActivity","반환 User: null")
                        binding.warn.isGone = false
                    }
                }else{
                    Log.e("FindEmailActivity", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    binding.warn.isGone = false
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("FindEmailActivity", "서버 통신 중 오류 발생")
                Toast.makeText(this@FindEmailActivity,"통신 오류로 인해 사용자를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun sendCode(){
// 번호를 국제코드로 변환
        val editedNum = replacePhoneNum(phone_num)
        // 콜백 정의
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
            override fun onVerificationFailed(e: FirebaseException) {
            }
            //인증번호를 발송하면 verificationId값을 저장합니다 (인증코드)
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@FindEmailActivity.verificationId = verificationId
            }
        }
        // 인증번호 전송에 대한 코드
        val optionsCompat =  PhoneAuthOptions.newBuilder(auth)
//               .setPhoneNumber(editedNum) //실제 작동하는 코드입니다
            .setPhoneNumber("+821020192024") //실제 실행 환경에선 인증코드 발송 횟수가 정해져 있어서 테스트 시에는 이 번호로 테스트해주세요 인증번호는 789078입니다
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
        auth.setLanguageCode("kr") // sms 언어를 한국어로 정의
    }
    //휴대폰을 국제코드로 변환하는 함수
    fun replacePhoneNum(phoneNumber : String) : String{
        val firstNumber : String = phoneNumber.substring(0,3)
        var lastNumber = phoneNumber.substring(3)

        when(firstNumber){
            "010" -> lastNumber = "+8210$lastNumber"
            "011" -> lastNumber = "+8211$lastNumber"
        }
        Log.d("국가코드로 변경된 번호 ",lastNumber)
        return lastNumber
    }
    //인증여부 확인
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //인증성공
                    Log.d("phoneNumCheck","success")
                    val intent = Intent(this, FindEmailCompltActivity::class.java)
                    intent.putExtra("userEmail",userEmail)
                    startActivity(intent)
                    this.finishAffinity()
                }
                else {
                    //인증실패 (잘못된 인증번호일 경우)
                    binding.warnCertifi.isVisible = true
                    binding.btn.text = "재요청"
                    binding.btn.isEnabled = true
                    binding.btn.setAlpha(1f)
                }
            }
    }
}