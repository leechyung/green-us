package kr.ac.kpu.green_us

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kpu.green_us.adapter.PointAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Prize
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.common.dto.Withdraw
import kr.ac.kpu.green_us.databinding.ActivityPointBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
class PointActivity : AppCompatActivity() {
    lateinit var binding: ActivityPointBinding
    lateinit var recyclerView: RecyclerView
    lateinit var pointAdapter: PointAdapter
    private var allPrizes: List<Prize> = emptyList()
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1 // 기본값은 현재 월
    private var currentBalance: Int = 0

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesToMypage: SharedPreferences
    private var withdraws: List<Withdraw> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 버튼 설정
        binding.btnEsc.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("key3", "mypage")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerview_point)
        recyclerView.layoutManager = LinearLayoutManager(this)
        pointAdapter = PointAdapter(emptyList())
        recyclerView.adapter = pointAdapter

        getUserByEmail { user ->
            user?.let {
                Log.d("PointActivity", "가져온 userSeq: ${it.userSeq}")
                fetchPrizes(it.userSeq)  // userSeq를 fetchPrizes에 전달
            } ?: run {
                Log.e("PointActivity", "사용자 정보를 가져오지 못했습니다.")
            }
        }

        // 출금하기 버튼 설정
        binding.pointWithdraw.setOnClickListener {
            val intent = Intent(this, PointWithdrawActivity::class.java)
            intent.putExtra("currentBalance", currentBalance) // 현재 잔액 전달
            startActivity(intent)
        }
        // 출금 내역 보기 버튼 설정
        findViewById<Button>(R.id.btn_view_withdraws).setOnClickListener {
            val intent = Intent(this, WithdrawsActivity::class.java)
            startActivity(intent)
        }

        // 월 변경 버튼 설정
        binding.btnBack.setOnClickListener {
            if (selectedMonth > 1) {
                selectedMonth--
                updateRecyclerView()
            }
        }

        binding.btnFront.setOnClickListener {
            if (selectedMonth < 12) {
                selectedMonth++
                updateRecyclerView()
            }
        }
        sharedPreferences = getSharedPreferences("point_prefs", Context.MODE_PRIVATE)
        val withdrawAmount = intent.getIntExtra("withdrawAmount", 0)
        Log.d("PointActivity", "받은 출금 금액: $withdrawAmount")
        if (withdrawAmount > 0) {
            getUserByEmail { user ->
                user?.let {
                    fetchPrizes(it.userSeq)  // 출금 금액이 있을 때도 userSeq로 fetchPrizes 호출
                }
            }
        }
    }

    private fun getUserByEmail(callback: (User?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email

        if (currentEmail != null) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        Log.e("GetUserByEmail", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("GetUserByEmail", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            Log.e("GetUserByEmail", "로그인된 이메일을 가져올 수 없습니다.")
            callback(null)
        }
    }
    private fun fetchPrizes(userSeq: Int) {
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)

        retrofitAPI.getPrizeByUserSeq(userSeq).enqueue(object : Callback<List<Prize>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if (response.isSuccessful) {
                    allPrizes = response.body() ?: emptyList()

                    // 포인트 총액 계산
                    val totalPoints = allPrizes.sumOf { it.prizeMoney ?: 0 }
                    Log.d("PointActivity", "포인트 총액: $totalPoints")

                    // 출금 내역을 가져오는 함수 호출
                    fetchWithdraws(userSeq, totalPoints)
                } else {
                    Log.d("PointActivity", "API 응답 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                Log.d("PointActivity", "API 호출 실패: ${t.message}")
            }
        })
    }

    private fun fetchWithdraws(userSeq: Int, totalPoints: Int) {
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        retrofitAPI.getWithdrawByUserSeq(userSeq).enqueue(object : Callback<List<Withdraw>> {
            override fun onResponse(call: Call<List<Withdraw>>, response: Response<List<Withdraw>>) {
                if (response.isSuccessful) {
                    withdraws = response.body() ?: emptyList()

                    // 출금 총액 계산
                    val totalWithdrawAmount = withdraws.sumOf { it.withdrawAmount ?: 0 }
                    Log.d("PointActivity", "출금 총액: $totalWithdrawAmount")

                    // 잔액 계산 (포인트 총액 - 출금 총액)
                    currentBalance = totalPoints - totalWithdrawAmount
                    if (currentBalance < 0) currentBalance = 0

                    // UI 업데이트
                    binding.remainingPoint.text = "$currentBalance"
                    updateRecyclerView()
                    savePointBalance(currentBalance)
                    Log.d("PointActivity", "저장된 돈 보내기 마지막 확인: ${savePointBalance(currentBalance)}")
                } else {
                    Log.d("PointActivity", "출금 데이터 API 응답 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Withdraw>>, t: Throwable) {
                Log.d("PointActivity", "출금 데이터 API 호출 실패: ${t.message}")
            }
        })
    }

    private fun updateRecyclerView() {
        val totalWithdrawAmount = withdraws.sumOf { it.withdrawAmount ?: 0 }
        val totalPoints = allPrizes.sumOf { it.prizeMoney ?: 0 }
        currentBalance = totalPoints - totalWithdrawAmount

        // 잔액이 음수가 되지 않도록 처리
        if (currentBalance < 0) {
            currentBalance = 0
        }

        // 선택된 월에 따른 필터링
        val filteredPrizes = allPrizes.filter {
            val prizeDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.prizeDate ?: "") ?: Date()
            val calendar = Calendar.getInstance().apply { time = prizeDate }
            calendar.get(Calendar.MONTH) + 1 == selectedMonth
        }
        pointAdapter.updateData(filteredPrizes)
        binding.month.text = selectedMonth.toString()
    }
    private fun printSavedPointBalance() {
        sharedPreferencesToMypage = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedBalance = sharedPreferencesToMypage.getInt("point_balance", 0)
        Log.d("PointActivity", "저장된 포인트 잔액: $savedBalance")
    }
    private fun savePointBalance(balance: Int) {
        sharedPreferencesToMypage = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferencesToMypage.edit()
        editor.putInt("point_balance", balance)
        editor.apply()
        printSavedPointBalance()
    }

}
