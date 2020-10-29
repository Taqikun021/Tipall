package xyz.tqydn.tipang.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_penjual.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.PenjualAdapter
import xyz.tqydn.tipang.model.DataPenjual
import xyz.tqydn.tipang.model.Penjual
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.Constants.Companion.BUAT_TRANSAKSI
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.BuatTransaksiContract

class PenjualFragment : Fragment() {

    lateinit var preference: SharedPreference

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
                }
            }

            override fun onFailure(call: Call<Penjual>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showPenjual(penjual: List<DataPenjual>) {
        val penjualAdapter = PenjualAdapter(penjual)
        rvPenjual.layoutManager = LinearLayoutManager(requireContext())
        rvPenjual.adapter = penjualAdapter
        penjualAdapter.setOnItemClickCallback(object : PenjualAdapter.OnItemClickCallback {
            override fun onItemClicked(penjual: DataPenjual) {
                buatTransaksi.launch(BUAT_TRANSAKSI)
            }
        })
    }

    private val buatTransaksi = registerForActivityResult(BuatTransaksiContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_penjual, container, false)
    }
}