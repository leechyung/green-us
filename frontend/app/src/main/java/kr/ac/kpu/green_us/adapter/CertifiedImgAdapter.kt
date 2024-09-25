package kr.ac.kpu.green_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.kpu.green_us.R

class CertifiedImgAdapter(private val representImgList: MutableList<String>): RecyclerView.Adapter<CertifiedImgAdapter.CertifiedImgViewHolder>(){

    // 이미지 클릭 위한 인터페이스 지정
    interface OnItemClickListener {
        fun onItemClick(url:String){}
    }
    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CertifiedImgViewHolder {
        // view 생성
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_certified_img, parent, false)

        return CertifiedImgViewHolder(view)
    }

    override fun onBindViewHolder(holder: CertifiedImgViewHolder, position: Int) {
        // 인증이미지 스토리지에 있는 사진들을 불러와 붙임
        Glide.with(holder.itemView.context).load(representImgList[position]).into(holder.certifiedImg)

    }

    override fun getItemCount(): Int {
        return representImgList.count()
    }

    inner class CertifiedImgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var certifiedImg: ImageView = itemView.findViewById(R.id.imageView4)

        init{
            certifiedImg.setOnClickListener{ itemClickListener?.onItemClick(representImgList[position]) }
        }

    }



}