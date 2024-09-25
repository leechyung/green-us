package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.adapter.GreeningReviewRecyclerViewAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Review
import kr.ac.kpu.green_us.databinding.ActivityGreeningReviewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

class GreeningReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGreeningReviewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreeningReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                            binding.gseq.text = greening.gName

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
                                        revieweStar = ceil(revieweStar*10) /10
                                        if(revieweStar.isNaN()){
                                            binding.ratingBar2.rating = 0.0f
                                            binding.reviewRating.text = "0 (0)"
                                        }
                                        else{
                                            binding.ratingBar2.rating = revieweStar
                                            binding.reviewRating.text = "${revieweStar.toString()} (${greeningReview.size})"
                                        }
                                        Log.d("GreeningDetailSubActivity", "review: ${revieweStar.toString()}")
                                        greeningReview = greeningReview
                                        setupGreeningReviewRecyclerViews(greeningReview!!)
                                    } else {
                                        Log.e("GreeningDetailSubActivity", "리뷰 불러오기 실패: ${response.code()}, ${response.errorBody()?.string()}")
                                    }
                                }

                                override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                                    Log.e("GreeningDetailSubActivity", "서버 통신 중 오류 발생", t)
                                }
                            })
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

        binding.btnEsc.setOnClickListener {
            finish()
        }
    }

    private fun setupGreeningReviewRecyclerViews(greeningReview: List<Review>) {
        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewAdapter = GreeningReviewRecyclerViewAdapter(greeningReview)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_review).apply {
            setHasFixedSize(true)
            suppressLayout(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }
}