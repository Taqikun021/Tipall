package xyz.tqydn.tipang.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_stok.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.BarangAdapter
import xyz.tqydn.tipang.model.Barang
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.EditBarangContract
import xyz.tqydn.tipang.utils.contracts.TambahBarangContract

class StokFragment : Fragment() {

    private lateinit var preference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        fetchStok()
        toolbar.setOnMenuItemClickListener{ menu: MenuItem? ->
            when(menu?.itemId){
                R.id.tambahBarang -> {
                    tambahBarangActivity.launch(Constants.TAMBAH_BARANG)
                    true
                } else -> false
            }
        }
    }

    private fun fetchStok() {
        val call: Call<Barang> = Constants.apiInterface.getBarang(preference.getValues("id_distributor"))
        call.enqueue(object : Callback<Barang> {
            override fun onResponse(call: Call<Barang>, response: Response<Barang>) {
                val item = response.body()?.dataBarang
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showStok(it)
                    }
                } else {
                    rvBarang.visibility = View.GONE
                    kosong.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Barang>, t: Throwable) {
                rvBarang.visibility = View.GONE
                kosong.visibility = View.VISIBLE
                iv.setImageResource(R.drawable.ic_ilustrasi_eror)
                tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
            }
        })
    }

    private fun showStok(stok: List<DataBarang>) {
        val barangAdapter = BarangAdapter(stok)
        rvBarang.layoutManager = LinearLayoutManager(requireContext())
        rvBarang.adapter = barangAdapter
        barangAdapter.setOnItemClickCallback(object : BarangAdapter.OnItemClickCallback {
            override fun onItemClicked(stok: DataBarang) {
                editBarangActivity.launch(Constants.EDIT_BARANG)
            }
        })
    }

    private val tambahBarangActivity = registerForActivityResult(TambahBarangContract()) {
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchStok()
    }

    private val editBarangActivity = registerForActivityResult(EditBarangContract()) {
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchStok()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stok, container, false)
    }
}