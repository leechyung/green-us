package kr.ac.kpu.green_us

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kr.ac.kpu.green_us.adapter.HomeTabAdapter
import kr.ac.kpu.green_us.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var homeTabAdapter: HomeTabAdapter
    private lateinit var viewPager: ViewPager2
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeTabAdapter = HomeTabAdapter(this)
        viewPager = binding.homeTabViewPager
        viewPager.setUserInputEnabled(false)
        viewPager.adapter = homeTabAdapter

        val tabLayout = binding.homeTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(position == 0){
                tab.text = "홈"
            }
            else if(position == 1){
                tab.text = "인기"
            }
            else{
                tab.text = "신규"
            }
        }.attach()
    }


}