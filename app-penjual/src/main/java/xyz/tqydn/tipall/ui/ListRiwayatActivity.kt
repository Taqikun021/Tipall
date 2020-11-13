package xyz.tqydn.tipall.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_riwayat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.adapter.RiwayatAdapter
import xyz.tqydn.tipall.model.Transaksi
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.RIWAYAT_TRANSAKSI
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.RiwayatTransaksiContract

class ListRiwayatActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_riwayat)
        preference = SharedPreference(this)
        val id = preference.getValues("id_penjual")
        fetchTransaksi(id)
    }

    private fun fetchTransaksi(id: String?) {
        val call: Call<Transaksi> = apiInterface.getListTransaksiLunas(1, id)
        call.enqueue(object : Callback<Transaksi> {
            override fun onResponse(call: Call<Transaksi>, response: Response<Transaksi>) {
                val item = response.body()?.transaksi
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showTransaksi(it)
                    }
                } else {
                    rvRiwayat.visibility = View.GONE
                    kosong.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                rvRiwayat.visibility = View.GONE
                kosong.visibility = View.VISIBLE
                iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = RiwayatAdapter(list)
        rvRiwayat.adapter = items
        rvRiwayat.layoutManager = LinearLayoutManager(this)
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