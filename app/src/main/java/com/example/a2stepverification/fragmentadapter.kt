package com.example.a2stepverification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class fragmentadapter(fm:FragmentActivity):FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 ->FirstFragment()
            1 -> secondFragment()
            else -> FirstFragment()
        }
    }

}