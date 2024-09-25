package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Participate
import kr.ac.kpu.green_us.common.dto.Payment
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.ActivityGreeningDetailBinding
import kr.co.bootpay.android.Bootpay
import kr.co.bootpay.android.events.BootpayEventListener
import kr.co.bootpay.android.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Types.NULL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GreeningDetailActivity : AppCompatActivity() {
    private  lateinit var binding : ActivityGreeningDetailBinding
    lateinit var auth: FirebaseAuth
    var user: User? = null
    val applicationId = BuildConfig.PAYMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreeningDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkWarningCounts()

        val gSeq: Int = intent.getIntExtra("gSeq", -1)

        if(gSeq <= -1){
            //gSeq조회 실패한 경우 예외처리 -> 로그아웃하고 초기화면으로
            Log.e("GreeningDetailActivity","gSeq 실패")
        }else{
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getGreeningById(gSeq).enqueue(object : Callback<Greening> {
                override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                    if (response.isSuccessful) {
                        val greening = response.body() ?: null
                        if(greening != null){
                            val greenWeek = if (greening.gFreq != 0 && greening.gNumber != 0) {
                                greening.gNumber!! / greening.gFreq!!
                            } else { 0 }

                            // greening.gStartDate는 "yyyy-MM-dd" 형식의 문자열
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val startDate = LocalDate.parse(greening.gStartDate, formatter)
                            // 이미지 로드
                            val gseq = gSeq.toString()
                            val imgName = gseq
                            val storage = Firebase.storage
                            if ((greening.gKind == 1).or(greening.gKind == 2)){
                                binding.imgWhenOfficial.isGone = false
                                val ref = storage.getReference("officialGreeningImgs/").child(imgName)
                                ref.downloadUrl.addOnSuccessListener {
                                        uri -> Glide.with(this@GreeningDetailActivity).load(uri).into(binding.imgGreening)
                                }
                                val detailRef = storage.getReference("officialGreeningImgs/content").child(imgName)
                                detailRef.downloadUrl.addOnSuccessListener {
                                    uri-> Glide.with(this@GreeningDetailActivity).load(uri).into(binding.imgWhenOfficial)
                                }
                            }else{
                                val ref = storage.getReference("greeningImgs/").child(imgName)
                                ref.downloadUrl.addOnSuccessListener {
                                        uri -> Glide.with(this@GreeningDetailActivity).load(uri).into(binding.imgGreening)
                                }

                            }

                            binding.barTitle.text = greening.gName ?: ""
                            binding.greeningTitle.text = greening.gName ?: ""
                            binding.tagTerm.text = "${greenWeek}주"
                            binding.tagFreq.text = "주${greening.gFreq}회"
                            binding.tagCertifi.text = greening.gCertiWay
                            binding.tvStartDate.text =
                                "${startDate.monthValue}월 ${startDate.dayOfMonth}일부터 시작"
                            binding.tvParticipateFee.text = "${greening.gDeposit}"
                            val rawText = greening.gInfo.toString()
                            val replacedText = rawText.replace("<br>","\n")
                            binding.tvHowto.text = replacedText
                            binding.textView10.text = when(greening.gKind){
                                1,3 -> "활동형" //1->공식 3->회원
                                2,4 -> "구매형" //2->공식 4->회원
                                else -> ""
                            }

                            //참여하기 버튼
                            binding.button.setOnClickListener {
                                getUserByEmail { user ->
                                    if (user != null) {
                                        val participate = Participate(user = user, greening = greening)
                                        val apiService =
                                            RetrofitManager.retrofit.create(RetrofitAPI::class.java)

                                        apiService.findpSeqByUserSeqAndgSeq(user.userSeq, greening.gSeq).enqueue(object :
                                            Callback<Int> {
                                            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                                                if (response.isSuccessful) {
                                                    Log.e("GreeningDetailActivity", "Participate 데이터 로딩: ${response.body()}")
                                                    if (response.body() == null) {
                                                        paymentTest(it, greening.gDeposit?.toDouble() ?: 0.0, greening.gName ?: "그리닝 활동", user, greening)
                                                    }
                                                    else{
                                                        Toast.makeText(this@GreeningDetailActivity, "이미 참여한 그리닝입니다.", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Log.e("GreeningDetailActivity", "Participate 데이터 로딩 실패: ${response.code()}")
                                                }
                                            }

                                            override fun onFailure(call: Call<Int>, t: Throwable) {
                                                Log.e("GreeningDetailActivity", "서버 통신 중 오류 발생", t)
                                            }
                                        })
                                    }
                                }
                            }

                        }
                    } else {
                        Log.e("GreeningDetailActivity", "Greening 데이터 로딩 실패: ${response.code()}")
                    }
                }

                override fun onFailure(p0: Call<Greening>, p1: Throwable) {
                    Log.e("GreeningDetailActivity", "서버 통신 중 오류 발생", p1)
                }
            })
        }
        binding.btnEsc.setOnClickListener {
            finish()
        }
    }

    private fun getUserByEmail(callback: (User?)->Unit) {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email

        //로그인중인 email에 해당하는 user 가져오는 코드
        if(currentEmail != null){
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    }else{
                        Log.e("GreeningDetailActivity", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                        callback(null)
                        //실패 처리 로직
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("GreeningDetailActivity", "서버 통신 중 오류 발생", t)
                    callback(null)
                    // 실패 처리 로직
                }
            })
        }else{
            Log.e("GreeningOpenFragment", "로그인된 이메일을 가져올 수 없습니다.")
            callback(null)
            //로그인된 Email을 못가져온 경우
            //로그아웃 시키고 처음 화면으로 가도록
        }
    }
    private fun checkWarningCounts(){
        auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email.toString()
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        retrofitAPI.getUserWCountByUserEmail(userEmail).enqueue(object :Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    val waringCounts = response.body()
                    Log.d("MypageFragment","신고당한횟수: $waringCounts ")

                    if (waringCounts != null) {
                        if (waringCounts >= 5){
                            binding.button.isEnabled = false
                            binding.button.alpha = 0.5f
                            binding.button.text = "그리닝 참여 제한됨"
                        }else{
                            binding.button.isEnabled = true
                            binding.button.alpha = 1f
                            binding.button.text = "그리닝 참여하기"
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MypageFragment", "API 호출 실패: ${t.message}")
            }
        })
    }



    fun paymentTest(v: View?, price: Double, orderName: String, user1: User, greening: Greening) {
        val extra = BootExtra()
            .setCardQuota("0,2,3") // 일시불, 2개월, 3개월 할부 허용

        val items: MutableList<BootItem> = ArrayList()
        val item1 = BootItem().setName("그리닝 활동").setId("ITEM_CODE_GREENING").setQty(1).setPrice(price)
        items.add(item1)

        val user = BootUser().setPhone("010-1234-5678")
        val payload = Payload()
        val pg = "이니시스"
        val method = "카드"

        payload.setApplicationId(applicationId)
            .setOrderName(orderName)
            .setPg(pg)
            .setMethod(method)
            .setOrderId("1234")
            .setPrice(price) // 여기에 결제 금액을 설정합니다.
            .setUser(user)
            .setExtra(extra).items = items

        Bootpay.init(supportFragmentManager, applicationContext)
            .setPayload(payload)
            .setEventListener(object : BootpayEventListener {
                override fun onCancel(data: String) {
                    Log.d("bootpay", "cancel: $data")
                }

                override fun onError(data: String) {
                    Log.d("bootpay", "error: $data")
                }

                override fun onClose() {
                    Bootpay.removePaymentWindow()
                }

                override fun onIssued(data: String) {
                    Log.d("bootpay", "issued: $data")
                }

                override fun onConfirm(data: String): Boolean {
                    Log.d("bootpay", "confirm: $data")
                    return true
                }

                override fun onDone(data: String) {
                    Log.d("done", data)
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    // 결제 완료 후 Payment 객체 생성
                    val payment = Payment(
                        user = user1,
                        paymentContent = orderName,
                        paymentMethod = "카드",
                        paymentDate = LocalDate.now().toString(),
                        paymentMoney = price.toInt()
                    )
                    Log.d("PaymentTest", "Payment Object: $payment")

                    // Retrofit API 호출
                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                    apiService.createPayment(payment).enqueue(object : Callback<Payment> {
                        override fun onResponse(call: Call<Payment>, response: Response<Payment>) {
                            if (response.isSuccessful) {
                                Log.d("Payment", "결제 정보 전송 성공: ${response.body()}")
                            } else {
                                Log.e("Payment", "결제 정보 전송 실패: ${response.code()}, ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<Payment>, t: Throwable) {
                            Log.e("Payment", "서버 통신 중 오류 발생", t)
                        }
                    })
                    val participate = Participate(user = user1, greening = greening)
                    apiService.registerParticipate(participate)
                        .enqueue(object : Callback<Participate> {
                            override fun onResponse(
                                call: Call<Participate>,
                                response: Response<Participate>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("GreeningDetailActivity", "참여 등록 완료 ${response.body()}")
                                    Toast.makeText(application,"${greening.gName}에 참여 완료", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e("GreeningDetailActivity", "그리닝 참여 실패: ${response.code()}, ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(
                                call: Call<Participate>,
                                t: Throwable
                            ) {
                                Log.e("GreeningDetailActivity", "서버 통신 중 오류 발생", t)
                            }
                        })
                }
            }).requestPayment()

    }

}