package com.erickvazquezs.mantenteencontacto.ui.pagers

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    val fragmentList: ArrayList<Fragment>,
    fm: Fragment
): FragmentStateAdapter(fm) {
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}