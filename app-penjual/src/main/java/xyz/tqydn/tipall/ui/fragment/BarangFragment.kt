package xyz.tqydn.tipall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.adapter.BarangAdapter
import xyz.tqydn.tipall.databinding.FragmentBarangBinding
import xyz.tqydn.tipall.model.Barang
import xyz.tqydn.tipall.model.DataBarang
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.BUAT_TRANSAKSI
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.BuatTransaksiContract

class BarangFragment : Fragment() {

    private lateinit var preference: SharedPreference
    private var _binding: FragmentBarangBinding? = null
    private val binding get() = _binding!!

    private val buatTransaksi = registerForActivityResult(BuatTransaksiContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchBarang()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        fetchBarang()
        binding.refresh.setOnRefreshListener {
            fetchBarang()
            binding.kosong.visibility = View.GONE
            binding.rvBarang.visibility = View.VISIBLE
            binding.refresh.isRefreshing = false
        }

    }

    private fun fetchBarang() {
        binding.loading.visibility = View.VISIBLE
        val call: Call<Barang> = Constants.apiInterface.getBarang()
        call.enqueue(object : Callback<Barang> {
            override fun onResponse(call: Call<Barang>, response: Response<Barang>) {
                val item = response.body()?.dataBarang
                binding.loading.visibility = View.GONE
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showBarang(it)
                    }
                } else {
                    binding.rvBarang.visibility = View.GONE
                    binding.kosong.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Barang>, t: Throwable) {
                binding.loading.visibility = View.GONE
                binding.rvBarang.visibility = View.GONE
                binding.kosong.visibility = View.VISIBLE
                binding.iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                binding.tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showBarang(barang: List<DataBarang>) {
        val barangAdapter = BarangAdapter(barang)
        binding.rvBarang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarang.adapter = barangAdapter
        barangAdapter.setOnItemClickCallback(object : BarangAdapter.OnItemClickCallback {
            override fun onItemClicked(barang: DataBarang) {
                buatTransaksi.launch(BUAT_TRANSAKSI)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}