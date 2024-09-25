package kr.ac.kpu.green_us.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.SubActivity
class HeroAdapter(bannerList:MutableList<String>): RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() { //이미지 리스트 가져오는 어댑터
    val itemList = bannerList // 이미지 배열 리스트가 될 것
//    private val context : Context
//        get() {
//            
//        }

    interface OnItemClickListener {
        fun onItemClick(url:String)
    }
    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val heroImgView = LayoutInflater.from(parent.context).inflate(R.layout.hero_item,parent,false)
        return HeroViewHolder(heroImgView)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(itemList[position%itemList.size]).into(holder.hero_imgs)
        holder.hero_imgs.setOnClickListener { itemClickListener?.onItemClick(itemList[position%itemList.size])
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE // 무한스크롤처럼 좌우로 스크롤 가능하도록 많은 값을 반환하게 함
    }

    inner class HeroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var hero_imgs : ImageView = itemView.findViewById(R.id.hero_imgs)
    }
}