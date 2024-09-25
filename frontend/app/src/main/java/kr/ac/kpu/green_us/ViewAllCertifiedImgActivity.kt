package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import kr.ac.kpu.green_us.adapter.CertifiedImgAdapter
import kr.ac.kpu.green_us.adapter.CertifiedRepresentAdapter
import kr.ac.kpu.green_us.data.CertifiedImgs
import kr.ac.kpu.green_us.databinding.ActivityCertifyGreeningBinding
import kr.ac.kpu.green_us.databinding.ActivityViewAllCertifiedImgBinding

class ViewAllCertifiedImgActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllCertifiedImgBinding
    private val representImgList = mutableListOf<String>()
    private var gSeq: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllCertifiedImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gSeq = intent.getIntExtra("gSeq", -1)

        if (gSeq != -1) {
            // 초기 세팅
            viewInit()
        } else {
            Toast.makeText(this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 이전 버큰 클릭
        binding.btnEsc.setOnClickListener {
            this.finish()
        }
    }

    fun viewInit() {
        val layoutAdapter = CertifiedImgAdapter(representImgList)
        layoutAdapter.notifyDataSetChanged()

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("certificationImgs/${gSeq}/")
        // 스토리지 이미지 전체 가져옴
        storageRef.listAll().addOnSuccessListener { listResult ->
            for (img in listResult.items) {
                img.downloadUrl.addOnSuccessListener { uri ->
                    representImgList.add(uri.toString())
                }.addOnSuccessListener {
                    binding.layoutAllCertifiedImgs.apply {
                        layoutManager = GridLayoutManager(this.context, 3)
                        adapter = layoutAdapter
                        setHasFixedSize(true)
                    }
                }
            }
        }
        layoutAdapter.itemClickListener = object : CertifiedImgAdapter.OnItemClickListener {
            override fun onItemClick(url: String) {
                val intent = Intent(applicationContext, CertificationImgDetailActivity::class.java)
                intent.putExtra("imgUrl", url)
                intent.putExtra("gSeq", gSeq)
                startActivity(intent)

            }
        }
    }
}