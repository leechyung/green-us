package kr.ac.kpu.green_us

import android.app.Dialog
import android.view.Window
import kr.ac.kpu.green_us.databinding.ActivityDeleteCheckBinding

// 리뷰 삭제 다이얼로그 띄우기
class DeleteCheckActivity(private val context: MyReviewActivity) {
    private lateinit var binding : ActivityDeleteCheckBinding
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감

    private lateinit var listener : MyDialogDeleteClickedListener

    fun show() {
        binding = ActivityDeleteCheckBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(binding.root)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        // 아니요 버튼 동작
        binding.no.setOnClickListener {
            listener.onDeleteClicked(1)
            dlg.dismiss()
        }

        // 예 버튼 동작
        binding.yes.setOnClickListener {
            listener.onDeleteClicked(2)
            dlg.dismiss()
        }

        dlg.show()
    }

    fun setOnDeleteClickedListener(listener: (Int) -> Unit) {
        this.listener = object: MyDialogDeleteClickedListener {
            override fun onDeleteClicked(content: Int) {
                listener(content)
            }
        }
    }

    interface MyDialogDeleteClickedListener {
        fun onDeleteClicked(content : Int)
    }
}