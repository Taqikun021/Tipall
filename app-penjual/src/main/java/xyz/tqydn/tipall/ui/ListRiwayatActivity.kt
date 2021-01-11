package xyz.tqydn.tipall.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.adapter.RiwayatAdapter
import xyz.tqydn.tipall.databinding.ActivityListRiwayatBinding
import xyz.tqydn.tipall.model.Transaksi
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.RIWAYAT_TRANSAKSI
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.RiwayatTransaksiContract

class ListRiwayatActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var binding: ActivityListRiwayatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        val id = preference.getValues("id_penjual")
        fetchTransaksi(id)
        binding.refresh.setOnRefreshListener {
            fetchTransaksi(id)
            binding.kosong.visibility = View.GONE
            binding.rvRiwayat.visibility = View.VISIBLE
            binding.refresh.isRefreshing = false
        }
    }

    private fun fetchTransaksi(id: String?) {
        binding.loading.visibility = View.VISIBLE
        val call: Call<Transaksi> = apiInterface.getListTransaksiLunas(1, id)
        call.enqueue(object : Callback<Transaksi> {
            override fun onResponse(call: Call<Transaksi>, response: Response<Transaksi>) {
                val item = response.body()?.transaksi
                binding.loading.visibility = View.GONE
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showTransaksi(it)
                    }
                } else {
                    binding.rvRiwayat.visibility = View.GONE
                    binding.kosong.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                binding.loading.visibility = View.GONE
                binding.rvRiwayat.visibility = View.GONE
                binding.kosong.visibility = View.VISIBLE
                binding.iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                binding.tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = RiwayatAdapter(list)
        binding.rvRiwayat.adapter = items
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        items.setOnItemClickCallback(object: RiwayatAdapter.OnItemClickCallback{
            override fun onItemClicked(item: TransaksiItem) {
                riwayatTransaksi.launch(RIWAYAT_TRANSAKSI)
            }
        })
    }

    private val riwayatTransaksi = registerForActivityResult(RiwayatTransaksiContract()){
        if (it != null){
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchTransaksi(preference.getValues("id_distributor"))
    }
}