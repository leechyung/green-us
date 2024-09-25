package kr.ac.kpu.green_us.util

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class FirebaseAuthUtils {
    companion object {
        private lateinit var auth : FirebaseAuth

        fun getUid() : String {
            auth = Firebase.auth
            return auth.currentUser?.uid.toString()
        }
    }
}