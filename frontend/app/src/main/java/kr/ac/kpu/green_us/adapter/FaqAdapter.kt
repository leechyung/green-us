package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.data.FaqData


class FaqAdapter(private var faqList: List<FaqData>) : RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val q_content: TextView = itemView.findViewById(R.id.q_content)
        val btnDown: ImageView = itemView.findViewById(R.id.btn_down)
        val a_content: TextView = itemView.findViewById(R.id.a_content)
        val question: LinearLayout = itemView.findViewById(R.id.question)
        val answer: LinearLayout = itemView.findViewById(R.id.answer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_faq, parent, false)
        return MyViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val faq = faqList[position]
        holder.q_content.text = faq.q_content
        holder.a_content.text = faq.a_content


        // 열려있는 답변 닫음
        if (holder.answer.visibility == View.VISIBLE) {
            holder.btnDown.setImageResource(R.drawable.btn_down)
            holder.answer.visibility = View.GONE
        }

        holder.btnDown.setOnClickListener {
            if (holder.answer.visibility == View.VISIBLE) {
                holder.btnDown.setImageResource(R.drawable.btn_down)
                holder.answer.visibility = View.GONE
            } else {
                holder.btnDown.setImageResource(R.drawable.btn_up)
                holder.answer.visibility = View.VISIBLE
                if(holder.answer.visibility == View.VISIBLE){
                    holder.answer.requestFocus()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    fun updateData(newList: List<FaqData>) {
        faqList = newList
        notifyDataSetChanged()
    }
}