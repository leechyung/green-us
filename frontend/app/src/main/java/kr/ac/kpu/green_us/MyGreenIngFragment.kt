package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kpu.green_us.adapter.GreenCardAdapter
import kr.ac.kpu.green_us.adapter.MyGreenDegreeAdapter
import kr.ac.kpu.green_us.adapter.MyGreenEndAdapter
import kr.ac.kpu.green_us.adapter.MyGreenIngAdapter
import kr.ac.kpu.green_us.adapter.MyGreenIngMoreAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Participate
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMyGreenIngBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 진행중인 그리닝 - 진행중인 그리닝, 전체 그리닝 진척도, 개별 그리닝 진척도 확인 가능
class MyGreenIngFragment : Fragment() {
    lateinit var binding: FragmentMyGreenIngBinding
    lateinit var recyclerViewIng: RecyclerView
    lateinit var recyclerViewDegree: RecyclerView
    lateinit var viewAdapterIng: RecyclerView.Adapter<*>
    lateinit var viewAdapterDegree: RecyclerView.Adapter<*>
    lateinit var viewManagerIng: RecyclerView.LayoutManager
    lateinit var viewManagerDegree: RecyclerView.LayoutManager

    lateinit var degreeGreeningList: List<Greening>
    lateinit var degreeParticipateList: List<Participate>
    var degreeSize: Int = 0

    lateinit var auth: FirebaseAuth
    var user: User? = null
    val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyGreenIngBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        showNoDataView()

