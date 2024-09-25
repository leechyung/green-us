package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.adapter.GreeningReviewAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Review
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.ActivityGreeningDetailSubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

// 간단한 그리닝 정보만 담은 상세페이지 보여줌
class GreeningDetailSubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGreeningDetailSubBinding
    lateinit var auth: FirebaseAuth
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreeningDetailSubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var green_state1 = intent.getStringExtra("end")
        var green_state2 = intent.getStringExtra("open")

        getUserByEmail()

        val gSeq:Int = intent.getIntExtra("gSeq", -1)

        if (gSeq <= -1) {
            //gSeq조회 실패한 경우 예외처리 -> 로그아웃하고 초기화면으로
            Log.e("GreeningDetailSubActivity", "gSeq 실패")
        } else {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getGreeningById(gSeq).enqueue(object : Callback<Greening> {
                override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                    if (response.isSuccessful) {
                        val greening = response.body() ?: null
                        if (greening != null) {
                            val greenWeek = if (greening.gFreq != 0 && greening.gNumber != 0) {
                                greening.gNumber!! / greening.gFreq!!
                            } else {
                                0
                            }

                            // greening.gStartDate는 "yyyy-MM-dd" 형식의 문자열
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val startDate = LocalDate.parse(greening.gStartDate, formatter)


                            // 이미지 로드
                            val gseq = gSeq.toString()
                            val imgName = gseq
                            val storage = Firebase.storage
                            if ((greening.gKind == 1).or(greening.gKind == 2)){
                                val ref = storage.getReference("officialGreeningImgs/").child(imgName)
                                ref.downloadUrl.addOnSuccessListener {
                                        uri -> Glide.with(this@GreeningDetailSubActivity).load(uri).into(binding.imgGreening)
                                }
                            }else{
                                val ref = storage.getReference("greeningImgs/").child(imgName)
                                ref.downloadUrl.addOnSuccessListener {
                                        uri -> Glide.with(this@GreeningDetailSubActivity).load(uri).into(binding.imgGreening)
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
                            binding.textView10.text = when (greening.gKind) {
                                1, 3 -> "활동형" //1->공식 3->회원
                                2, 4 -> "구매형" //2->공식 4->회원
                                else -> ""
                            }

                            if (green_state1 == "end_state") {
                                binding.button.text = "리뷰 작성"
                                // 리뷰 작성 버튼 클릭 시
                                binding.button.setOnClickListener {
                                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                                    apiService.getReviewByUserSeqAndgSeq(user!!.userSeq, gSeq).enqueue(object : Callback<Review> {
                                            override fun onResponse(call: Call<Review>, response: Response<Review>,
                                            ) {
                                                if (response.isSuccessful) {
                                                    Log.d("GreeningDetailSubActivity", "리뷰 불러오기 성공")
                                                    var review = response.body() ?:null
                                                    if(review == null){
                                                        val intent = Intent(
                                                            this@GreeningDetailSubActivity,
                                                            SubActivity::class.java
                                                        )
                                                        intent.putExtra("3", "my_review_write")
                                                        intent.putExtra("gSeq", gSeq)
                                                        startActivity(intent)
                                                    }
                                                    else{
                                                        Toast.makeText(application,"이미 리뷰를 작성했습니다", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Log.e("GreeningDetailSubActivity", "리뷰 불러오기 실패: ${response.code()}, ${response.errorBody()?.string()}")
                                                }
                                            }

                                        override fun onFailure(call: Call<Review>, t: Throwable) {
                                            Log.e("GreeningDetailSubActivity", "서버 통신 중 오류 발생", t)
                                        }
                                    })
                                }
                            } else if (green_state2 == "open_state") {
                                binding.button.visibility = View.GONE
                            }

                            // 그리닝 리뷰 설정
                            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                            apiService.getReviewByGreeningSeq(gSeq).enqueue(object : Callback<List<Review>> {
                                override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>,
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d("GreeningDetailSubActivity", "리뷰 불러오기 성공")
                                        var greeningReview = response.body()
                                        var revieweStar = 0.0f
                                        greeningReview?.forEachIndexed { index, review ->
                                            Log.d("GreeningDetailSubActivity", "reviewList $index: ${review.toString()}")
                                            revieweStar += review.reviewRate!!
                                        }
                                        revieweStar /= greeningReview!!.size.toFloat()
                                        revieweStar = ceil(revieweStar*10)/10
                                        if(revieweStar.isNaN()){
                                            binding.ratingBar2.rating = 0.0f
                                            binding.reviewRating.text = "0 (0)"
                                            binding.moreBtn.visibility = View.GONE
                                        }
                                        else{
                                            binding.ratingBar2.rating = revieweStar
                                            binding.reviewRating.text = "${revieweStar.toString()} (${greeningReview.size})"
                                        }
                                        Log.d("GreeningDetailSubActivity", "review: ${revieweStar.toString()}")
                                        greeningReview = greeningReview?.shuffled()?.take(5)
                                        setupGreeningReviewRecyclerViews(greeningReview!!)
                                    } else {
                                        Log.e("GreeningDetailSubActivity", "리뷰 불러오기 실패: ${response.code()}, ${response.errorBody()?.string()}")
                                    }
                                }

                                override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                                    Log.e("GreeningDetailSubActivity", "서버 통신 중 오류 발생", t)
                                }
                            })

                            /*
                                                        //참여하기 버튼
                                                        binding.button.setOnClickListener {
                                                            getUserByEmail { user ->
                                                                if (user != null) {
                                                                    val participate = Participate(user = user, greening = greening)
                                                                    val apiService =
                                                                        RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                                                                    apiService.registerParticipate(participate)
                                                                        .enqueue(object : Callback<Participate> {
                                                                            override fun onResponse(
                                                                                call: Call<Participate>,
                                                                                response: Response<Participate>
                                                                            ) {
                                                                                if (response.isSuccessful) {
                                                                                    Log.d("GreeningDetailSubActivity", "참여 등록 완료")
                                                                                    Toast.makeText(application,"${greening.gName}에 참여 완료", Toast.LENGTH_SHORT).show()
                                                                                    //메인 화면으로 이동
                                                                                } else {
                                                                                    Log.e("GreeningDetailSubActivity", "그리닝 참여 실패: ${response.code()}, ${response.errorBody()?.string()}")
                                                                                }
                                                                            }

                                                                            override fun onFailure(
                                                                                call: Call<Participate>,
                                                                                t: Throwable
                                                                            ) {
                                                                                Log.e("GreeningDetailSubActivity", "서버 통신 중 오류 발생", t)
                                                                                // 실패 처리 로직
                                                                            }
                                                                        })
                                                                }
                                                            }
                                                        }

                             */
                        }
                    } else {
                        Log.e("GreeningDetailSubActivity", "Greening 데이터 로딩 실패: ${response.code()}")
                    }
                }

                override fun onFailure(p0: Call<Greening>, p1: Throwable) {
                    Log.e("GreeningDetailSubActivity", "서버 통신 중 오류 발생", p1)
                }
            })
        }

        binding.btnMore.setOnClickListener {
            val intent = Intent(this, GreeningReviewActivity::class.java)
            intent.putExtra("gSeq", gSeq)
            startActivity(intent)
        }

        binding.btnEsc.setOnClickListener {
            finish()
        }
    }

    private fun setupGreeningReviewRecyclerViews(greeningReview: List<Review>) {

        binding.recyclerviewThisReview.adapter = GreeningReviewAdapter(greeningReview)
        binding.recyclerviewThisReview.offscreenPageLimit = 4
        binding.recyclerviewThisReview.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position + 1}")
            }
        })
    }

    private fun getUserByEmail() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email

        //로그인중인 email에 해당하는 user 가져오는 코드
        if (currentEmail != null) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                    } else {
                        Log.e(
                            "GreeningDetailSubActivity",
                            "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("GreeningDetailSubActivity", "서버 통신 중 오류 발생", t)
                }
            })
        } else {
            Log.e("GreeningOpenFragment", "로그인된 이메일을 가져올 수 없습니다.")
        }
    }

}