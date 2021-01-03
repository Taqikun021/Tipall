package xyz.tqydn.tipall.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import xyz.tqydn.tipall.adapter.TransaksiPagerAdapter
import xyz.tqydn.tipall.databinding.FragmentTransaksiBinding

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = listOf(
            PermintaanFragment(),
            TawaranFragment(),
            BerlangsungFragment(),
            BelumdibayarFragment()
        )
        binding.viewPager.adapter = TransaksiPagerAdapter(rv, requireActivity().supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tab, binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "Permintaan Saya"
                1 -> tab.text = "Tawaran"
                2 -> tab.text = "Berlangsung"
                else -> tab.text = "Belum Dibayar"
            }
        }.attach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}