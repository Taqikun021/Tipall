package xyz.tqydn.tipang.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_detail_berlangsung.*
import kotlinx.android.synthetic.main.activity_detail_hutang.*
import kotlinx.android.synthetic.main.activity_detail_hutang.alamat
import kotlinx.android.synthetic.main.activity_detail_hutang.barang
import kotlinx.android.synthetic.main.activity_detail_hutang.descBarang
import kotlinx.android.synthetic.main.activity_detail_hutang.imagePenjual
import kotlinx.android.synthetic.main.activity_detail_hutang.imageUsaha
import kotlinx.android.synthetic.main.activity_detail_hutang.jumlahBarang
import kotlinx.android.synthetic.main.activity_detail_hutang.keMaps
import kotlinx.android.synthetic.main.activity_detail_hutang.keWA
import kotlinx.android.synthetic.main.activity_detail_hutang.kode_transaksi
import kotlinx.android.synthetic.main.activity_detail_hutang.namaBarang
import kotlinx.android.synthetic.main.activity_detail_hutang.namaPemilik
import kotlinx.android.synthetic.main.activity_detail_hutang.namaUsaha
import kotlinx.android.synthetic.main.activity_detail_hutang.nomorHP
import kotlinx.android.synthetic.main.activity_detail_hutang.ratingPenjual
import kotlinx.android.synthetic.main.activity_detail_hutang.tandaiLunas
import kotlinx.android.synthetic.main.activity_detail_hutang.tanggal
import kotlinx.android.synthetic.main.activity_detail_hutang.totalHarga
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference

class DetailHutangActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hutang)
        preference = SharedPreference(this)

        showTransaksi()
        keMaps.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://google.co.id/maps/dir/${dari}/${ke}"))
            )
        }
        keWA.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${telepon}"))
            )
        }
        tandaiLunas.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this).create()
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.alert_lunas, null)
            alertDialog.setCancelable(true)
            alertDialog.setView(dialogView)
            val yakin = dialogView.findViewById(R.id.yakin) as Button
            val batal = dialogView.findViewById(R.id.batal) as Button
            yakin.setOnClickListener {
                tandaiLunas(preference.getValues("trans_click"))
                alertDialog.dismiss()
                showTransaksi()
            }

            batal.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun tandaiLunas(id: String?) {
        val call: Call<DefaultResponse> = Constants.apiInterface.updateStatusTransaksi(1, status,id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                Toast.makeText(this@DetailHutangActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                val intent = Intent().apply {
                    putExtra(Constants.TITLE, response.body()?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@DetailHutangActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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
                dari = "${preference.getValues("lat")},${preference.getValues("long")}"
                ke = "${item?.lat},${item?.lng}"
                telepon = "62${item?.no_hp}"
                status = item?.status_transaksi.toString()

                Glide.with(this@DetailHutangActivity)
                    .load(imgProfil)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imagePenjual)
                Glide.with(this@DetailHutangActivity)
                    .load(imgUsaha)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageUsaha)
                Glide.with(this@DetailHutangActivity)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(barang)

                kode_transaksi.text = item?.kode_transaksi
                tanggal.text = item?.waktu_mulai
                namaUsaha.text = item?.nama_usaha
                alamat.text = item?.alamat
                nomorHP.text = "+62 ${item?.no_hp}"
                namaBarang.text = item?.nama_barang
                descBarang.text = item?.deskripsi_produk
                jumlahBarang.text = "${item?.jumlah_barang} Item"
                totalHarga.text = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                Toast.makeText(this@DetailHutangActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}