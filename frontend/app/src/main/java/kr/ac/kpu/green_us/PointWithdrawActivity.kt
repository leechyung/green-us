package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.common.dto.Withdraw
import kr.ac.kpu.green_us.databinding.ActivityPointWithdrawBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class PointWithdrawActivity : AppCompatActivity() {
    lateinit var binding: ActivityPointWithdrawBinding
    private val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
    private var userSeq: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 버튼 설정
        binding.btnEsc.setOnClickListener {
            finish()
        }

        // 출금 버튼 설정
        binding.complete.setOnClickListener {
            val withdrawAmountStr = binding.withdrawPoint.text.toString()
            if (withdrawAmountStr.isNotEmpty()) {
                val withdrawAmount = withdrawAmountStr.toInt()

                // 현재 잔액을 가져오기
                val currentBalance = intent.getIntExtra("currentBalance", 0)
                if (withdrawAmount <= currentBalance && withdrawAmount > 4999) {
                    getUserByEmail { user ->
                        user?.let {
                            val userSeq = it.userSeq
                            handleWithdrawRequest(userSeq, withdrawAmount)
                        } ?: run {
                            Toast.makeText(this, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "잔액이 부족하거나 출금 금액이 너무 적습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "출금 금액을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleWithdrawRequest(userSeq: Int, amount: Int) {
        val bank = binding.bank.text.toString()
        val accountHolder = binding.accountHolder.text.toString()

        if (bank.isNotEmpty() && accountHolder.isNotEmpty()) {
            val withdraw = Withdraw(
                withdrawSeq = 0,
                user = User(userSeq = userSeq),
                withdrawContent = "출금 요청 - $bank, $accountHolder",
                withdrawDate = LocalDate.now().toString(),
                withdrawAmount = amount
            )

            val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            retrofitAPI.createWithdraw(withdraw).enqueue(object : Callback<Withdraw> {
                override fun onResponse(call: Call<Withdraw>, response: Response<Withdraw>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PointWithdrawActivity, "출금 성공", Toast.LENGTH_SHORT).show()
                        updatePointInActivity(amount)
                    } else {
                        Toast.makeText(this@PointWithdrawActivity, "출금 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Withdraw>, t: Throwable) {
                    Toast.makeText(this@PointWithdrawActivity, "출금 요청 실패", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "모든 값을 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updatePointInActivity(amount: Int) {
        getUserByEmail { user ->
            user?.let {
                val userSeq = it.userSeq
                val intent = Intent(this, PointActivity::class.java).apply {
                    putExtra("withdrawAmount", amount)
                    putExtra("userSeq", userSeq) // userSeq를 전달
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
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
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }
}