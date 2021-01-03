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
import xyz.tqydn.tipang.adapter.TawaranAdapter
import xyz.tqydn.tipang.databinding.FragmentTawaranBinding
import xyz.tqydn.tipang.model.Transaksi
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.DetailTawaranContract

class TawaranFragment : Fragment() {

    private lateinit var preference: SharedPreference
    private var _binding: FragmentTawaranBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        val id = preference.getValues("id_distributor")
        fetchTransaksi(id)
    }

    private fun fetchTransaksi(id: String?) {
        val call: Call<Transaksi> = Constants.apiInterface.getListTransaksi(Constants.status1, id)
        call.enqueue(object : Callback<Transaksi> {
            override fun onResponse(call: Call<Transaksi>, response: Response<Transaksi>) {
                val item = response.body()?.transaksi
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showTransaksi(it)
                    }
                } else {
                    binding.rv.visibility = View.GONE
                    binding.kosong.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                binding.rv.visibility = View.GONE
                binding.kosong.visibility = View.VISIBLE
                binding.iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                binding.tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = TawaranAdapter(list)
        binding.rv.adapter = items
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        items.setOnItemClickCallback(object: TawaranAdapter.OnItemClickCallback{
            override fun onItemClicked(transaksi: TransaksiItem) {
                detailTawaran.launch(Constants.DETAIL_TAWARAN)
            }
        })
    }

    private val detailTawaran = registerForActivityResult(DetailTawaranContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchTransaksi(preference.getValues("id_distributor"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTawaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}