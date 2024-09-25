package kr.ac.kpu.green_us

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kr.ac.kpu.green_us.adapter.HeroListAdapter
import kr.ac.kpu.green_us.databinding.FragmentHeroSectionListBinding

class HeroSectionListFragment : Fragment() {
    lateinit var binding: FragmentHeroSectionListBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager
    private var representImgList  = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHeroSectionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("heroImgs/")
        // 스토리지 이미지 전체 가져옴
        storageRef.listAll().addOnSuccessListener { listResult ->
            for (img in listResult.items) {
                img.downloadUrl.addOnSuccessListener { uri ->
                    representImgList.add(uri.toString())
                }.addOnSuccessListener { // 이미지 가져와서 리스트에 저장했으면 리싸이클러뷰 어댑터 붙임
                    // hero banner
                    viewManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    viewAdapter = HeroListAdapter(representImgList)
                    viewAdapter.notifyDataSetChanged()
                    // 어댑터에 클릭리스너 붙임
                    (viewAdapter as HeroListAdapter).itemClickListener = object : HeroListAdapter.OnItemClickListener{
                        override fun onItemClick(url:String) {
                            // firebasestore에 저장된 이미지에 맞는 환경부 링크 찾음
                            val db = Firebase.firestore
                            db.collection("heroUrls").whereEqualTo("img", url)
                                .get().addOnSuccessListener {result ->
                                    for (doc in result){ // 찾으면 링크로 이동함
                                        val websiteUrl = doc["web"].toString()
                                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                                        startActivity(intent)
                                    }
                                }.addOnFailureListener { Log.d("websiteUrl", "웹사이트 없음") }
                        }
                    }
                    recyclerView = binding.recyclerviewHero.apply {
                        setHasFixedSize(true)
                        suppressLayout(true)
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                }
            }
        }
    }
}