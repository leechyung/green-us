package kr.ac.kpu.green_us

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.ac.kpu.green_us.databinding.DialogReportBinding
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.findViewTreeViewModelStoreOwner

interface ReportDialogInterface {
    fun ontYesButton()
}
class ReportDialog(reportDialogInterface: ReportDialogInterface,viewType:String):DialogFragment() {
    // 뷰 바인딩 정의
    private var _binding: DialogReportBinding? = null
    private val binding get() = _binding!!
    private var reportDialogInterface: ReportDialogInterface? = null
    var type = viewType

    init {
        this.reportDialogInterface = reportDialogInterface
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReportBinding.inflate(inflater, container, false)
        val view = binding.root

        // 다이얼로그 배경 투명화
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 타입에 따른 뷰 변화
        if (type == "report"){ // 신고일 경우
            binding.tvContent.text = "이 사진을 정말 신고하시겠습니까?"
            binding.btnYes.text ="신고"
        }else if (type == "quit") {
            binding.tvContent.text = "정말 탈퇴하시겠습니까?\n탈퇴시 모든 정보는\n삭제됩니다."
            binding.btnYes.text = "탈퇴"
        }

        // 취소 버튼 클릭
        binding.btnCancle.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.btnYes.setOnClickListener {
            this.reportDialogInterface?.ontYesButton()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}