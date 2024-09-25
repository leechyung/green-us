package kr.ac.kpu.green_us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kpu.green_us.adapter.MyReviewAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Review
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.ActivityMyReviewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 내리뷰 - 리뷰 리스트로 확인, 삭제 가능
class MyReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    lateinit var auth: FirebaseAuth
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        //val gSeq = intent.getStringExtra("gSeq")!!.toInt()
        //setupRecyclerView(mutableListOf()) // 리뷰 작성 -> 내리뷰 이동 시 데이터 업데이트

        // 데이터 가져오기
        getUserByEmail { user ->
            if (user != null) {
                // 1. review 데이터 가져오기
                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                apiService.getReviewByUserSeq(user.userSeq).enqueue(object :
                    Callback<List<Review>> {
                    override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                        if (response.isSuccessful) {
                            var reviewList:MutableList<Review>? = response.body()?.toMutableList() ?: mutableListOf<Review>()
                            reviewList?.forEachIndexed { index, review ->
                                Log.d("MyReviewActivity", "reviewList $index: ${review.toString()}")
                            }
                            setupRecyclerView(reviewList!!)
                        } else {
                            Log.e("MyReviewActivity", "Review 데이터 로딩 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                        Log.e("MyReviewActivity", "서버 통신 중 오류 발생", t)
                    }
                })

            } else {
                Log.d("MyReviewActivity", "사용자의 리뷰를 확인할 수 없음")
            }
        }

        // 이전버튼
        binding.btnEsc.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("key3","mypage")
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@MyReviewActivity, MainActivity::class.java)
            intent.putExtra("key3","mypage")
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView(reviewList: MutableList<Review>) {
        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewAdapter = MyReviewAdapter(reviewList)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_review).apply {
            setHasFixedSize(true)
            suppressLayout(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        (viewAdapter as MyReviewAdapter).updateData(reviewList)
    }

    private fun getUserByEmail(callback: (User?) -> Unit) {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email.toString()
        Log.d("currentEmail", currentEmail)

        if (currentEmail.isNotEmpty()) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        if (user != null) {
                            Log.d("MyReviewActivity", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyReviewActivity", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyReviewActivity", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                    callback(user)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyReviewActivity", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }
}