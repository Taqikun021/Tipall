package xyz.tqydn.tipang.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_list_riwayat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.RiwayatAdapter
import xyz.tqydn.tipang.model.Transaksi
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.RiwayatTransaksiContract

class ListRiwayatActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_riwayat)
        preference = SharedPreference(this)
        val id = preference.getValues("id_distributor")
        fetchTransaksi(id)
    }

    private fun fetchTransaksi(id: String?) {
        val call: Call<Transaksi> = Constants.apiInterface.getListTransaksiLunas(1, id)
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

            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@ListRiwayatActivity).create()
                val inflater = LayoutInflater.from(this@ListRiwayatActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = RiwayatAdapter(list)
        rvRiwayat.adapter = items
        rvRiwayat.layoutManager = LinearLayoutManager(this)
        items.setOnItemClickCallback(object: RiwayatAdapter.OnItemClickCallback{
            override fun onItemClicked(item: TransaksiItem) {
                riwayatTransaksi.launch(Constants.RIWAYAT_TRANSAKSI)
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