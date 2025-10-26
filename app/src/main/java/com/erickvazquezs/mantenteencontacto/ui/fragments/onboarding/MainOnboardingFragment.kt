package com.erickvazquezs.mantenteencontacto.ui.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.erickvazquezs.mantenteencontacto.databinding.FragmentMainOnboardingBinding
import com.erickvazquezs.mantenteencontacto.ui.pagers.OnboardingPagerAdapter

class MainOnboardingFragment : Fragment() {
    private var _binding: FragmentMainOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var onboardingPagerAdapter: OnboardingPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf(WelcomeFragment(), PlacesAndContactsFragment(), PrivacyFragment(),
            GetStartedFragment())
        val dotsIndicator = binding.dots

        onboardingPagerAdapter = OnboardingPagerAdapter(fragmentList, this)
        binding.pager.adapter = onboardingPagerAdapter
        dotsIndicator.attachTo(binding.pager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }
}