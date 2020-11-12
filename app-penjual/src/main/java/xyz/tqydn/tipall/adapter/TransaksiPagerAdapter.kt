package xyz.tqydn.tipall.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TransaksiPagerAdapter(private val items: List<Fragment>, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position]
}