package xyz.tqydn.tipall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_permintaan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.adapter.PermintaanAdapter
import xyz.tqydn.tipall.model.Transaksi
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.DETAIL_PERMINTAAN
import xyz.tqydn.tipall.utils.Constants.Companion.status1
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.DetailPermintaanContract

class PermintaanFragment : Fragment() {

    private lateinit var preference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        val id = preference.getValues("id_penjual")
        fetchTransaksi(id)
    }

    private fun fetchTransaksi(id: String?) {
        val call: Call<Transaksi> = Constants.apiInterface.getListTransaksi(status1, id)
        call.enqueue(object : Callback<Transaksi> {
            override fun onResponse(call: Call<Transaksi>, response: Response<Transaksi>) {
                val item = response.body()?.transaksi
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showTransaksi(it)
                    }
                } else {
                    rv.visibility = View.GONE
                    kosong.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                rv.visibility = View.GONE
                kosong.visibility = View.VISIBLE
                iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = PermintaanAdapter(list)
        rv.adapter = items
        rv.layoutManager = LinearLayoutManager(requireContext())
        items.setOnItemClickCallback(object: PermintaanAdapter.OnItemClickCallback{
            override fun onItemClicked(transaksi: TransaksiItem) {
                detailPermintaan.launch(DETAIL_PERMINTAAN)
            }
        })
    }

    private val detailPermintaan = registerForActivityResult(DetailPermintaanContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchTransaksi(preference.getValues("id_penjual"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permintaan, container, false)
    }
}