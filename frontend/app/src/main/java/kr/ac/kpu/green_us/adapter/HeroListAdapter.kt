package kr.ac.kpu.green_us.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kr.ac.kpu.green_us.R

class HeroListAdapter(bannerList:MutableList<String>): RecyclerView.Adapter<HeroListAdapter.HeroCardHolder>() {
    interface OnItemClickListener {
        fun onItemClick(url:String)
    }
    var itemClickListener: OnItemClickListener? = null

    val itemList = bannerList // 이미지 배열 리스트가 될 것
    class HeroCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemimage: ImageView = itemView.findViewById(R.id.banner)
    }

    // 1. Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HeroListAdapter.HeroCardHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_hero, parent, false)

        return HeroCardHolder(cardView)
    }

    // 2. Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: HeroCardHolder, position: Int) {
        Glide.with(holder.itemView.context).load(itemList[position]).into(holder.itemimage)
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(itemList[position]) }
    }

    // 3. Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return itemList.size
    }

}