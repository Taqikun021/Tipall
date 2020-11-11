package xyz.tqydn.tipang.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_tawaran.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.TawaranAdapter
import xyz.tqydn.tipang.model.Transaksi
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants.Companion.DETAIL_TAWARAN
import xyz.tqydn.tipang.utils.Constants.Companion.status1
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.DetailTawaranContract

class TawaranFragment : Fragment() {

    private lateinit var preference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        val id = preference.getValues("id_distributor")
        fetchTransaksi(id)
    }

    private fun fetchTransaksi(id: String?) {
        val call: Call<Transaksi> = apiInterface.getListTransaksi(status1, id)
        call.enqueue(object : Callback<Transaksi> {
            override fun onResponse(call: Call<Transaksi>, response: Response<Transaksi>) {
                val item = response.body()?.transaksi
                if (response.body()?.jumlahData!! > 0) {
                    item?.let {
                        showTransaksi(it)
                    }
                }
            }

            override fun onFailure(call: Call<Transaksi>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showTransaksi(list: List<TransaksiItem?>) {
        val items = TawaranAdapter(list)
        rv.adapter = items
        rv.layoutManager = LinearLayoutManager(requireContext())
        items.setOnItemClickCallback(object: TawaranAdapter.OnItemClickCallback{
            override fun onItemClicked(transaksi: TransaksiItem) {
                detailTawaran.launch(DETAIL_TAWARAN)
            }
        })
    }

    private val detailTawaran = registerForActivityResult(DetailTawaranContract()){
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        fetchTransaksi(preference.getValues("id_distributor"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tawaran, container, false)
    }
}