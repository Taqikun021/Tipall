package xyz.tqydn.tipang.ui.fragment

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
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.PermintaanAdapter
import xyz.tqydn.tipang.model.Transaksi
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants.Companion.DETAIL_PERMINTAAN
import xyz.tqydn.tipang.utils.Constants.Companion.status5
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.DetailPermintaanContract

class PermintaanFragment : Fragment() {

    private lateinit var preference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        val id = preference.getValues("id_distributor")
        fetchTransaksi(id)
    }

    private fun fetchTransaksi(id: String?) {
        val call: Call<Transaksi> = apiInterface.getListTransaksi(status5, id)
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
            override fun onItemClicked(item: TransaksiItem) {
                detailPermintaan.launch(DETAIL_PERMINTAAN)
            }
        })
    }

    private val detailPermintaan = registerForActivityResult(DetailPermintaanContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchTransaksi(preference.getValues("id_distributor"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permintaan, container, false)
    }
}