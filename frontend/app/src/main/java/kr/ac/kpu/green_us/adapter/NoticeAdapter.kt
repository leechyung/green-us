package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.dto.Notice


class NoticeAdapter(private val noticeList: List<Notice>) : RecyclerView.Adapter<NoticeAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val btnDown: ImageView = itemView.findViewById(R.id.btn_down)
        val content: TextView = itemView.findViewById(R.id.content)
        val date: TextView = itemView.findViewById(R.id.date)
        val constraintLayout2: LinearLayout = itemView.findViewById(R.id.constraintLayout2)
        val constraintLayout3: LinearLayout = itemView.findViewById(R.id.constraintLayout3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_notice, parent, false)
        return MyViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notice = noticeList[position]
        holder.title.text = notice.noticeTitle
        holder.content.text = notice.noticeContent
        holder.date.text = notice.noticeDate

        holder.btnDown.setOnClickListener {
            if (holder.constraintLayout3.visibility == View.VISIBLE) {
                holder.btnDown.setImageResource(R.drawable.btn_down)
                holder.constraintLayout3.visibility = View.GONE
            } else {
                holder.btnDown.setImageResource(R.drawable.btn_up)
                holder.constraintLayout3.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }
}