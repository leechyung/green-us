package kr.ac.kpu.green_us


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.adapter.WithdrawAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.common.dto.Withdraw
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth

class WithdrawsActivity : AppCompatActivity() {
    private lateinit var withdrawAdapter: WithdrawAdapter
    private var withdraws: List<Withdraw> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraws)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_withdraws)
        recyclerView.layoutManager = LinearLayoutManager(this)
        withdrawAdapter = WithdrawAdapter(withdraws)
        recyclerView.adapter = withdrawAdapter

        // 사용자 이메일로 userSeq를 가져와서 출금 내역을 요청
        getUserByEmail { user ->
            user?.let {
                fetchWithdraws(it.userSeq)
            } ?: run {
                Log.e("userSeq가져오기", "userSeq가져오기 실패함.")
            }
        }
    }

    private fun fetchWithdraws(userSeq: Int) {
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        retrofitAPI.getWithdrawByUserSeq(userSeq).enqueue(object : Callback<List<Withdraw>> {
            override fun onResponse(call: Call<List<Withdraw>>, response: Response<List<Withdraw>>) {
                if (response.isSuccessful) {
                    withdraws = response.body() ?: emptyList()
                    withdrawAdapter.updateData(withdraws)
                } else {
                }
            }
            override fun onFailure(call: Call<List<Withdraw>>, t: Throwable) {
            }
        })
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