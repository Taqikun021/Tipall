package xyz.tqydn.tipang.ui.fragment

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
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.PenjualAdapter
import xyz.tqydn.tipang.databinding.FragmentPenjualBinding
import xyz.tqydn.tipang.model.DataPenjual
import xyz.tqydn.tipang.model.Penjual
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.BuatTransaksiContract

@SuppressLint("SetTextI18n")
class PenjualFragment : Fragment() {

    lateinit var preference: SharedPreference
    private var _binding: FragmentPenjualBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        fetchRow()
    }

    private fun fetchRow() {
        val call: Call<Penjual> = Constants.apiInterface.getPenjual()
        call.enqueue(object : Callback<Penjual> {
            override fun onResponse(call: Call<Penjual>, response: Response<Penjual>) {
                val item = response.body()?.dataPenjual
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showPenjual(it)
                    }
                } else {
                    binding.rvPenjual.visibility = View.GONE
                    binding.kosong.visibility = View.VISIBLE
                }
            }


            override fun onFailure(call: Call<Penjual>, t: Throwable) {
                binding.rvPenjual.visibility = View.GONE
                binding.kosong.visibility = View.VISIBLE
                binding.iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                binding.tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showPenjual(penjual: List<DataPenjual>) {
        val penjualAdapter = PenjualAdapter(penjual)
        binding.rvPenjual.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPenjual.adapter = penjualAdapter
        penjualAdapter.setOnItemClickCallback(object : PenjualAdapter.OnItemClickCallback {
            override fun onItemClicked(penjual: DataPenjual) {
                buatTransaksi.launch(Constants.BUAT_TRANSAKSI)
            }
        })
    }

    private val buatTransaksi = registerForActivityResult(BuatTransaksiContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchRow()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPenjualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}