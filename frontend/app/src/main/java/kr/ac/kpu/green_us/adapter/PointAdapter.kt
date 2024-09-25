package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.dto.Prize

class PointAdapter(private var prizes: List<Prize>) : RecyclerView.Adapter<PointAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day: TextView = itemView.findViewById(R.id.day)
        val category: TextView = itemView.findViewById(R.id.category)
        val greeningTitle: TextView = itemView.findViewById(R.id.greening_title)
        val getPoint: TextView = itemView.findViewById(R.id.get_point)
        val totalPoint: TextView = itemView.findViewById(R.id.total_point)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_point, parent, false)
        return MyViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prize = prizes[position]

        val cumulativePoints = prizes.take(position + 1).sumOf { it.prizeMoney ?: 0 }

        holder.day.text = prize.prizeDate ?: "N/A"
        holder.category.text = "획득"
        holder.greeningTitle.text = prize.prizeName ?: ""
        holder.getPoint.text = "+ ${prize.prizeMoney ?: 0}"
        holder.totalPoint.text = "이달의 상금합 $cumulativePoints p"
    }

    override fun getItemCount(): Int = prizes.size

    fun updateData(newPrizes: List<Prize>) {
        prizes = newPrizes
        notifyDataSetChanged()
    }
}
