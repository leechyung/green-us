package kr.ac.kpu.green_us

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kr.ac.kpu.green_us.adapter.*
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.databinding.FragmentTabOfHomeBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext


class TabOfHomeFragment : Fragment() {
    lateinit var binding: FragmentTabOfHomeBinding
    private lateinit var homeDoAdapter: HomeDoAdapter
    private lateinit var homeBuyAdapter: HomeBuyAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var total_banner_num = 0 // 배너 전체 개수
    private var current_banner_position = Int.MAX_VALUE/2  // 무한스크롤처럼 좌우로 스크롤 가능하도록 중간지점으로 세팅함
    private var representImgList  = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabOfHomeBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        //구매형 및 활동형 RecyclerView 설정
        setupRecyclerViews()

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
                }.addOnSuccessListener {
                    total_banner_num = representImgList.size
                    viewAdapter = HeroAdapter(representImgList)
                    viewAdapter.notifyDataSetChanged()
                    (viewAdapter as HeroAdapter).itemClickListener = object : HeroAdapter.OnItemClickListener{
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
                    binding.heroSection.adapter = viewAdapter
                    binding.heroSection.orientation = ViewPager2.ORIENTATION_HORIZONTAL //가로 스크롤
                    binding.heroSection.currentItem =  current_banner_position
                    binding.totalBannerNum.text = total_banner_num.toString()// 전체 배너(이미지) 개수 세팅
                    val showCurrentNum = (current_banner_position%total_banner_num)+1
                    binding.currentBannerNum.text = showCurrentNum.toString() // 현재 이미지 순서 세팅

                    // 이미지 위치에 따라 현재 위치 숫자를 변경함
                    binding.heroSection.apply {
                        registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                val posiText = "${(position%total_banner_num)+1}"
                                binding.currentBannerNum.text = posiText
                            }
                        })
                    }
                }
            }
        }

        // 빠른 접근을 위한 버튼들 클릭 구현
        // 만보기 버튼
        binding.btnManbo.setOnClickListener {
            val intent = Intent(requireActivity(), SubActivity::class.java)
            intent.putExtra("8","pedometer")
            startActivity(intent)
        }
        // 개설하기 버튼
        binding.btnOpen.setOnClickListener {
            val intent = Intent(getActivity(), SubActivity::class.java)
            intent.putExtra("2","open_green")
            startActivity(intent)
        }
        // 내주변 버튼
        binding.btnNearMarket.setOnClickListener {
            val intent = Intent(requireActivity(), MapActivity::class.java)
            startActivity(intent)
        }
        // 히어로 섹션 전체보기
        binding.tvSeeAll.setOnClickListener {
            val intent = Intent(requireActivity(), SubActivity::class.java)
            intent.putExtra("13","hero_list")
            startActivity(intent)
        }

        //데이터 로딩
        loadGreeningData()

    }


    private fun setupRecyclerViews(){
        // 구매형 RecyclerView 설정
        viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        homeBuyAdapter = HomeBuyAdapter(emptyList())
        recyclerView=binding.recyclerviewIngGreening.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = homeBuyAdapter
        }

        homeBuyAdapter.itemClickListener = object : HomeBuyAdapter.OnItemClickListener{
            override fun onItemClick(gSeq:Int) {
                val user = Firebase.auth.currentUser
                if(user != null){
                    val email = user.email?:""
                    Log.d("TabOfNewFragment","$email")
                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                    apiService.findPSeqByGSeqAndUserEmail(email,gSeq).enqueue(object : Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            if (response.isSuccessful) {
                                val pSeq = response.body()?:-1
                                Log.d("TabOfHomeFragment", "pSeq : ${pSeq}")
                                if (pSeq >= 0) {
                                    Log.d("TabOfHomeFragment", "in")
                                    val intent = Intent(requireActivity(),CertifyGreeningActivity::class.java)
                                    intent.putExtra("status","in")
                                    intent.putExtra("gSeq", gSeq)
                                    startActivity(intent)
                                }else{
                                    Log.d("TabOfHomeFragment", "notIn")
                                    val intent = Intent(requireActivity(),GreeningDetailActivity::class.java)
                                    intent.putExtra("status","notIn")
                                    intent.putExtra("gSeq", gSeq)
                                    startActivity(intent)
                                }
                            } else {
                                Log.e("TabOfHomeFragment", "Participate 데이터 로딩 실패: ${response.code()}")
                                Toast.makeText(context, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
                                //오류 처리
                            }
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.e("TabOfHomeFragment", "서버 통신 중 오류 발생", t)
                            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                            startActivity(intent)
                        }
                    })

                }else{
                    Log.d("TabOfHomeFragment", "user null")
                    Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    val intent = Intent(getActivity(), LoginActivity::class.java)
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                    startActivity(intent)
                }
            }

        }

        // 더보기 버튼 클릭 시
        binding.imageView3.setOnClickListener {
            val intent = Intent(getActivity(), SubActivity::class.java)
            intent.putExtra("11","buy_green")
            startActivity(intent)
        }

        // 활동형 RecyclerView 설정
        viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        homeDoAdapter = HomeDoAdapter(emptyList()) // 빈 리스트로 초기화
        recyclerView=binding.recyclerviewIngGreening2.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = homeDoAdapter
        }

        homeDoAdapter.itemClickListener = object : HomeDoAdapter.OnItemClickListener{
            override fun onItemClick(gSeq:Int) {
                val user = Firebase.auth.currentUser
                if(user != null){
                    val email = user.email?:""
                    Log.d("TabOfNewFragment","$email")
                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                    apiService.findPSeqByGSeqAndUserEmail(email,gSeq).enqueue(object : Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            if (response.isSuccessful) {
                                val pSeq = response.body()?:-1
                                Log.d("TabOfHomeFragment", "pSeq : ${pSeq}")
                                if (pSeq >= 0) {
                                    Log.d("TabOfHomeFragment", "in")
                                    val intent = Intent(requireActivity(),CertifyGreeningActivity::class.java)
                                    intent.putExtra("status","in")
                                    intent.putExtra("gSeq", gSeq)
                                    startActivity(intent)
                                }else{
                                    Log.d("TabOfHomeFragment", "notIn")
                                    val intent = Intent(requireActivity(),GreeningDetailActivity::class.java)
                                    intent.putExtra("status","notIn")
                                    intent.putExtra("gSeq", gSeq)
                                    startActivity(intent)
                                }
                            } else {
                                Log.e("TabOfHomeFragment", "Participate 데이터 로딩 실패: ${response.code()}")
                                Toast.makeText(context, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
                                //오류 처리
                            }
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.e("TabOfHomeFragment", "서버 통신 중 오류 발생", t)
                            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                            startActivity(intent)
                        }
                    })

                }else{
                    Log.d("TabOfNewFragment", "user null")
                    Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    val intent = Intent(getActivity(), LoginActivity::class.java)
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                    startActivity(intent)
                }
            }

        }

        // 더보기 버튼 클릭 시
        binding.imageView5.setOnClickListener {
            val intent = Intent(getActivity(), SubActivity::class.java)
            intent.putExtra("12","do_green")
            startActivity(intent)
        }

    }

    private fun loadGreeningData() {
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        val today = LocalDate.now()
        apiService.getDoGreening().enqueue(object : Callback<List<Greening>> {
            override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                if (response.isSuccessful) {
                    val allDoGreeningList = response.body() ?: emptyList()
                    val selectedGreeningList = allDoGreeningList.filter{ greening->
                        try {
                            val startDate = LocalDate.parse(greening.gStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            (startDate.isAfter(today))
                        }catch (e: Exception){
                            false
                        }
                    }.shuffled().take(4) //아직 시작하지 않은 그리닝 중에 무작위로 4개 선택
                    homeDoAdapter.updateData(selectedGreeningList) // 데이터로 어댑터 초기화
                    binding.recyclerviewIngGreening2.adapter = homeDoAdapter
                } else {
                    Log.e("TabOfHomeFragment", "DoGreening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                Log.e("TabOfHomeFragment", "서버 통신 중 오류 발생", t)
            }
        })


        apiService.getBuyGreening().enqueue(object : Callback<List<Greening>> {
            override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                if (response.isSuccessful) {
                    val allBuyGreeningList = response.body() ?: emptyList()
                    val selectedGreeningList = allBuyGreeningList.filter{ greening->
                        try {
                            val startDate = LocalDate.parse(greening.gStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//                            (today.isEqual(startDate) ||startDate.isAfter(today))
                            (startDate.isAfter(today))
                        }catch (e: Exception){
                            false
                        }
                    }.shuffled().take(4) //무작위로 4개 선택
                    homeBuyAdapter.updateData(selectedGreeningList) // 데이터로 어댑터 초기화
                    binding.recyclerviewIngGreening.adapter = homeBuyAdapter
                } else {
                    Log.e("TabOfHomeFragment", "BuyGreening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                Log.e("TabOfHomeFragment", "서버 통신 중 오류 발생", t)
            }
        })

    }




}