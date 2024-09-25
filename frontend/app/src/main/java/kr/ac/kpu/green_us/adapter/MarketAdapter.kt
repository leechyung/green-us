package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.data.Market
import kr.ac.kpu.green_us.data.MarketTime
import kr.ac.kpu.green_us.databinding.ItemsMarketBinding

// MarketAdapter(marketList:ArrayList<Int>)
class MarketAdapter(private val marketList:ArrayList<Market>,private val timeList:ArrayList<MarketTime>):RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(url:String)
    }
    var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder { // 레이아웃 붙이기
        val binding = ItemsMarketBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) { // 데이터 붙이기
        holder.bind(marketList[position],timeList[position])
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(marketList[position].link)
        }
    }

    override fun getItemCount(): Int {
        return marketList.size
    }

    inner class MarketViewHolder(val binding: ItemsMarketBinding ) : RecyclerView.ViewHolder(binding.root){ // 데이터 붙일 뷰 가져오기
        fun bind(markeData:Market, timeData: MarketTime){
            binding.marketName.text = markeData.name
            binding.location.text = markeData.location
            binding.time.text = timeData.time
        }
    }
}