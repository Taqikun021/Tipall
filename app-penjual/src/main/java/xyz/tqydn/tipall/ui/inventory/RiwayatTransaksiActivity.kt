package xyz.tqydn.tipall.ui.inventory

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.ActivityRiwayatTransaksiBinding
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.BUAT_TRANSAKSI
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.BuatTransaksiContract

class RiwayatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var binding: ActivityRiwayatTransaksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        showTransaksi()
        binding.buatTransaksi.setOnClickListener {
            regbuatTransaksi.launch(BUAT_TRANSAKSI)
        }
    }

    private fun showTransaksi() {
        val call: Call<TransaksiItem> = Constants.apiInterface.getDetailTransaksi(preference.getValues("trans_click"))
        call.enqueue(object : Callback<TransaksiItem> {
            @SuppressLint("SetTextI18n", "ResourceAsColor")
            override fun onResponse(call: Call<TransaksiItem>, response: Response<TransaksiItem>) {
                val item: TransaksiItem? = response.body()
                val imgProfil = Uri.parse(item?.foto)
                val imgUsaha = Uri.parse(item?.foto_usaha)
                val imgBarang = Uri.parse(item?.foto_barang)
                Glide.with(this@RiwayatTransaksiActivity)
                    .load(imgProfil)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imagePenjual)
                Glide.with(this@RiwayatTransaksiActivity)
                    .load(imgUsaha)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.imageUsaha)
                Glide.with(this@RiwayatTransaksiActivity)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.barang)
                binding.kodeTransaksi.text = item?.kode_transaksi
                binding.tanggal.text = item?.waktu_mulai
                binding.namaUsaha.text = item?.nama_usaha
                binding.alamat.text = item?.alamat
                binding.namaBarang.text = item?.nama_barang
                binding.descBarang.text = item?.deskripsi_produk
                binding.jumlahBarang.text = "${item?.jumlah_barang} Item"
                binding.totalHarga.text = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
                binding.ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                binding.status.text = item.status_transaksi
                if (item.jenis_kelamin == "Perempuan") {
                    binding.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    binding.namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@RiwayatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@RiwayatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private val regbuatTransaksi = registerForActivityResult(BuatTransaksiContract()){
        if (it != null){
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}