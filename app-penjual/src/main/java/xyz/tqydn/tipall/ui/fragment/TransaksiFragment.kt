package xyz.tqydn.tipall.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_transaksi.*
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.adapter.TransaksiPagerAdapter

class TransaksiFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = listOf(
            PermintaanFragment(),
            TawaranFragment(),
            BerlangsungFragment(),
            BelumdibayarFragment()
        )
        viewPager.adapter = TransaksiPagerAdapter(rv, requireActivity().supportFragmentManager, lifecycle)
        TabLayoutMediator(tab, viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "Permintaan Saya"
                1 -> tab.text = "Tawaran"
                2 -> tab.text = "Berlangsung"
                else -> tab.text = "Belum Dibayar"
            }
        }.attach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaksi, container, false)
    }
}