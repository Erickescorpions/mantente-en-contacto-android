package com.erickvazquezs.mantenteencontacto.ui.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.erickvazquezs.mantenteencontacto.Extensions.dataStore
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentMainOnboardingBinding
import com.erickvazquezs.mantenteencontacto.ui.pagers.OnboardingPagerAdapter
import com.erickvazquezs.mantenteencontacto.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
            val viewPager = binding.pager
            val lastPageIndex = onboardingPagerAdapter.itemCount - 1

            binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (position == lastPageIndex) {
                        binding.llBtnControls.visibility = View.INVISIBLE
                    } else {
                        binding.llBtnControls.visibility = View.VISIBLE
                    }
                }
            })

            binding.btnNext.setOnClickListener {
                val currentItem = viewPager.currentItem
                if (currentItem < lastPageIndex) {
                    viewPager.setCurrentItem(currentItem + 1, true)
                }
            }

            binding.btnSkip.setOnClickListener {
                viewPager.setCurrentItem(lastPageIndex, true)
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            _binding = FragmentMainOnboardingBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }