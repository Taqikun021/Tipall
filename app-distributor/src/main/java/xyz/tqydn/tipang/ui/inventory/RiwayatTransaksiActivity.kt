package xyz.tqydn.tipang.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_riwayat_transaksi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.Constants.Companion.BUAT_TRANSAKSI
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.BuatTransaksiContract

class RiwayatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_transaksi)
        preference = SharedPreference(this)
        showTransaksi()
        buatTransaksi.setOnClickListener {
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
                    .into(imagePenjual)
                Glide.with(this@RiwayatTransaksiActivity)
                    .load(imgUsaha)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageUsaha)
                Glide.with(this@RiwayatTransaksiActivity)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(barang)
                kode_transaksi.text = item?.kode_transaksi
                tanggal.text = item?.waktu_mulai
                namaUsaha.text = item?.nama_usaha
                alamat.text = item?.alamat
                namaBarang.text = item?.nama_barang
                descBarang.text = item?.deskripsi_produk
                jumlahBarang.text = "${item?.jumlah_barang} Item"
                totalHarga.text = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                status.text = item.status_transaksi
                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
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
            val intent = Intent().apply {
                putExtra(Constants.TITLE, it.toString())
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}