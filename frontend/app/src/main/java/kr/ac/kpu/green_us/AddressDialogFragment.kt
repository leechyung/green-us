package kr.ac.kpu.green_us

import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import org.json.JSONObject

class AddressDialogFragment : DialogFragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address_search, container, false)
        webView = view.findViewById(R.id.webView)
        setupWebView()
////        webView.loadUrl("http://192.168.219.105:8080/address") //여기 주소 변경 필요
        webView.loadUrl("http://192.168.25.8:8080/address") //여기 주소 변경 필요
////        webView.loadUrl("http://172.30.1.11:8080/address") //여기 주소 변경 필요
//        webView.loadUrl("http://192.168.219.107:8080/address") //세진
        return view
    }

    private fun setupWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(WebAppInterface(), "Android")
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {
            val addressData = JSONObject(data)
            val address = addressData.getString("address")

            val bundle = Bundle()
            bundle.putString("address", address)
            parentFragmentManager.setFragmentResult("addressData", bundle)

            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // Dialog 크기 조절
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1),
            (resources.displayMetrics.heightPixels * 0.8).toInt()  // 세로 크기
        )
        dialog?.window?.setGravity(Gravity.CENTER)
    }
}