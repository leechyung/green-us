package kr.ac.kpu.green_us.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import kr.ac.kpu.green_us.MainActivity
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Participate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyGreenDegreeAdapter(private var participateList: List<Participate>) :
    RecyclerView.Adapter<MyGreenDegreeAdapter.MyGreenDegreeViewHolder>() {

    inner class MyGreenDegreeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.greening_title_pro) // 그리닝명
        var percentage: TextView = view.findViewById(R.id.greening_percentage) // 그리닝 진행률 %
        var progressbar: LinearProgressIndicator =
            view.findViewById(R.id.linearProgressIndicator) // 그리닝 프로그레스바
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyGreenDegreeViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_green_degree, parent, false)

        return MyGreenDegreeViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: MyGreenDegreeViewHolder, position: Int) {
        val participate = participateList.getOrNull(position)

        Log.d("MyGreenDegreeAdapter", "Binding position $position: participate = $participate")

        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getParticipateByPId(participate!!.pSeq).enqueue(object :
            Callback<Greening> {
            override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                if (response.isSuccessful) {
                    var greening = response.body()
                    holder.title.text = greening!!.gName
                    val percentage = participate.pCount?.toDouble()?.div(greening.gNumber ?: 1)?.times(100) ?: 0.0
                    holder.percentage.text = "${percentage.toInt()}%"
                    holder.progressbar.progress = percentage.toInt()
                } else {
                    Log.e("MyGreenDegreeAdapter", "Greening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Greening>, t: Throwable) {
                Log.e("MyGreenDegreeAdapter", "서버 통신 중 오류 발생", t)
            }
        })
    }

    override fun getItemCount(): Int {
        return participateList.size
    }

    fun updateData(newList1: List<Participate>) {
        participateList = newList1
        notifyDataSetChanged()
    }
}