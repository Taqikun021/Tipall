package xyz.tqydn.tipall.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_barang.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.adapter.BarangAdapter
import xyz.tqydn.tipall.model.Barang
import xyz.tqydn.tipall.model.DataBarang
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.BUAT_TRANSAKSI
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.BuatTransaksiContract

class BarangFragment : Fragment() {

    private lateinit var preference: SharedPreference

    private val buatTransaksi = registerForActivityResult(BuatTransaksiContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        fetchBarang()
    }

    private fun fetchBarang() {
        val call: Call<Barang> = Constants.apiInterface.getBarang()
        call.enqueue(object : Callback<Barang> {
            override fun onResponse(call: Call<Barang>, response: Response<Barang>) {
                val item = response.body()?.dataBarang
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showBarang(it)
                    }
                }
            }

            override fun onFailure(call: Call<Barang>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBarang(barang: List<DataBarang>) {
        val barangAdapter = BarangAdapter(barang)
        rvBarang.layoutManager = LinearLayoutManager(requireContext())
        rvBarang.adapter = barangAdapter
        barangAdapter.setOnItemClickCallback(object : BarangAdapter.OnItemClickCallback {
            override fun onItemClicked(barang: DataBarang) {
                buatTransaksi.launch(BUAT_TRANSAKSI)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barang, container, false)
    }
}