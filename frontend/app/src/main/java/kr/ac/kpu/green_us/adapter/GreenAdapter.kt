package kr.ac.kpu.green_us.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.ac.kpu.green_us.MyGreenEndFragment
import kr.ac.kpu.green_us.MyGreenIngFragment
import kr.ac.kpu.green_us.MyGreenOpenFragment

class GreenAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        if(position == 0){
            val fragment = MyGreenIngFragment()
            return fragment;
        }
        else if(position == 1){
            val fragment = MyGreenEndFragment()
            return fragment;
        }
        else{
            val fragment = MyGreenOpenFragment()
            return fragment;
        }
    }
}