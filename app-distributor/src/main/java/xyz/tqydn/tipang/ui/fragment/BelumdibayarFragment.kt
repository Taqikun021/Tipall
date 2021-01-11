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
import xyz.tqydn.tipang.adapter.BelumdibayarAdapter
import xyz.tqydn.tipang.databinding.FragmentBelumdibayarBinding
import xyz.tqydn.tipang.model.Transaksi
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.DetailHutangContract

@SuppressLint("SetTextI18n")
class BelumdibayarFragment : Fragment() {

    private lateinit var preference: SharedPreference
    private var _binding: FragmentBelumdibayarBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        val id = preference.getValues("id_distributor")
        fetchTransaksi(id)
        binding.refresh.setOnRefreshListener {
            fetchTransaksi(id)
            binding.kosong.visibility = View.GONE
            binding.rv.visibility = View.VISIBLE
            binding.refresh.isRefreshing = false
        }
    }

    private fun fetchTransaksi(id: String?) {
        binding.loading.visibility = View.VISIBLE
        val call: Call<Transaksi> = apiInterface.getListTransaksiLunas(0, id)
        call.enqueue(object : Callback<Transaksi> {
            override fun onResponse(call: Call<Transaksi>, response: Response<Transaksi>) {
                val item = response.body()?.transaksi
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        binding.loading.visibility = View.GONE
                        showTransaksi(it)
                    }
                } else {
                    binding.loading.visibility = View.GONE
                    binding.rv.visibility = View.GONE
                    binding.kosong.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                binding.loading.visibility = View.GONE
                binding.rv.visibility = View.GONE
                binding.kosong.visibility = View.VISIBLE
                binding.iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                binding.tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = BelumdibayarAdapter(list)
        binding.rv.adapter = items
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        items.setOnItemClickCallback(object: BelumdibayarAdapter.OnItemClickCallback{
            override fun onItemClicked(item: TransaksiItem) {
                detailBelumdibayar.launch(Constants.DETAIL_HUTANG)
            }
        })
    }

    private val detailBelumdibayar = registerForActivityResult(DetailHutangContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchTransaksi(preference.getValues("id_distributor"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBelumdibayarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}