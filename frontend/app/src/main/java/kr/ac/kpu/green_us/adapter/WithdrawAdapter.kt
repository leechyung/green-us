package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.dto.Withdraw

class WithdrawAdapter(private var withdrawList: List<Withdraw>) :
    RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_withdraw, parent, false)
        return WithdrawViewHolder(view)
    }

    override fun onBindViewHolder(holder: WithdrawViewHolder, position: Int) {
        val withdraw = withdrawList[position]
        holder.bind(withdraw)
    }

    override fun getItemCount(): Int {
        return withdrawList.size
    }

    // 데이터 업데이트 메서드
    fun updateData(newWithdraws: List<Withdraw>) {
        withdrawList = newWithdraws
        notifyDataSetChanged() // 데이터 변경을 RecyclerView에 알림
    }

    class WithdrawViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountTextView: TextView = itemView.findViewById(R.id.withdraw_amount)
        private val dateTextView: TextView = itemView.findViewById(R.id.withdraw_date)

        fun bind(withdraw: Withdraw) {
            amountTextView.text = withdraw.withdrawAmount.toString()
            dateTextView.text = withdraw.withdrawDate
        }
    }
}
