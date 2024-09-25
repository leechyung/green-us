package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.adapter.NoticeAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.dto.Notice
import kr.ac.kpu.green_us.databinding.ActivityNoticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 공지사항 - 공지사항 보여주는 곳
class NoticeActivity:AppCompatActivity() {
    lateinit var binding: ActivityNoticeBinding
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전버튼
        binding.btnEsc.setOnClickListener {
            finish()
        }

        recyclerView = binding.recyclerviewNotice
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 공지사항 데이터 가져오기
        fetchNotices()
    }

    private fun fetchNotices() {
        RetrofitManager.api.getNotices().enqueue(object : Callback<List<Notice>> {
            override fun onResponse(call: Call<List<Notice>>, response: Response<List<Notice>>) {
                if (response.isSuccessful) {
                    val notices = response.body() ?: emptyList()
                    recyclerView.adapter = NoticeAdapter(notices)
                }
            }

            override fun onFailure(call: Call<List<Notice>>, t: Throwable) {
                // 실패 처리
            }
        })
    }
}