        // 리사이클러뷰 중복 스크롤 막기
        binding.recyclerviewGreenDegree.isNestedScrollingEnabled = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 가져오기
        getUserByEmail { user ->
            if (user != null) {
                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)

                // 1. Greening 데이터 가져오기
                apiService.findGreeningByUserSeq(user.userSeq).enqueue(object :
                    Callback<List<Greening>> {
                    override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                        if (response.isSuccessful) {
                            var greeningList = response.body() ?: emptyList()
                            greeningList = greeningList.filter { greening ->
                                try {
                                    val startDate = LocalDate.parse(greening.gStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    val endDate = LocalDate.parse(greening.gEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    today.isEqual(startDate) || (today.isAfter(startDate) && today.isBefore(endDate))
                                } catch (e: Exception) {
                                    false
                                }
                            }
                            degreeGreeningList = greeningList
                            degreeSize = greeningList.size

                            greeningList.forEachIndexed { index, greening ->
                                Log.d("MyGreenIngFragment", "Greening $index: ${greening.toString()}")
                            }
                            val selectedGreeningList = greeningList.shuffled().take(4)

                            if (selectedGreeningList.isNotEmpty()) {
                                showDataView()
                            }

                            Log.d("MyGreenIngFragment", "Greening Size : ${greeningList.size} -> ${selectedGreeningList.size}")
                            setupGreenIngRecyclerViews(selectedGreeningList)

                            // 2. Participate 데이터 가져오기
                            apiService.getGreeningParticipateByUserSeq(user.userSeq).enqueue(object :
                                Callback<List<Participate>> {
                                override fun onResponse(call: Call<List<Participate>>, response: Response<List<Participate>>) {
                                    if (response.isSuccessful) {
                                        val participateList = response.body() ?: emptyList()
                                        degreeParticipateList = participateList
                                        Log.d("MyGreenIngFragment", "Participate Size : ${participateList.size}")
                                        // 리스트의 모든 항목을 로그로 출력
                                        participateList.forEachIndexed { index, participate ->
                                            Log.d("MyGreenIngFragment", "Participate $index: ${participate.toString()}")
                                        }
                                        setupDegreeRecyclerViews(participateList)

                                        // 전체 진척도 표시
                                        var totalDegree = 0
                                        Log.d("MyGreenIngFragment", "진척도 크기 : $degreeSize")
                                        for(i:Int in 0 until degreeSize){
                                            totalDegree +=
                                                (degreeParticipateList[i].pCount?.toDouble()?.div(degreeGreeningList[i].gNumber!! ?: 1)?.times(100) ?: 0.0).toInt()
                                            Log.d("MyGreenIngFragment", "전체 진척도 크기 : $totalDegree")
                                        }
                                        if(degreeSize!=0){
                                            totalDegree /= degreeSize
                                            binding.totalDegree.progress = totalDegree
                                            binding.totalDegreePercentage.text = "${totalDegree}%"
                                            binding.ingGreeningNum.text = "${degreeSize}"
                                        }else{
                                            binding.totalDegree.progress = 0
                                            binding.totalDegreePercentage.text = "0%"
                                            binding.ingGreeningNum.text = "0"
                                        }
                                    } else {
                                        Log.e("MyGreenDegreeFragment", "Participate 데이터 로딩 실패: ${response.code()}")
                                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(getActivity(),MainActivity::class.java))
                                    }
                                }
                                override fun onFailure(call: Call<List<Participate>>, t: Throwable) {
                                    Log.e("MyGreenDegreeFragment", "서버 통신 중 오류 발생", t)
                                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(getActivity(),MainActivity::class.java))
                                }
                            })
                        } else {
                            Log.e("MyGreenIngFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                            Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(getActivity(),MainActivity::class.java))
                        }
                    }

                    override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                        Log.e("MyGreenIngFragment", "서버 통신 중 오류 발생", t)
                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(getActivity(),MainActivity::class.java))
                    }
                })
            } else {
                showNoDataView()
            }
        }
    }

    private fun getUserByEmail(callback: (User?) -> Unit) {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email.toString()
        Log.d("currentEmail", currentEmail)

        if (currentEmail.isNotEmpty()) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        if (user != null) {
                            Log.d("MyGreenIngFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyGreenIngFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyGreenIngFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                    callback(user)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyGreenIngFragment", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }

    private fun setupGreenIngRecyclerViews(greeningList: List<Greening>) {
        // 진행중인 그리닝 리사이클러뷰 설정
        viewManagerIng = GridLayoutManager(requireContext(), 2)
        viewAdapterIng = MyGreenIngAdapter(emptyList())
        recyclerViewIng = binding.recyclerviewIngGreening.apply {
            setHasFixedSize(true)
            suppressLayout(true)
            layoutManager = viewManagerIng
            adapter = viewAdapterIng
        }

        (viewAdapterIng as MyGreenIngAdapter).itemClickListener = object : MyGreenIngAdapter.OnItemClickListener {
            override fun onItemClick(gSeq: Int) {
                val intent = Intent(requireActivity(), CertifyGreeningActivity::class.java)
                intent.putExtra("status","ing")
                intent.putExtra("gSeq", gSeq)
                startActivity(intent)
            }
        }

        // 더보기 버튼 클릭 시
        binding.btnMore.setOnClickListener {
            val intent = Intent(requireActivity(), SubActivity::class.java)
            intent.putExtra("0", "green_ing_more")
            startActivity(intent)
        }

        (viewAdapterIng as MyGreenIngAdapter).updateData(greeningList)
    }

    private fun setupDegreeRecyclerViews(participateList: List<Participate>) {
        // 그리닝 개별 진척도 리사이클러뷰 설정
        viewManagerDegree = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewAdapterDegree = MyGreenDegreeAdapter(participateList)
        recyclerViewDegree = binding.recyclerviewGreenDegree.apply {
            setHasFixedSize(true)
            layoutManager = viewManagerDegree
            adapter = viewAdapterDegree
        }

        // 데이터 업데이트
        (viewAdapterDegree as MyGreenDegreeAdapter).updateData(participateList)
    }

    private fun showNoDataView() {
        binding.notExistIng.visibility = View.VISIBLE
        binding.recyclerviewIngGreening.visibility = View.GONE
        binding.moreBtn.visibility = View.GONE
        binding.greenDegree.visibility = View.GONE
        binding.recyclerviewGreenDegree.visibility = View.GONE
    }

    private fun showDataView() {
        binding.notExistIng.visibility = View.GONE
        binding.recyclerviewIngGreening.visibility = View.VISIBLE
        binding.moreBtn.visibility = View.VISIBLE
        binding.greenDegree.visibility = View.VISIBLE
        binding.recyclerviewGreenDegree.visibility = View.VISIBLE
    }
}