package kr.ac.kpu.green_us.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.data.CertifiedImgs

@SuppressLint("NotifyDataSetChanged")
//private val representImgList: MutableList<String>
class CertifiedRepresentAdapter (private val representImgList: MutableList<String>) : RecyclerView.Adapter<CertifiedRepresentAdapter.CertifiedRepresentViewHolder>(){
    // 이미지 클릭 위한 인터페이스 지정
    interface OnItemClickListener {
        fun onItemClick(url:String){} // 클릭한 이미지의 url 넘겨준다
    }
    var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CertifiedRepresentViewHolder {
        // view 생성
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.certified_img_represent, parent, false)

        return CertifiedRepresentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CertifiedRepresentViewHolder, position: Int) {
        // 매개변수로 받은 url 리스트를 인덱스순으로 뷰에 붙임
        Glide.with(holder.itemView.context).load(representImgList[position]).into(holder.imgs)
    }

    override fun getItemCount(): Int {
        return representImgList.count()
    }

    inner class CertifiedRepresentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgs: ImageView = itemView.findViewById(R.id.represent_img)
        init{
            // 해당 이미지의 url값을 보냄
            itemView.setOnClickListener{ itemClickListener?.onItemClick(representImgList[position]) }
        }



    }



}