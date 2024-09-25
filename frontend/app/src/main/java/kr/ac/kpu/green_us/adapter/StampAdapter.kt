package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.dto.Certify
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StampAdapter(val list: List<Certify>):RecyclerView.Adapter<StampAdapter.StampHolder>() {
    override fun getItemCount(): Int {
        // return 스탬프 인증횟수만큼
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_stamps,parent,false)

        return StampHolder(view)
    }
    inner class StampHolder(view: View): RecyclerView.ViewHolder(view){
        var stampDate : TextView = view.findViewById(R.id.stamp_date)
    }
    override fun onBindViewHolder(holder: StampAdapter.StampHolder, position: Int) {
        val time = formatCertifyDate(list[position].certifyDate.toString())
        holder.stampDate.text = time
    }
    fun formatCertifyDate(dateString: String): String {
        // 날짜 문자열을 LocalDateTime으로 파싱
        val localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)

        // yyyy-MM-dd 형식으로 포맷팅
        val formatter = DateTimeFormatter.ofPattern("MM.dd")
        return localDateTime.format(formatter)
    }
}