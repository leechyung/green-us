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
class AdvAdapter(bannerList:MutableList<Uri>): RecyclerView.Adapter<AdvAdapter.AdvViewHolder>() {
    val itemList = bannerList // 이미지 배열 리스트가 될 것

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvViewHolder {
        val advView = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_adv,parent,false)
        return AdvViewHolder(advView)
    }

    override fun onBindViewHolder(holder: AdvViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(itemList[position]).into(holder.adv_imgs)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class AdvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var adv_imgs : ImageView = itemView.findViewById(R.id.img_adv)
    }
}