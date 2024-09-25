package kr.ac.kpu.green_us

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kr.ac.kpu.green_us.databinding.FragmentJoin1Binding
import kr.ac.kpu.green_us.databinding.FragmentJoin2Binding
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class Join2Fragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentJoin2Binding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var pw = ""
    private var phoneNumber = ""
    private var verificationId =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= FragmentJoin2Binding.inflate(inflater,container,false)

        // 인스턴스 초기화
        auth = Firebase.auth
        // reCAPTCHA로 강제 적용
        auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
        // 이전 프래그먼트로부터 온 bundle 데이터 받기
        email = arguments?.getString("email").toString()
        pw = arguments?.getString("pw").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 인증번호 받기 버튼 비활성화
        binding.btnSentCode.isEnabled = false
        binding.btnSentCode.alpha = 0.5f
        // 인증번호 확인 버튼 비활성화
        binding.btnPhoneCheck.isEnabled = false
        binding.btnPhoneCheck.alpha = 0.5f
        // 휴대전화번호 입력후 인증번호 받기 버튼 활성화
        binding.etPhone.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val user_phone = binding.etPhone.text.toString().trim()
                if(user_phone.isNotEmpty()){
                    binding.btnSentCode.isEnabled = true
                    binding.btnSentCode.alpha = 1f
                }
            }
        })
        // 인증번호 입력 후 인증번호 확인 버튼 활성화
        binding.etCodeInput.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val code = binding.etCodeInput.text.toString()
                if(code.isNotEmpty()){
                    binding.btnPhoneCheck.isEnabled = true
                    binding.btnPhoneCheck.alpha = 1f
                }
            }
        })
        // 인증번호 받기 버튼 클릭시
        binding.btnSentCode.setOnClickListener {

            phoneNumber = binding.etPhone.text.toString().trim()
            // 번호를 국제코드로 변환 -> firebase 정책 변경으로 인한 9월1일자로 사용 불가
//            val editedNum = replacePhoneNum(phoneNumber)
            // 콜백 정의
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
                override fun onVerificationFailed(e: FirebaseException) {
                }
                //인증번호를 발송하면 verificationId값을 저장합니다 (인증코드)
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@Join2Fragment.verificationId = verificationId
                }
            }
            // 인증번호 전송에 대한 코드
            val optionsCompat =  PhoneAuthOptions.newBuilder(auth)
//               .setPhoneNumber(editedNum) //실제 작동하는 코드입니다 -> firebase 정책 변경으로 인한 9월1일자로 사용 불가
                .setPhoneNumber("+821020192024")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
            auth.setLanguageCode("kr") // sms 언어를 한국어로 정의

        }
        //인증여부 확인
        binding.btnPhoneCheck.setOnClickListener {
            val codeInput = binding.etCodeInput.text.toString().trim()
            val credential = PhoneAuthProvider.getCredential(verificationId, codeInput)
            signInWithPhoneAuthCredential(credential)
        }
        //이전 프래그먼트로
        binding.btnEsc.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
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
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //인증성공
                    Log.d("phoneNumCheck","success")
                    //이메일,비번,전화번호 데이터를 번들에 담아 다음 프래그먼트로 전송
                    val bundle2 = Bundle()
                    bundle2.putString("email",email)
                    bundle2.putString("pw",pw)
                    bundle2.putString("phone",phoneNumber)
                    val fragmentAddress = JoinAddressFragment()
                    fragmentAddress.arguments = bundle2
                    parentFragmentManager.beginTransaction().replace(R.id.join_container,fragmentAddress).addToBackStack(null).commit()
                }
                else {
                    //인증실패 (잘못된 인증번호일 경우)
                    binding.tvValid.isVisible = true
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}