package kr.ac.kpu.green_us

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.adapter.FaqAdapter
import kr.ac.kpu.green_us.data.FaqData
import kr.ac.kpu.green_us.databinding.ActivityFaqBinding

// FAQ 정보 나타냄
class FaqActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    val itemList = ArrayList<FaqData>()
    val selectedItemList = ArrayList<FaqData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전버튼
        binding.btnEsc.setOnClickListener {
            finish()
        }

        faqListAdd()

        val size = itemList.size
        for (i: Int in 0 until size) {
            selectedItemList.add(itemList[i])
        }

        // 전체 버튼 클릭 시
        binding.total.setOnClickListener {
            selectedItemList.clear()
            val size = itemList.size
            for (i: Int in 0 until size) {
                selectedItemList.add(itemList[i])
            }
            (viewAdapter as FaqAdapter).updateData(selectedItemList)
        }

        // 포인트 버튼 클릭 시
        binding.point.setOnClickListener {
            selectedItemList.clear()
            val type = itemList.map { it.type }
            val size = itemList.size
            for (i: Int in 0 until size) {
                if (type[i] == 1) {
                    selectedItemList.add(itemList[i])
                    Log.d("FaqActivity", "Faq : ${selectedItemList.toString()}")
                }
            }
            (viewAdapter as FaqAdapter).updateData(selectedItemList)
        }

        // 그리닝 버튼 클릭 시
        binding.greening.setOnClickListener {
            selectedItemList.clear()
            val type = itemList.map { it.type }
            val size = itemList.size
            for (i: Int in 0 until size) {
                if (type[i] == 2) {
                    selectedItemList.add(itemList[i])
                    Log.d("FaqActivity", "Faq : ${selectedItemList.toString()}")
                }
            }
            (viewAdapter as FaqAdapter).updateData(selectedItemList)
        }

        // 인증 버튼 클릭 시
        binding.certification.setOnClickListener {
            selectedItemList.clear()
            val type = itemList.map { it.type }
            val size = itemList.size
            for (i: Int in 0 until size) {
                if (type[i] == 3) {
                    selectedItemList.add(itemList[i])
                    Log.d("FaqActivity", "Faq : ${selectedItemList.toString()}")
                }
            }
            (viewAdapter as FaqAdapter).updateData(selectedItemList)
        }

        // 개설 버튼 클릭 시
        binding.greenOpen.setOnClickListener {
            selectedItemList.clear()
            val type = itemList.map { it.type }
            val size = itemList.size
            for (i: Int in 0 until size) {
                if (type[i] == 4) {
                    selectedItemList.add(itemList[i])
                    Log.d("FaqActivity", "Faq : ${selectedItemList.toString()}")
                }
            }
            (viewAdapter as FaqAdapter).updateData(selectedItemList)
        }

        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewAdapter = FaqAdapter(selectedItemList)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_faq).apply {
            setHasFixedSize(true)
            suppressLayout(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        (viewAdapter as FaqAdapter).updateData(selectedItemList)
    }

    fun faqListAdd() {
        // 포인트
        itemList.add(
            FaqData(
                1,
                "Q. 신고 누적으로 탈퇴한 회원의 포인트는 어떻게 되나요?",
                "A. 신고 5회 누적 1주일 후 회원 삭제를 진행합니다. 기한 안에 자발적으로 포인트 출금할 수 있도록 안내합니다."
            )
        )
        itemList.add(
            FaqData(
                1,
                "Q. 포인트로 그리닝 참여 가능한가요?",
                "A. 포인트로는 그리닝 참여 불가능합니다. 포인트는 출금 후 사용해 주세요."
            )
        )
        itemList.add(
            FaqData(
                1,
                "Q. 포인트는 5,000원 단위로 출금해야 하는 건가요?",
                "A. 포인트는 최소 5,000원부터 천 원 단위로 출금할 수 있습니다."
            )
        )

        // 그리닝
        itemList.add(
            FaqData(
                2,
                "Q. 첫 그리닝에 참여할 수 있는 신규 가입자의 기준이 무엇인가요?",
                "A. 가입일과 상관없이 가입 이후 한 번도 그리닝에 참여하지 않은 가입자를 신규 가입자로 분류합니다."
            )
        )
        itemList.add(
            FaqData(
                2,
                "Q. 그리닝 중도 포기가 가능한가요?",
                "A. 그리닝 중도 포기는 불가능합니다. 그리닝 참여 시 신중하게 고민 후 참여하시기를 바랍니다."
            )
        )
        itemList.add(
            FaqData(
                2,
                "Q. 같은 그리닝에 중복 참여가 가능한가요?",
                "A. 같은 그리닝은 중복 참여가 불가능합니다. 다양한 그리닝이 진행되고 있으니 다른 그리닝에 참여해 보세요."
            )
        )
        itemList.add(
            FaqData(
                2,
                "Q. 그리닝은 언제부터 시작되나요?",
                "A. 각 그리닝 상세 페이지에 표시된 그리닝 시작일부터 시작됩니다."
            )
        )
        itemList.add(
            FaqData(
                2,
                "Q. 최대 예치금 제한이 있나요?",
                "A. 최소 1,000원부터 최대 100,000원까지 예치할 수 있습니다."
            )
        )
        itemList.add(
            FaqData(
                2,
                "Q. 그리닝마다 참여할 수 있는 조건이 따로 정해져 있나요?",
                "A. 그리닝에 참여할 수 있는 조건은 따로 정해져 있지 않습니다. 각자의 여건에 맞춰서 참여해 주세요."
            )
        )

        // 인증
        itemList.add(
            FaqData(
                3,
                "Q. 인증 사진을 잘못 업로드한 경우 수정이 가능한가요?",
                "A. 인증 사진을 업로드 한 이후에 사진 수정은 불가능합니다. 따라서 신중하게 인증 사진을 업로드 해주세요."
            )
        )
        itemList.add(
            FaqData(
                3,
                "Q. 올바르지 않은 인증 사진을 발견했는데 어떻게 해야 하나요?",
                "A. 즐겁고 공정한 그린어스를 위해 올바르지 않은 인증은 여러분으로부터 제보를 받고 있습니다. 신고해 주시면 저희가 검토 후 빠르게 처리해 드리고 있으니 많은 참여 부탁드립니다."
            )
        )
        itemList.add(
            FaqData(
                3,
                "Q. 인증사진은 어떤 방식으로 검열되고 있나요?",
                "A. 그린어스는 신고받은 인증사진들을 일일이 확인하여 검열하는 방식으로 운영하고 있습니다."
            )
        )
        itemList.add(
            FaqData(
                3,
                "Q. 인증에 사용한 사진이 부적절한 경우에는 어떤 조치가 취해지나요?",
                "A. 해당 인증 사진은 삭제되고, 해당 회원에게는 경고가 주어집니다. 경고가 누적 시 그리닝 참가가 제한될 수 있습니다."
            )
        )
        itemList.add(
            FaqData(
                3,
                "Q. 카메라로만 그리닝 인증을 할 수 있나요?",
                "A. 기본적으로 구매형 그리닝의 경우 카메라, 갤러리 둘 다 사용할 수 있고, 활동형 그리닝의 경우 카메라만 사용할 수 있습니다."
            )
        )

        // 개설
        itemList.add(
            FaqData(
                4,
                "Q. 그리닝을 개설할 수 없습니다. 어떻게 하면 개설할 수 있나요?",
                "A. 그린어스는 다른 회원의 그리닝에 3회 이상 참여한 회원에게만 그리닝을 개설할 수 있도록 하고 있습니다."
            )
        )
        itemList.add(
            FaqData(
                4,
                "Q. 그리닝을 개설할 때 정보를 잘못 입력했어요. 수정할 수 있나요?",
                "A. 그리닝 개설 후 그리닝 정보를 수정할 수 없습니다. 개설 시 주의해 주세요."
            )
        )
        itemList.add(
            FaqData(
                4,
                "Q. 그리닝을 개설하였는데 아무도 참여하지 않으면 그 그리닝은 삭제되나요?",
                "A. 아니요, 그리닝은 삭제되지 않습니다. 그린어스는 개개인의 도전을 응원합니다."
            )
        )
    }

}